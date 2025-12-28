# 🧱 SOLID Principles – Deep Dive (With Code + Interview Tips)

> **Goal:** Master SOLID not as definitions, but as **design instincts** you can *code, explain, and defend* in LLD interviews.

This README is:

* 🔗 Connected to LLD, patterns, concurrency
* 💡 Interview-focused
* ☕ Java-centric

---

## S — Single Responsibility Principle (SRP)

### 🔍 What It REALLY Means

> **A class should have only one reason to change.**

Not:

* One method
* One feature

But:

* One **responsibility**

---

### ❌ Bad Code (SRP Violation)

```java
class OrderManager {
    void createOrder() {}
    void calculatePrice() {}
    void saveToDB() {}
    void sendEmail() {}
}
```

❌ Why this fails:

* Business logic
* Persistence
* Communication

All mixed → fragile design

---

### ✅ Good Code (SRP Applied)

```java
class Order {}

class PricingService {
    double calculate(Order o) { return 0; }
}

class OrderRepository {
    void save(Order o) {}
}

class NotificationService {
    void notifyUser(Order o) {}
}
```

📌 Each class has **one reason to change**.

---

### 🎯 Interview Tips (SRP)

* Say: *"This responsibility may change independently"*
* Smaller classes = easier concurrency
* SRP failures are **top rejection reason**

---

### ❓ Common Interview Question

> **Q:** How do you identify SRP violations?

✅ Answer:

> When a class changes for multiple unrelated reasons or has unrelated collaborators.

---

## O — Open / Closed Principle (OCP)

### 🔍 What It REALLY Means

> **You should be able to add behavior without modifying existing code.**

---

### ❌ Bad Code (OCP Violation)

```java
class PaymentService {
    void pay(String type) {
        if (type.equals("CARD")) {}
        else if (type.equals("UPI")) {}
    }
}
```

❌ Every new payment = modify class

---

### ✅ Good Code (OCP Applied via Strategy)

```java
interface PaymentStrategy {
    void pay();
}

class CardPayment implements PaymentStrategy {
    public void pay() {}
}

class UpiPayment implements PaymentStrategy {
    public void pay() {}
}
```

```java
class PaymentService {
    private final PaymentStrategy strategy;
    PaymentService(PaymentStrategy s) { this.strategy = s; }
    void pay() { strategy.pay(); }
}
```

📌 Add new payment → new class only.

---

### 🎯 Interview Tips (OCP)

* Identify **what changes**
* Patterns = tools for OCP
* Avoid premature abstraction

---

### ❓ Interview Question

> **Q:** Is OCP always achievable?

✅ Answer:

> No. We apply it where change is expected; otherwise we risk over-engineering.

---

## L — Liskov Substitution Principle (LSP)

### 🔍 What It REALLY Means

> **Subtypes must be usable wherever their base type is expected, without breaking correctness.**

---

### ❌ Classic LSP Violation

```java
class Rectangle {
    int width, height;
    void setWidth(int w) { width = w; }
    void setHeight(int h) { height = h; }
}

class Square extends Rectangle {
    void setWidth(int w) { width = height = w; }
}
```

❌ Behavior changes unexpectedly

---

### ✅ Correct Design

```java
interface Shape {
    int area();
}

class Rectangle implements Shape {}
class Square implements Shape {}
```

📌 No broken assumptions.
## 🐦 Bird / Fly Example — Liskov Substitution Principle (LSP)

### ❌ LSP Violation

```java
class Bird {
    void fly() {
        System.out.println("Flying...");
    }
}

class Ostrich extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException("Ostriches can't fly!");
    }
}
```
### ✅ Correct Design

```java
interface Bird {
    void eat();
}

interface Flyable {
    void fly();
}

class Sparrow implements Bird, Flyable {
    @Override
    public void eat() { /* eating logic */ }
    @Override
    public void fly() { /* flying logic */ }
}

class Ostrich implements Bird {
    @Override
    public void eat() { /* eating logic */ }
}


```
---

### 🎯 Interview Tips (LSP)

* LSP is about **behavior**, not inheritance
* Mention contracts (pre/post conditions)
* Advanced: mention thread-safety consistency

---

### ❓ Interview Question

> **Q:** How does LSP relate to polymorphism?

✅ Answer:

> Polymorphism is safe only when LSP is respected; otherwise it causes subtle bugs.

---

## I — Interface Segregation Principle (ISP)

### 🔍 What It REALLY Means

> **Clients should not depend on methods they do not use.**

---

### ❌ Bad Interface

```java
interface Vehicle {
    void drive();
    void fly();
}
```

❌ Forces unnecessary implementation

---

### ✅ Segregated Interfaces

```java
interface Drivable {
    void drive();
}

interface Flyable {
    void fly();
}
```

📌 Cleaner contracts

---

### 🎯 Interview Tips (ISP)

* Smaller interfaces = easier mocking
* Prevents ripple effects
* Works great with Java 8 default methods

---

### ❓ Interview Question

> **Q:** How is ISP different from SRP?

✅ Answer:

> SRP focuses on classes; ISP focuses on client-facing interfaces.

---

## D — Dependency Inversion Principle (DIP)

### 🔍 What It REALLY Means

> **High-level modules should not depend on low-level modules. Both depend on abstractions.**

---

### ❌ Bad Code (Tight Coupling)

```java
class OrderService {
    private final MySQLRepository repo = new MySQLRepository();
}
```

---

### ✅ DIP Applied

```java
interface OrderRepository {
    void save();
}

class MySQLRepository implements OrderRepository {}

class OrderService {
    private final OrderRepository repo;
    OrderService(OrderRepository r) { this.repo = r; }
}
```

📌 Enables testing + flexibility

---

### 🎯 Interview Tips (DIP)

* Enables mocking
* Makes systems extensible
* Core of clean architecture

---

### ❓ Interview Question

> **Q:** How is DIP related to Dependency Injection?

✅ Answer:

> Dependency Injection is a technique; DIP is the design principle it supports.

---

## 🔗 How SOLID Ties Everything Together

| Principle | Solves              | Enables       |
| --------- | ------------------- | ------------- |
| SRP       | God classes         | Concurrency   |
| OCP       | Rigid code          | Extensibility |
| LSP       | Broken polymorphism | Correctness   |
| ISP       | Fat APIs            | Testability   |
| DIP       | Tight coupling      | Flexibility   |

---

## 🧠 Final Interview Cheatsheet

* SOLID ≠ rules
* SOLID = design smell detector
* Explain *why*, not definitions
* Mention trade-offs

> **If you think in SOLID, patterns come naturally.**

---

