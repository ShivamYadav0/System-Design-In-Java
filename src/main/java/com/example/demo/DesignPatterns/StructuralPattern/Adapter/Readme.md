# 🧩 Adapter Design Pattern – Deep Dive

> **Mental model:** The Adapter pattern acts as a bridge between two incompatible interfaces. It converts the interface of a class into another interface that a client expects.

---

## 🔍 Problem (Realistic Scenario)

Imagine you have an existing system that works with a specific type of `PaymentGateway` interface. Now, you want to integrate a new, third-party payment processor, but its API is completely different from what your system expects.

- **Existing System:** Your application is designed to work with a `PaymentGateway` interface that has a `processPayment(amount)` method.
- **New Service (Adaptee):** The new payment processor has a class called `ThirdPartyProcessor` with a method named `executeTransaction(transactionDetails)`.

You cannot directly use `ThirdPartyProcessor` in your system because the interfaces are incompatible. Modifying the third-party library is not an option, and changing your entire system to accommodate this one service is impractical and risky.

---

## ✅ Adapter Solution

The solution is to create an `Adapter` class that wraps the `ThirdPartyProcessor` and implements the `PaymentGateway` interface. This adapter will translate the calls from your system's interface to the one used by the third-party processor.

### 🧱 Structure

```
+----------------+      +------------------+      +--------------------+
|     Client     |----->|  Target Interface|      |      Adaptee       |
| (Your System)  |      | (PaymentGateway) |      | (ThirdPartyProcessor)|
+----------------+      +------------------+      +--------------------+
                           ^
                           |
                           |
                    +------------------+
                    |      Adapter     |
                    | (ProcessorAdapter)|
                    +------------------+
```

- **Client:** The part of your application that needs to process payments. It is coupled to the `Target Interface`.
- **Target Interface:** The interface your `Client` expects (`PaymentGateway`).
- **Adaptee:** The existing class with an incompatible interface that you want to use (`ThirdPartyProcessor`).
- **Adapter:** A class that implements the `Target Interface` and holds a reference to the `Adaptee`. It translates requests from the `Client` into calls on the `Adaptee`.

---

## ☕ Java Example

Let's illustrate this with a clear Java example.

### Target Interface

This is the interface your application expects.

```java
// Target Interface
public interface PaymentGateway {
    void processPayment(double amount);
}
```

### Adaptee (The Incompatible Class)

This is the third-party class you want to integrate.

```java
// Adaptee
public class ThirdPartyProcessor {
    public void executeTransaction(String transactionId, double value) {
        System.out.println("Executing transaction " + transactionId + " for amount: " + value);
    }
}
```

### Adapter Implementation

This adapter makes the `ThirdPartyProcessor` compatible with the `PaymentGateway` interface.

```java
import java.util.UUID;

// Adapter
public class ProcessorAdapter implements PaymentGateway {

    private final ThirdPartyProcessor adaptee;

    public ProcessorAdapter(ThirdPartyProcessor adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void processPayment(double amount) {
        // Generate a unique transaction ID, which the adaptee requires
        String transactionId = UUID.randomUUID().toString();
        
        // Translate the method call from processPayment(amount) to executeTransaction(id, value)
        adaptee.executeTransaction(transactionId, amount);
    }
}
```

### Client Code

Here is how the client can now use the adapter to process payments, unaware that a different underlying system is being used.

```java
public class PaymentService {
    public static void main(String[] args) {
        // The system can work with any gateway that implements the target interface
        PaymentGateway gateway;

        // Use the adapter to work with the incompatible third-party processor
        ThirdPartyProcessor newProcessor = new ThirdPartyProcessor();
        gateway = new ProcessorAdapter(newProcessor);

        // Process a payment. The client code doesn't change.
        System.out.println("Processing a payment of $150...");
        gateway.processPayment(150.0);
    }
}
```

---

## ✔ When to Use the Adapter Pattern

- **Integrating Legacy Code:** When you need to make existing classes work with other classes without modifying their source code.
- **Third-Party APIs:** When you need to integrate external libraries or services that have interfaces different from what your application expects.
- **Interface Mismatch:** Any time you have a class that provides the right functionality but the wrong interface.

## 💡 Interview Line

> **“The Adapter pattern is about making things work together, focusing on compatibility, not adding new functionality.”**

---

## 🆚 Adapter vs. Other Patterns

- **Adapter vs. Bridge:** The Adapter pattern is used to make incompatible interfaces work together, often applied to existing systems. The Bridge pattern is a design choice made upfront to decouple an abstraction from its implementation, allowing them to evolve independently.
- **Adapter vs. Decorator:** An Adapter changes an object's interface, while a Decorator enhances an object's responsibilities without changing its interface.

---

## 🚀 Next Steps

- Explore the **Decorator Pattern** to see how to add functionality dynamically.
- Understand the **Facade Pattern**, which simplifies the interface of a complex subsystem.
