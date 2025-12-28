# 🧩 Factory Method Design Pattern – Deep Dive

> **Mental model:** The Factory Method pattern defines an interface for creating an object, but lets subclasses alter the type of objects that will be created. It allows a class to defer instantiation to its subclasses.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a logistics management application. The application needs to handle different types of transportation. Initially, you might just have `Truck` transportation.

Your code might look like this:

```java
public class LogisticsApp {
    public void planDelivery(String destination) {
        // The client code is tightly coupled to the Truck class
        Truck truck = new Truck();
        truck.loadCargo();
        truck.assignRoute(destination);
        truck.startJourney();
    }
}
```

Now, the business requirements change, and you need to add `Ship` transportation for overseas deliveries. You would have to modify the `LogisticsApp` class:

```java
public class LogisticsApp {
    public void planDelivery(String destination, String transportType) {
        Transport transport;
        if (transportType.equals("TRUCK")) {
            transport = new Truck();
        } else if (transportType.equals("SHIP")) {
            transport = new Ship();
        }
        // ... and so on for other types
        
        transport.loadCargo();
        // ...
    }
}
```

This `if/else` block is problematic. It violates the **Open/Closed Principle** because every time you add a new transportation method (e.g., `Airplane`), you have to modify this central logic. The client code becomes bloated and responsible for deciding which concrete class to instantiate.

---

## ✅ Factory Method Solution

The Factory Method pattern solves this by moving the instantiation logic into a dedicated "factory method." The client code works with a common `Transport` interface, and the decision of which specific transport to create is delegated to a subclass.

### 🧱 Structure

```
+--------------------+      +--------------------+
|      Creator       |      |      Product       |
| (e.g., Logistics)  |      |  (e.g., Transport) |
|--------------------|      +--------------------+
| + createTransport()|             ^
+--------------------+             |
        ^
        |
+--------------------+      +--------------------+
|  ConcreteCreator   |----->|  ConcreteProduct   |
| (e.g., RoadLogistics)|    |    (e.g., Truck)   |
+--------------------+      +--------------------+
```

- **Product:** The common interface for the objects the factory method creates (`Transport`).
- **ConcreteProduct:** Concrete classes that implement the `Product` interface (`Truck`, `Ship`).
- **Creator:** An abstract class that declares the factory method (`createTransport()`), which returns a `Product` object. It can also contain common business logic that relies on the product.
- **ConcreteCreator:** Subclasses that override the factory method to return a specific `ConcreteProduct` (`RoadLogistics` creates a `Truck`, `SeaLogistics` creates a `Ship`).

### ☕ Java Example

Let's refactor the logistics application.

#### 1. The Product Interface and Concrete Products

```java
// Product Interface
public interface Transport {
    void deliver(String destination);
}

// Concrete Product A
public class Truck implements Transport {
    @Override
    public void deliver(String destination) {
        System.out.println("Delivering by land in a truck to " + destination);
    }
}

// Concrete Product B
public class Ship implements Transport {
    @Override
    public void deliver(String destination) {
        System.out.println("Delivering by sea in a ship to " + destination);
    }
}
```

#### 2. The Creator (Abstract Class)

The `Logistics` class contains the business logic for planning a delivery, but it defers the creation of the transport object to its subclasses through the abstract `createTransport()` method.

```java
// Creator
public abstract class Logistics {
    // This is the core business logic that uses the product.
    public void planDelivery(String destination) {
        // The factory method is called to get the transport object.
        Transport transport = createTransport();
        transport.deliver(destination);
    }

    // This is the Factory Method.
    public abstract Transport createTransport();
}
```

#### 3. Concrete Creators

Each concrete creator implements the factory method to produce a specific type of transport.

```java
// Concrete Creator A
public class RoadLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        // This factory method creates a Truck
        return new Truck();
    }
}

// Concrete Creator B
public class SeaLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        // This factory method creates a Ship
        return new Ship();
    }
}
```

#### 4. Client Code

The client code now depends only on the `Logistics` abstraction, not the concrete `Truck` or `Ship` classes. To get a different type of transport, the client simply uses a different creator.

```java
public class Application {
    public static void main(String[] args) {
        Logistics logistics;

        String deliveryType = "ROAD"; // This could come from config, user input, etc.

        // The application decides which creator to use at runtime.
        if (deliveryType.equalsIgnoreCase("ROAD")) {
            logistics = new RoadLogistics();
        } else if (deliveryType.equalsIgnoreCase("SEA")) {
            logistics = new SeaLogistics();
        } else {
            throw new IllegalArgumentException("Unknown delivery type");
        }

        // The client code doesn't know which concrete transport is being used.
        logistics.planDelivery("New York City");
    }
}
```

---

## ✔ When to Use the Factory Method Pattern

- **Decoupling Creation Logic:** When a class cannot anticipate the class of objects it must create.
- **Subclass Responsibility:** When you want to give subclasses the responsibility of creating specific objects.
- **Extensibility:** When you want to provide users of your library or framework with a way to extend its internal components (e.g., adding a new `AirplaneLogistics` without changing the core framework).

## 🆚 Factory Method vs. Simple Factory

A "Simple Factory" is a common idiom (but not a GoF pattern) where a single class with one method is responsible for creating objects. The key difference is that with the Factory Method pattern, the creation logic is delegated to **subclasses**, promoting the Open/Closed Principle more effectively.

## 💡 Interview Line

> **“The Factory Method pattern lets a class defer instantiation to its subclasses. The superclass knows *when* an object needs to be created, but the subclass decides *which* specific object to create.”**

---

## 🚀 Next Steps

- Explore the **Abstract Factory Pattern**, which is used to create *families* of related objects.
- Understand the **Builder Pattern**, which is ideal for creating complex objects with many configuration options.
