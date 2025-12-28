# 🏗️ CREATIONAL DESIGN PATTERNS – DEEP DIVE (LLD + INTERVIEW)

> **Core question they answer:**
> **How should objects be created so the system stays flexible, testable, and extensible?**

Creational patterns deal with:

* Who creates objects
* How creation logic is hidden
* How dependencies are injected
* How object construction evolves over time

📌 **Key Insight**
Object creation is a **responsibility** — and responsibilities must be designed.

---

## 1️⃣ Why Object Creation Is Hard

### ❌ Naive Code

```java
UserService service = new UserService(
    new MySqlRepo(),
    new EmailSender(),
    new Logger()
);
```

### 🚨 Problems

* ❌ Tight coupling
* ❌ Hard to test (no mocking)
* ❌ Hard to change implementations
* ❌ Constructor explosion

👉 Creational patterns solve this **systematically**.

---

## 2️⃣ Creational Patterns — Big Picture

| Pattern             | Solves                    |
| ------------------- | ------------------------- |
| Singleton           | One instance              |
| Factory             | Which object to create    |
| Abstract Factory    | Families of objects       |
| Builder             | Complex object creation   |
| Prototype           | Copying expensive objects |
| Lazy Initialization | Delay object creation     |
| Object Pool         | Reuse costly objects      |

📌 **Interview Reality**
Interviewers mainly care about **Factory, Builder, Abstract Factory**.

---

## 3️⃣ Singleton Pattern (⚠ Use Carefully)

### 🎯 Intent

Ensure only **one instance** exists.

### ✅ Correct Java Implementation

```java
class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();
    private ConfigManager() {}
    public static ConfigManager getInstance() {
        return INSTANCE;
    }
}
```

### ✔ When It Makes Sense

* Configuration
* Cache manager
* Thread pool

### ❌ When NOT to Use

* Business logic
* Services
* Anything requiring isolation in tests

📌 **Interview Line**

> “Singleton introduces global state, so I avoid it unless the object is truly global and immutable.”

---

## 4️⃣ Factory Pattern (🔥 MOST IMPORTANT)

### 🎯 Problem

You want to:

* Hide creation logic
* Return interface types
* Avoid scattered if-else logic

---

### ☕ Simple Factory Example

```java
interface Notification { void send(); }

class EmailNotification implements Notification {}
class SmsNotification implements Notification {}

class NotificationFactory {
    static Notification create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SmsNotification();
            default -> throw new IllegalArgumentException();
        };
    }
}
```

### 💡 Why This Is Powerful

* ✔ Loose coupling
* ✔ Centralized creation
* ✔ Easy extensibility

📌 **Important**
Simple Factory is a **technique** — intent matters.

---

### Factory Method (True Pattern)

```java
abstract class NotificationService {
    abstract Notification create();
}
```

Subclasses decide **which object** to create.

📌 **Interview Line**

> “Factories encapsulate object creation and return abstractions, not concretes.”

---

## 5️⃣ Abstract Factory — Factory of Factories

### 🎯 Problem

You need **families of related objects** that must work together.

### 🧱 Structure

```java
interface UIFactory {
    Button createButton();
    TextBox createTextBox();
}

class DarkUIFactory implements UIFactory {}
class LightUIFactory implements UIFactory {}
```

### 🤔 Why Not Simple Factory?

* Consistency matters
* Objects must be compatible

📌 **Interview Line**

> “Abstract Factory ensures compatibility between related objects.”

---

## 6️⃣ Builder Pattern (⭐ Very Practical)

### 🎯 Problem

* Too many constructor parameters
* Optional fields
* Need immutability

### ☕ Builder Example

```java
User user = User.builder()
    .name("Shivam")
    .email("x@y.com")
    .age(25)
    .build();
```

### ✔ Benefits

* Readable
* Immutable objects
* Easy validation

📌 **Advanced Tip**
Put validation inside `build()`.

---

## 7️⃣ Prototype Pattern

### 🎯 Problem

Object creation is **expensive**.

### ✅ Solution

Clone instead of creating from scratch.

```java
interface Shape extends Cloneable {
    Shape clone();
}
```

### 📦 Used In

* Game engines
* Cache templates

---

## 8️⃣ Lazy Initialization (Lazy Loading)

### 🎯 Problem

Object creation is **heavy**, but not always needed.

### ✅ Solution

Create object **only when first used**.

```java
class HeavyService {
    private static HeavyService instance;

    static HeavyService getInstance() {
        if (instance == null) {
            instance = new HeavyService();
        }
        return instance;
    }
}
```

📌 Often combined with **Singleton** (double-checked locking in real systems).

---

## 9️⃣ Object Pool Pattern

### 🎯 Problem

Creating objects is expensive and frequent.

Examples:

* Database connections
* Thread objects
* Network sockets

### ✅ Solution

Reuse objects instead of recreating them.

```java
class ConnectionPool {
    Queue<Connection> pool;

    Connection acquire() {}
    void release(Connection c) {}
}
```

📌 **Interview Insight**
Most real-world pools are **bounded + thread-safe**.

---

## 🔟 Creational vs Other Patterns

| Pattern Type | Focus              |
| ------------ | ------------------ |
| Creational   | Object creation    |
| Structural   | Object composition |
| Behavioral   | Object interaction |

Creational patterns often **work with others**.

---

## 1️⃣1️⃣ Common Interview Mistakes 🚨

* ❌ Using Singleton everywhere
* ❌ Factory without abstraction
* ❌ Overusing Abstract Factory
* ❌ Builder for simple objects

---

## 1️⃣2️⃣ How FAANG Interviewers Expect You to Think

Say this 👇

> “I separate object creation from usage so that changes in instantiation don’t affect business logic.”

✅ This shows **design maturity**.

---

## 1️⃣3️⃣ Pattern Decision Cheat Sheet

* Need one instance? → **Singleton**
* Need to hide creation? → **Factory**
* Need families? → **Abstract Factory**
* Too many params? → **Builder**
* Expensive creation? → **Prototype**
* Delay creation? → **Lazy Initialization**
* Reuse costly objects? → **Object Pool**

---

## 🔥 Final Takeaway

> **Creational patterns control how objects come to life.**
> Good design means object creation doesn’t leak everywhere.

This is how **production systems stay flexible under change**.
