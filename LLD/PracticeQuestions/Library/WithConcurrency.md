# ☕ Java LLD Starter Template for Library Management System

> **Goal:** Design a concurrent and thread-safe library system that correctly handles multiple members borrowing and returning the same book.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., book reservations, fine calculation, member types).
2.  Identify entities (`BookCopy`, `Member`, `Loan`).
3.  **Identify Shared, Mutable State** (The availability of a specific `BookCopy`).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., fine-grained vs. coarse-grained locking on books).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.library
 ├── domain        // Entities, including the mutable BookCopy.
 ├── service       // Business logic with clear concurrency controls.
 ├── strategy      // Pluggable behaviors (e.g., FineCalculationStrategy).
 ├── repository    // Thread-safe data access abstractions.
 ├── factory       // Object creation (e.g., LoanFactory).
 └── api           // Controllers handling concurrent member actions.
```

---

## 2️⃣ The `Entity` Template (Managing Shared Book Copies)

The distinction between a `Book` (the abstract concept) and a `BookCopy` (the physical item) is key to managing concurrency.

```java
// Book (the title, author, etc.) is immutable.
public final class Book {
    private final String isbn;
    private final String title;
    // constructor, getters
}

// BookCopy is the shared, mutable resource that members borrow.
public class BookCopy {
    private final String copyId;
    private final Book book;
    // Use an AtomicReference to manage the loan state atomically.
    private final AtomicReference<Loan> currentLoan = new AtomicReference<>(null);

    public BookCopy(String copyId, Book book) {
        this.copyId = copyId;
        this.book = book;
    }

    // Tries to borrow the book. This is an atomic check-then-act operation.
    public boolean borrow(Loan loan) {
        // Atomically set the new loan IF the current loan is null.
        return currentLoan.compareAndSet(null, loan);
    }

    // Tries to return the book.
    public boolean returnCopy(Loan loan) {
        // Atomically set back to null IF the current loan matches the expected one.
        return currentLoan.compareAndSet(loan, null);
    }

    public boolean isAvailable() {
        return currentLoan.get() == null;
    }
}
```

---

## 3️⃣ The `Service` Template (Handling Concurrent Borrows)

The service layer orchestrates the atomic borrowing and returning of book copies.

```java
// LibraryService handles the core logic of borrowing and returning books.
public class LibraryService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public LibraryService(BookRepository bRepo, LoanRepository lRepo) {
        this.bookRepository = bRepo;
        this.loanRepository = lRepo;
    }

    public Optional<Loan> borrowBook(Member member, Book book) {
        // Find an available physical copy of the book.
        Optional<BookCopy> availableCopy = bookRepository.findAvailableCopy(book);

        // If a copy is found, try to borrow it.
        return availableCopy.flatMap(copy -> {
            Loan newLoan = new Loan(member, copy, LocalDateTime.now());
            // The `borrow` method on BookCopy is atomic.
            if (copy.borrow(newLoan)) {
                loanRepository.save(newLoan);
                return Optional.of(newLoan);
            }
            // If borrow failed, it means another thread just borrowed it. The user can retry.
            return Optional.empty();
        });
    }

    public void returnBook(Loan loan) {
        BookCopy copy = loan.getBookCopy();
        if (copy.returnCopy(loan)) {
            loanRepository.remove(loan);
            // Potentially calculate and apply fines here.
        } else {
            // This would indicate a serious inconsistency, e.g., trying to return a book
            // that is now recorded as being loaned to someone else.
            throw new IllegalStateException("Failed to return book; loan data is inconsistent.");
        }
    }
}
```

---

## 4️⃣ The `Repository` Template (Thread-Safe Storage)

Repositories must provide a thread-safe way to find available copies.

```java
// Abstraction for book and copy storage.
public interface BookRepository {
    Optional<BookCopy> findAvailableCopy(Book book);
    List<BookCopy> findCopiesByBook(Book book);
}

// In-memory implementation designed for concurrent access.
public class InMemoryBookRepository implements BookRepository {
    // A map from ISBN to a list of physical copies of that book.
    // The list of copies for a book does not change after initialization.
    private final Map<String, List<BookCopy>> copiesByIsbn = new ConcurrentHashMap<>();

    public void addBook(Book book, int numberOfCopies) {
        List<BookCopy> copies = new CopyOnWriteArrayList<>();
        for (int i = 0; i < numberOfCopies; i++) {
            copies.add(new BookCopy(UUID.randomUUID().toString(), book));
        }
        copiesByIsbn.put(book.getIsbn(), copies);
    }

    @Override
    public Optional<BookCopy> findAvailableCopy(Book book) {
        List<BookCopy> copies = copiesByIsbn.get(book.getIsbn());
        if (copies == null) {
            return Optional.empty();
        }
        // Stream through the copies and find one that is available.
        // The isAvailable() check is thread-safe.
        return copies.stream().filter(BookCopy::isAvailable).findFirst();
    }

    @Override
    public List<BookCopy> findCopiesByBook(Book book) {
        return copiesByIsbn.getOrDefault(book.getIsbn(), Collections.emptyList());
    }
}
```

---

## 5️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The critical shared and mutable state is not the `Book` itself, but the status of each physical `BookCopy`. Multiple members could try to borrow the last available copy of a popular book at the same time, leading to a race condition."
*   **Choose Primitives:** "I'm using an `AtomicReference` on the `BookCopy` to hold the current `Loan`. This is a lock-free, thread-safe mechanism. The `compareAndSet` operation allows me to atomically check if the book is available (the reference is null) and assign a new loan to it in a single, indivisible step."
*   **Explain the Race Condition & Solution:** "A naive implementation might check `isAvailable()` and then create a loan. But between that check and the update, another thread could swoop in and borrow the same copy. Using `compareAndSet` on `AtomicReference` completely prevents this. It guarantees that the state a thread is basing its action on has not changed in the interim. If it has, the operation fails, and we know someone else got the book."
*   **Discuss Data Structures:** "For storing the lists of book copies, I've used a `ConcurrentHashMap` to map ISBNs to lists, and a `CopyOnWriteArrayList` for the lists themselves. This is optimized for a scenario where the number of copies of a book doesn't change often, but lookups for available copies are very frequent. All read operations (like `findAvailableCopy`) are non-blocking and very fast."
*   **Mention Benefits:** "This lock-free approach using atomic variables is highly scalable. It avoids traditional locks, which can cause thread contention and performance bottlenecks. Different threads trying to borrow different books, or even different copies of the same book, can proceed in parallel without blocking each other."