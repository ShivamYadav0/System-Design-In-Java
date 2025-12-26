# 🧩 Observer Design Pattern – Deep Dive

> **Mental model:** The Observer pattern defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a stock trading application. You have a `Stock` object whose price can change frequently. You also have multiple UI components that need to react to this price change:

-   A `PriceTicker` that displays the latest price.
-   A `PriceChart` that adds the new price to a historical graph.
-   A `NotificationService` that sends an alert if the price crosses a certain threshold.

A naive approach would be for the `Stock` object to know about and directly call the update methods of all these other objects.

```java
// NOT a good approach
public class Stock {
    private String symbol;
    private double price;
    // The Stock object has direct references to other objects!
    private PriceTicker ticker;
    private PriceChart chart;
    private NotificationService notifier;

    public void setPrice(double newPrice) {
        this.price = newPrice;
        // Tightly coupled calls
        ticker.update(newPrice);
        chart.update(newPrice);
        notifier.update(newPrice);
    }
}
```

This design is deeply flawed:

1.  **Tight Coupling:** The `Stock` class (the business logic, or "Subject") is tightly coupled to the UI and notification classes (the "Observers"). It has to know about their specific classes and methods.
2.  **Violation of Single Responsibility Principle:** The `Stock`'s primary job is to manage its price, not to manage UI components. It has taken on the extra responsibility of updating other parts of the application.
3.  **Inflexible:** What if you want to add a new observer, like a `PortfolioManager`? You would have to go back and modify the `Stock` class. This violates the Open/Closed Principle.

---

## ✅ Observer Solution

The Observer pattern decouples the Subject from its Observers. The Subject maintains a list of observers and provides methods to subscribe (`register`) and unsubscribe (`unregister`) observers. When the Subject's state changes, it iterates through its list of observers and calls a generic `update` method on each one, passing itself as a parameter.

### 🧱 Structure

```
+------------------+         +-----------------+
|     Subject      |<>------>|     Observer    |
|   (Publisher)    |         |   (Subscriber)  |
|------------------|         |-----------------|
| - observers: List|         | + update(data)  |
| + register()     |         +-----------------+
| + unregister()   |                 ^
| + notify()       |                 |
+------------------+         +-------+-------+
        ^
        |                  |               |
+------------------+ +-----------------+ +-----------------+
| ConcreteSubject  | | ConcreteObserverA | | ConcreteObserverB |
| (Stock)          | | (PriceTicker)   | | (PriceChart)    |
+------------------+ +-----------------+ +-----------------+
```

-   **Subject:** An interface or abstract class that defines the methods for managing observers (`register`, `unregister`, `notify`).
-   **ConcreteSubject:** Implements the `Subject` interface. It stores the state that is of interest to observers and calls `notify()` when its state changes.
-   **Observer:** An interface or abstract class that defines the `update` method, which is called by the Subject.
-   **ConcreteObserver:** Implements the `Observer` interface. It registers with a `ConcreteSubject` to receive updates.

### ☕ Java Example

#### 1. The Observer Interface

```java
// The Observer interface
public interface StockObserver {
    void priceChanged(Stock stock);
}
```

#### 2. The Subject (Publisher)

We can use a base class to handle the subscription management.

```java
import java.util.ArrayList;
import java.util.List;

// The abstract Subject
public abstract class StockPublisher {
    private final List<StockObserver> observers = new ArrayList<>();

    public void subscribe(StockObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(StockObserver observer) {
        observers.remove(observer);
    }

    // Notify all subscribed observers
    protected void notifySubscribers(Stock stock) {
        for (StockObserver observer : observers) {
            observer.priceChanged(stock);
        }
    }
}
```

#### 3. The Concrete Subject

```java
// The Concrete Subject
public class Stock extends StockPublisher {
    private final String symbol;
    private double price;

    public Stock(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.price = initialPrice;
    }

    public void setPrice(double newPrice) {
        System.out.println("Price of " + symbol + " changed from " + this.price + " to " + newPrice);
        this.price = newPrice;
        // The crucial part: notify all observers about the change!
        notifySubscribers(this);
    }

    public double getPrice() { return price; }
    public String getSymbol() { return symbol; }
}
```

#### 4. Concrete Observers

```java
// Concrete Observer A
public class PriceTicker implements StockObserver {
    @Override
    public void priceChanged(Stock stock) {
        System.out.println("TICKER: New price for " + stock.getSymbol() + " is " + stock.getPrice());
    }
}

// Concrete Observer B
public class NotificationService implements StockObserver {
    private final double alertThreshold;

    public NotificationService(double alertThreshold) { this.alertThreshold = alertThreshold; }

    @Override
    public void priceChanged(Stock stock) {
        if (stock.getPrice() > alertThreshold) {
            System.out.println("NOTIFICATION: " + stock.getSymbol() + " has crossed the threshold of " + alertThreshold);
        }
    }
}
```

#### 5. Client Code

```java
public class Application {
    public static void main(String[] args) {
        // The subject
        Stock googleStock = new Stock("GOOGL", 2800.00);

        // The observers
        PriceTicker ticker = new PriceTicker();
        NotificationService notifier = new NotificationService(2850.00);

        // Subscribe the observers to the subject
        googleStock.subscribe(ticker);
        googleStock.subscribe(notifier);

        System.out.println("--- Changing price ---");
        googleStock.setPrice(2845.50);

        System.out.println("
--- Changing price again (this should trigger the notification) ---");
        googleStock.setPrice(2860.00);

        System.out.println("
--- Unsubscribing the ticker and changing price ---");
        googleStock.unsubscribe(ticker);
        googleStock.setPrice(2870.00); // Ticker will not be updated
    }
}
```

---

## ✔ When to Use the Observer Pattern

-   **One-to-Many Relationship:** When changes to one object require changing an unknown number of other objects.
-   **Decoupling:** When you want the objects to be loosely coupled. The subject doesn't need to know anything about the concrete classes of its observers.
-   **Event Handling:** It is the cornerstone of event-driven programming. GUI frameworks, message queues, and event listeners all use this pattern extensively.

## 💡 Interview Line

> **“The Observer pattern is used for building loosely coupled systems where multiple objects (Observers) need to react to state changes in a single object (the Subject). The Subject maintains a list of its Observers and notifies them automatically, so the Subject doesn’t need to know who or what its Observers are. It’s fundamental for event-driven architecture.”**

---

## 🚀 Next Steps

-   Look into the `java.util.Observer` and `java.util.Observable` classes (though they are now considered legacy/deprecated in favor of more modern event handling mechanisms like property change listeners or reactive streams).
-   Explore the **Publish-Subscribe (Pub/Sub)** pattern, which is a more advanced and scalable variant of the Observer pattern, often involving a dedicated message broker.
