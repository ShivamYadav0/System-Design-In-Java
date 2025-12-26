# ☕ Java LLD Starter Templates – Interview-Ready README

> **Goal:** Never start from a blank screen in an LLD interview again.
>
> These are **battle-tested Java templates** you can reuse across *Parking Lot, Elevator, Rate Limiter, Splitwise, File System,* etc.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1. Clarify requirements
2. Identify entities
3. Identify what changes
4. Plug into these templates
5. Explain trade-offs

📌 These templates are **intentionally minimal** — extensibility > completeness.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.system
 ├── domain        // entities & value objects
 ├── service       // business logic
 ├── strategy      // pluggable behaviors
 ├── repository    // storage abstractions
 ├── factory       // object creation
 └── api           // public interfaces / controllers
```

📌 Interview tip: *Say this structure out loud* — it shows maturity.

---

## 2️⃣ Base Entity Template (Identity + State)

```java
public abstract class BaseEntity {
    protected final String id;

    protected BaseEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
```

### When to Use

* Objects with identity
* Long-lived domain objects

Examples:

* User, Order, Vehicle, Elevator

---

## 3️⃣ Value Object Template (Immutable)

```java
public final class Money {
    private final double amount;
    private final String currency;

    public Money(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
```

📌 Value objects:

* No identity
* Immutable
* Easy to test

---

## 4️⃣ Service Interface Template (Business Logic)

```java
public interface Service<T> {
    void execute(T request);
}
```

### Example Specialization

```java
public interface PaymentService {
    void pay(PaymentRequest request);
}
```

📌 Services coordinate entities — they do **not** store state.

---

## 5️⃣ Strategy Template (Behavior That Changes)

```java
public interface Strategy {
    void apply();
}
```

### Example

```java
public interface PricingStrategy {
    double calculatePrice(int units);
}
```

```java
public class PeakPricingStrategy implements PricingStrategy {
    public double calculatePrice(int units) {
        return units * 1.5;
    }
}
```

📌 Use when:

* if–else starts growing
* behavior varies independently

---

## 6️⃣ Factory Template (Object Creation)

```java
public interface Factory<T> {
    T create();
}
```

### Example

```java
public class PaymentStrategyFactory {
    public static PaymentStrategy getStrategy(PaymentType type) {
        switch (type) {
            case CARD: return new CardPayment();
            case UPI: return new UpiPayment();
            default: throw new IllegalArgumentException();
        }
    }
}
```

📌 Factories isolate **creation change**.

---

## 7️⃣ Repository Template (Persistence Abstraction)

```java
public interface Repository<ID, T> {
    void save(T entity);
    T findById(ID id);
}
```

### In-Memory Implementation

```java
public class InMemoryRepository<ID, T> implements Repository<ID, T> {
    private final Map<ID, T> store = new ConcurrentHashMap<>();

    public void save(T entity) {
        // simplified
    }

    public T findById(ID id) {
        return store.get(id);
    }
}
```

📌 Interviewers love seeing repository abstraction.

---

## 8️⃣ Observer Template (Event-Driven Design)

```java
public interface Observer<T> {
    void update(T event);
}
```

```java
public interface Subject<T> {
    void register(Observer<T> observer);
    void notifyAll(T event);
}
```

📌 Perfect for:

* Notifications
* Event systems
* Status updates

---

## 9️⃣ Command Template (Encapsulate Actions)

```java
public interface Command {
    void execute();
}
```

Use cases:

* Undo/Redo
* Button actions
* Task queues

---

## 🔟 Thread-Safety Starter Template

```java
public class ThreadSafeCounter {
    private final AtomicInteger count = new AtomicInteger(0);

    public int increment() {
        return count.incrementAndGet();
    }
}
```

📌 Always *mention* concurrency considerations.

---

## 1️⃣1️⃣ LLD Interview Plug-and-Play Mapping

| Problem      | Templates Used            |
| ------------ | ------------------------- |
| Parking Lot  | Entity, Strategy, Factory |
| Elevator     | Entity, State, Observer   |
| Rate Limiter | Strategy, Service         |
| Splitwise    | Entity, Repository        |

---

## 1️⃣2️⃣ What NOT to Write in Interviews

❌ Full implementations
❌ Database code
❌ UI logic
❌ Framework annotations

📌 Focus on **design clarity**, not completeness.

---

## 🧠 Final Interview Wisdom

> **Good LLD code reads like a design document.**

If your interviewer can predict your next class, you’re doing it right.

---

