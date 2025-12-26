# 🧩 Facade Design Pattern – Deep Dive

> **Mental model:** The Facade pattern provides a simplified, high-level interface to a complex subsystem of components. It hides the system's complexity and makes it easier to use.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building an e-commerce platform. To place an order, a client needs to interact with several different services:

1.  **Inventory Service:** To check if the product is in stock.
2.  **Payment Service:** To process the payment.
3.  **Shipping Service:** To arrange for the delivery of the product.
4.  **Notification Service:** To send a confirmation email to the user.

The client code would look something like this:

```java
public class ECommerceClient {
    public void placeOrder(String productId, int amount, String address, String email) {
        // 1. Check stock
        InventoryService inventory = new InventoryService();
        if (!inventory.isAvailable(productId)) {
            System.out.println("Product out of stock.");
            return;
        }

        // 2. Process payment
        PaymentService payment = new PaymentService();
        String transactionId = payment.processPayment(amount);
        if (transactionId == null) {
            System.out.println("Payment failed.");
            return;
        }

        // 3. Ship the product
        ShippingService shipping = new ShippingService();
        shipping.shipProduct(productId, address);

        // 4. Send confirmation
        NotificationService notifier = new NotificationService();
        notifier.sendConfirmation(email, "Your order has been placed.");

        System.out.println("Order placed successfully!");
    }
}
```

This approach has several drawbacks:
- **High Coupling:** The client is tightly coupled to every single service in the subsystem. If any service's interface changes, the client code must be modified.
- **Complex Logic:** The client is responsible for understanding and managing the complex workflow of placing an order.
- **Code Duplication:** If multiple parts of the application need to place an order, this complex logic will be duplicated.

---

## ✅ Facade Solution

The Facade pattern introduces a single `OrderFacade` class that encapsulates the entire order-placement process. The client no longer needs to know about the individual services. It just calls a single method on the facade.

### 🧱 Structure

```
+--------------+      +------------------+      +--------------------+
|    Client    |----->|      Facade      |----->|   Subsystem Classes  |
| (ECommerce)  |      |  (OrderFacade)   |      | (Inventory, Payment, etc.) |
+--------------+      +------------------+      +--------------------+
```

- **Facade:** The class that provides a simple interface to the complex subsystem (`OrderFacade`).
- **Subsystem Classes:** The individual components that implement the actual functionality (`InventoryService`, `PaymentService`, etc.). The facade knows how to use them, but the client does not.

### ☕ Java Example

Let's refactor the e-commerce system using the Facade pattern.

#### 1. The Complex Subsystem

These are the individual services. They are internal to the system.

```java
// Subsystem Class 1
class InventoryService {
    public boolean isAvailable(String productId) {
        System.out.println("Checking stock for product: " + productId);
        return true; // Simulate availability
    }
}

// Subsystem Class 2
class PaymentService {
    public String processPayment(int amount) {
        System.out.println("Processing payment of: $" + amount);
        return "txn_12345"; // Simulate successful payment
    }
}

// Subsystem Class 3
class ShippingService {
    public void shipProduct(String productId, String address) {
        System.out.println("Shipping product " + productId + " to " + address);
    }
}

// Subsystem Class 4
class NotificationService {
    public void sendConfirmation(String email, String message) {
        System.out.println("Sending email to " + email + ": " + message);
    }
}
```

#### 2. The Facade

The `OrderFacade` class provides a single, simple method to place an order.

```java
public class OrderFacade {
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final NotificationService notificationService;

    public OrderFacade() {
        this.inventoryService = new InventoryService();
        this.paymentService = new PaymentService();
        this.shippingService = new ShippingService();
        this.notificationService = new NotificationService();
    }

    public boolean placeOrder(String productId, int amount, String address, String email) {
        System.out.println("--- Processing order with Facade ---");
        if (!inventoryService.isAvailable(productId)) {
            System.out.println("Order failed: Product out of stock.");
            return false;
        }

        String transactionId = paymentService.processPayment(amount);
        if (transactionId == null) {
            System.out.println("Order failed: Payment was not successful.");
            return false;
        }

        shippingService.shipProduct(productId, address);
        notificationService.sendConfirmation(email, "Your order has been placed successfully.");

        System.out.println("Order placed successfully!");
        return true;
    }
}
```

#### 3. The Client

The client code is now much simpler and cleaner.

```java
public class ECommerceClient {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade();
        
        // The client interacts with the simple facade, not the complex subsystem
        orderFacade.placeOrder("prod_101", 250, "123 Main St, Anytown, USA", "customer@example.com");
    }
}
```

---

## ✔ When to Use the Facade Pattern

- **Simplifying a Complex System:** When you have a complex system with many interdependent classes and you want to provide a simple way for clients to use it.
- **Decoupling:** To reduce the coupling between client code and the internal workings of a library or subsystem. This allows you to change the subsystem's components without affecting the client.
- **Layering:** To create layers in your application. A facade can be the entry point to a service layer, hiding the more complex domain logic behind it.

## 🆚 Facade vs. Adapter

- **Intent:** The key difference is intent. A **Facade** is designed to **simplify** a complex interface. An **Adapter** is designed to **convert** one interface into another to make it compatible with what a client expects.
- **Scope:** A facade usually wraps multiple objects to simplify a whole subsystem. An adapter typically wraps a single object.

## 💡 Interview Line

> **“A Facade simplifies a system by providing a unified, higher-level interface, while an Adapter helps two incompatible interfaces work together. Facade simplifies, Adapter converts.”**

---

## 🚀 Next Steps

- Explore the **Composite Pattern**, which lets you compose objects into tree structures and then work with these structures as if they were individual objects.
- Understand the **Bridge Pattern**, which decouples an abstraction from its implementation so that the two can vary independently.
