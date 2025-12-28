# 🧩 STRUCTURAL DESIGN PATTERNS – DEEP DIVE (LLD + INTERVIEW)

> **Mental model:** Structural patterns are about **how objects are wired together** to form larger structures *without breaking flexibility*.

Interviewers care only about patterns that:

* Appear in real systems
* Prevent class explosion
* Improve layering & boundaries

---

## 2️⃣ Structural Patterns (What Changes?)

> **What varies:** Object **composition & structure**, not behavior logic.

---

## 2.1 Adapter Pattern — *Compatibility Pattern*

### 🔍 Problem (Realistic)

You have:

* Existing / legacy class
* New system expects a **different interface**
* Functionality exists, but interfaces don’t match

❌ Modifying legacy code is:

* Risky
* Sometimes impossible (3rd-party SDK)

---

### ✅ Adapter Solution

> **Convert one interface into another expected by the client.**

### 🧱 Structure

```
Client → Target Interface
                ↑
            Adapter
                ↓
          Adaptee (Legacy)
```

---

### ☕ Java Example

```java
interface PaymentGateway {
    void pay(int amount);
}

// Legacy system
class Razorpay {
    void makePayment(int money) {}
}

// Adapter
class RazorpayAdapter implements PaymentGateway {
    private final Razorpay razorpay;

    RazorpayAdapter(Razorpay r) {
        this.razorpay = r;
    }

    public void pay(int amount) {
        razorpay.makePayment(amount);
    }
}
```

---

### ✔ When to Use

* Integrating legacy code
* Third-party APIs
* Interface mismatch

### 💡 Interview Line

> **“Adapter is about compatibility, not enhancement.”**

---

## 2.2 Decorator Pattern — *Dynamic Feature Addition*

### 🔍 Problem

You want to:

* Add responsibilities dynamically
* Avoid subclass explosion

❌ Inheritance explosion:

```
CoffeeWithMilk
CoffeeWithSugar
CoffeeWithMilkAndSugar
CoffeeWithMilkSugarCaramel
```

---

### ✅ Decorator Solution

> **Wrap an object and add behavior dynamically.**

### 🧱 Structure

```
Component
   ↑
ConcreteComponent
   ↑
Decorator (has-a Component)
   ↑
ConcreteDecorator
```

---

### ☕ Java Example

```java
interface Coffee {
    int cost();
}

class BasicCoffee implements Coffee {
    public int cost() { return 50; }
}

class MilkDecorator implements Coffee {
    private final Coffee coffee;

    MilkDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    public int cost() {
        return coffee.cost() + 10;
    }
}
```

### 🧠 Key Insight

* Decorator **is-a** Component
* Decorator **has-a** Component

### 📦 Used In

* Java IO (`BufferedInputStream`)
* Spring Security filter chains
* Middleware pipelines

### 🚨 Interview Trap

❌ Using Decorator for algorithm selection
➡ That’s **Strategy**

---

## 2.3 Proxy Pattern — *Access Control Pattern*

### 🔍 Problem

You want to:

* Control access
* Add pre/post logic
* Avoid direct object usage

---

### 🧱 Types of Proxies

| Type       | Use           |
| ---------- | ------------- |
| Virtual    | Lazy loading  |
| Protection | Authorization |
| Logging    | Audit         |
| Caching    | Performance   |

---

### ☕ Java Example

```java
class ServiceProxy implements Service {
    private RealService realService;

    public void execute() {
        checkPermission();
        realService.execute();
        log();
    }
}
```

---

### 🧠 Proxy vs Decorator (Interview Gold)

| Decorator          | Proxy           |
| ------------------ | --------------- |
| Adds functionality | Controls access |
| Same interface     | Same interface  |
| Client unaware     | Client unaware  |

📌 **Intent differs — structure looks same**

---

## 2.4 Facade Pattern — *Simplification Pattern*

### 🔍 Problem

Subsystem is too complex to use directly.

---

### ✅ Facade Solution

> **Provide a simple interface over a complex subsystem.**

---

### ☕ Java Example

```java
class OrderFacade {
    void placeOrder() {
        inventory.check();
        payment.pay();
        shipment.ship();
    }
}
```

---

### 📦 Used In

* SDKs
* APIs
* Service layers

### 💡 Interview Line

> **“Facade simplifies usage, not functionality.”**

---

## 2.5 Composite Pattern — *Tree Structure Pattern*

### 🔍 Problem

You want to:

* Treat individual and group objects uniformly

---

### 🌳 Examples

* File systems
* UI component trees

---

### ☕ Java Example

```java
interface FileSystem {
    int size();
}

class File implements FileSystem {}

class Directory implements FileSystem {
    List<FileSystem> children;
}
```

### 🏆 Interview Gold

> **Composite enables recursive structures with uniform treatment.**

---

## 2.6 Bridge Pattern — *Decoupling Abstraction*

### 🔍 Problem

Inheritance causes **class explosion**:

```
RedCircle
BlueCircle
RedSquare
BlueSquare
```

---

### ✅ Bridge Solution

> **Separate abstraction from implementation so both can vary independently.**

---

### ☕ Java Example

```java
interface Color {
    void apply();
}

abstract class Shape {
    protected Color color;
}
```

### 💡 Interview Tip

* **Bridge ≠ Adapter**
* Bridge is **designed upfront**

---

## 3️⃣ Summary — Structural Patterns

| Pattern   | Solves                  |
| --------- | ----------------------- |
| Adapter   | Incompatible interfaces |
| Decorator | Dynamic behavior        |
| Proxy     | Controlled access       |
| Facade    | Simplified interface    |
| Composite | Tree structures         |
| Bridge    | Avoid class explosion   |

---

## 4️⃣ Structural vs Behavioral — CLEAR DIFFERENCE

| Aspect      | Structural            | Behavioral         |
| ----------- | --------------------- | ------------------ |
| Focus       | Object composition    | Object interaction |
| Concern     | How objects are wired | How logic flows    |
| Changes     | Structure changes     | Algorithm changes  |
| Typical use | Wrapping, layering    | Flow control       |

---

## 🧠 Interview Master Tip

> **If you explain the *intent* correctly, interviewers don’t care about UML diagrams.**

---

## 🚀 Next Natural Step

* Behavioral Patterns (Strategy, Observer, Command)
* Structural vs Creational comparison
* Pattern misuse → real rejection cases


