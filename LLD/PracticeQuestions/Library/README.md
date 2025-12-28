# ☕ Java LLD Starter Template for Library Management System

> **Goal:** Never start from a blank screen in an LLD interview again.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements
2.  Identify entities
3.  Identify what changes
4.  Plug into these templates
5.  Explain trade-offs

📌 These templates are **intentionally minimal** — extensibility > completeness.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.library
 ├── domain        // entities & value objects (e.g., Book, Member, Loan)
 ├── service       // business logic (e.g., LibraryService)
 ├── strategy      // pluggable behaviors (e.g., FineCalculationStrategy)
 ├── repository    // storage abstractions (e.g., BookRepository, MemberRepository)
 ├── factory       // object creation (e.g., MemberFactory)
 └── api           // public interfaces / controllers
```

📌 Interview tip: *Say this structure out loud* — it shows maturity.

---

## 2️⃣ The `Entity` Template (The "Noun" Objects)

```java
// Common interface for all domain models
public abstract class BaseEntity {
    private String id;
    // getters, setters, equals, hashCode
}

// Example: A book in the library
public class Book extends BaseEntity {
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for managing the library
public class LibraryService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final FineCalculationStrategy fineStrategy;

    public LibraryService(BookRepository bRepo, MemberRepository mRepo, FineCalculationStrategy strategy) {
        this.bookRepository = bRepo;
        this.memberRepository = mRepo;
        this.fineStrategy = strategy;
    }

    public Loan borrowBook(Member member, Book book) {
        // ... check availability, create loan, etc.
    }

    public double returnBook(Loan loan) {
        // ... calculate fine, mark book as available, etc.
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable fine calculation logic
public interface FineCalculationStrategy {
    double calculateFine(Loan loan);
}

// Example: Different strategies for different member types
public class RegularMemberFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(Loan loan) {
        // ... logic for regular members
    }
}

public class StudentFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(Loan loan) {
        // ... logic for student members
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface BookRepository {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByAuthor(String author);
    void save(Book book);
}

// In-memory implementation for interviews
public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> books = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of members
public class MemberFactory {
    public static Member createMember(MemberType type, String name) {
        switch (type) {
            case REGULAR:
                return new RegularMember(name);
            case STUDENT:
                return new StudentMember(name);
            default:
                throw new IllegalArgumentException("Unknown member type");
        }
    }
}
```
