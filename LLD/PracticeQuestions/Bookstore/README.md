# ☕ Java LLD Starter Template for Online Bookstore

> **Goal:** Design a high-performance, concurrent online bookstore, ensuring data consistency for inventory and orders.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., payment processing, inventory management, user reviews).
2.  Identify entities (`Book`, `User`, `Order`, `Inventory`).
3.  **Identify Shared, Mutable State** (Critical: `Inventory` levels).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., `AtomicInteger` vs. database locks for inventory).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.bookstore
 ├── domain        // Entities like Book, Order, and the critical Inventory.
 ├── service       // Business logic with clear concurrency controls for ordering.
 ├── strategy      // Pluggable behaviors (e.g., DiscountStrategy).
 ├── repository    // Thread-safe data access abstractions.
 ├── factory       // Object creation (e.g., OrderFactory).
 └── api           // Controllers handling concurrent user requests.
```

---

## 2️⃣ The `Entity` Template (Managing Mutable Inventory)

`Book` can be immutable, but `Inventory` is a classic shared, mutable resource.

```java
// Book details are fixed, so it can be immutable and inherently thread-safe.
public final class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final double price;
    // constructor, getters
}

// Inventory for a book is the critical shared state.
public class Inventory {
    private final Book book;
    // AtomicInteger is a lock-free, thread-safe way to handle counters.
    private final AtomicInteger stock;

    public Inventory(Book book, int initialStock) {
        this.book = book;
        this.stock = new AtomicInteger(initialStock);
    }

    // Atomically decrements the stock if sufficient quantity is available.
    public boolean reserveStock(int quantity) {
        while (true) {
            int currentStock = stock.get();
            if (currentStock < quantity) {
                return false; // Not enough stock
            }
            // Attempt to atomically update the stock.
            if (stock.compareAndSet(currentStock, currentStock - quantity)) {
                return true; // Success
            }
            // If compareAndSet failed, another thread intervened. Loop and retry.
        }
    }

    public void releaseStock(int quantity) {
        stock.addAndGet(quantity); // Atomically add back to stock.
    }

    public int getAvailableStock() {
        return stock.get();
    }
}
```

---

## 3️⃣ The `Service` Template (Handling Concurrent Orders)

 The `OrderService` must ensure that inventory is reserved atomically before an order is confirmed.

```java
// OrderService handles the business logic of placing an order.
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(OrderRepository oRepo, InventoryRepository iRepo) {
        this.orderRepository = oRepo;
        this.inventoryRepository = iRepo;
    }

    public Optional<Order> placeOrder(User user, Map<Book, Integer> items) {
        // Step 1: Attempt to reserve stock for all items.
        boolean allItemsReserved = true;
        List<Inventory> affectedInventories = new ArrayList<>();

        for (Map.Entry<Book, Integer> entry : items.entrySet()) {
            Book book = entry.getKey();
            int quantity = entry.getValue();
            Optional<Inventory> inventoryOpt = inventoryRepository.findByBook(book);

            if (inventoryOpt.isPresent() && inventoryOpt.get().reserveStock(quantity)) {
                affectedInventories.add(inventoryOpt.get());
            } else {
                allItemsReserved = false;
                break; // Failed to reserve one item, so stop.
            }
        }

        // Step 2: If any reservation failed, roll back all previous reservations.
        if (!allItemsReserved) {
            for (int i = 0; i < affectedInventories.size(); i++) {
                Inventory inventory = affectedInventories.get(i);
                // Find the original requested quantity for the rollback.
                int quantityToRelease = items.get(inventory.getBook());
                inventory.releaseStock(quantityToRelease);
            }
            return Optional.empty(); // Order failed.
        }

        // Step 3: If all reservations succeeded, create and save the order.
        Order order = new Order(user, items);
        orderRepository.save(order);
        return Optional.of(order);
    }
}
```

---

## 4️⃣ The `Repository` Template (Thread-Safe Storage)

Repositories must provide safe access to the shared inventory data.

```java
// Abstraction for inventory storage.
public interface InventoryRepository {
    Optional<Inventory> findByBook(Book book);
    void save(Inventory inventory);
}

// In-memory implementation using a ConcurrentHashMap for thread-safe lookups.
public class InMemoryInventoryRepository implements InventoryRepository {
    // Map from ISBN to its inventory record.
    private final Map<String, Inventory> inventoryByIsbn = new ConcurrentHashMap<>();

    @Override
    public Optional<Inventory> findByBook(Book book) {
        return Optional.ofNullable(inventoryByIsbn.get(book.getIsbn()));
    }

    @Override
    public void save(Inventory inventory) {
        inventoryByIsbn.put(inventory.getBook().getIsbn(), inventory);
    }
}
```

---

## 5️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The number of available copies for each book—the inventory stock—is the most critical shared and mutable state. Multiple users trying to buy the last copy of a book is a classic race condition."
*   **Choose Primitives:** "I'm using `AtomicInteger` for the stock count. This is a high-performance, lock-free concurrency primitive. It uses a `compare-and-set` (CAS) operation, which is much more efficient than acquiring a lock, especially under high contention."
*   **Explain the Race Condition & Solution:** "If we just read the stock, check if it's sufficient, and then write the new value, we have a race condition. Between our read and write, another thread could have bought the same book. The `compareAndSet` loop in the `reserveStock` method solves this. It ensures the stock is only updated if it hasn't changed since we first read it. If it has changed, we retry the whole operation."
*   **Discuss Transactionality:** "The `placeOrder` method implements a simple form of transactional behavior. It first attempts to reserve all necessary resources (inventory). If any reservation fails, it rolls back all previous reservations to ensure data consistency. In a real-world system, this would be handled by a database transaction to guarantee atomicity."
*   **Mention Scalability:** "This design scales well because it's lock-free. Different threads trying to buy different books will never block each other. Even threads buying the same book will only contend for a very short period due to the efficient CAS operations, rather than waiting on a slow lock."