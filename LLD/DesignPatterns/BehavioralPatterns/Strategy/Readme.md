# 🧩 Strategy Design Pattern – Deep Dive

> **Mental model:** The Strategy pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a map application that provides route planning. A user can choose different modes of transportation, such as driving, walking, or public transit. The core algorithm for calculating a route changes drastically based on the chosen mode.

- **Driving:** The algorithm needs to consider roads, traffic, and one-way streets.
- **Walking:** The algorithm uses pedestrian paths, sidewalks, and shortcuts through parks.
- **Public Transit:** The algorithm must account for bus/subway schedules, station locations, and transfers.

A naive approach would be to put all this logic inside a single `RoutePlanner` class:

```java
public class RoutePlanner {
    public Route calculateRoute(Point start, Point end, String mode) {
        if (mode.equals("DRIVING")) {
            // Complex logic for calculating a driving route
            System.out.println("Calculating driving route...");
            return new Route(/* ... */);
        } else if (mode.equals("WALKING")) {
            // Complex logic for pedestrian routing
            System.out.println("Calculating walking route...");
            return new Route(/* ... */);
        } else if (mode.equals("TRANSIT")) {
            // Complex logic for public transit
            System.out.println("Calculating transit route...");
            return new Route(/* ... */);
        }
        return null;
    }
}
```

This approach has several major problems:

1.  **Massive Class:** The `RoutePlanner` class becomes bloated and difficult to maintain.
2.  **Violation of Single Responsibility Principle:** The class is responsible for all routing algorithms.
3.  **Violation of Open/Closed Principle:** To add a new mode of transportation (e.g., "Biking"), you must modify the `calculateRoute` method, which risks breaking existing logic.
4.  **Difficult to Test:** You cannot test one algorithm in isolation.

---

## ✅ Strategy Solution

The Strategy pattern solves this by extracting each algorithm into its own separate class, called a "strategy." The main class, known as the "context," holds a reference to a strategy object. The context delegates the work to the strategy object instead of implementing the behavior itself. The context can be configured with different strategies at runtime.

### 🧱 Structure

```
+----------------+
|    Context     |<>----->+-------------------+
| (RoutePlanner) |       |      Strategy     |
|----------------|       | (RoutingStrategy) |
| + setStrategy()|       |-------------------|
| + execute()    |       | + calculate():Route|
+----------------+       +-------------------+
                               ^
                               |
      +------------------------+------------------------+
      |                        |                        |
+-------------------+  +-------------------+  +--------------------+
| ConcreteStrategyA |  | ConcreteStrategyB |  | ConcreteStrategyC  |
| (DrivingStrategy) |  | (WalkingStrategy) |  | (TransitStrategy)  |
+-------------------+  +-------------------+  +--------------------+
```

-   **Context (`RoutePlanner`):** Maintains a reference to a `Strategy` object. It calls the method on the strategy object when it needs to execute the algorithm.
-   **Strategy (`RoutingStrategy`):** An interface that is common to all supported algorithms. It declares the method the context will use.
-   **ConcreteStrategy (`DrivingStrategy`, `WalkingStrategy`, etc.):** Implements the algorithm using the `Strategy` interface.

### ☕ Java Example

#### 1. The Strategy Interface

This defines the common operation for all routing algorithms.

```java
// The Strategy Interface
public interface RoutingStrategy {
    Route calculateRoute(Point start, Point end);
}
```

#### 2. Concrete Strategy Implementations

Each algorithm is encapsulated in its own class.

```java
// Concrete Strategy A
public class DrivingStrategy implements RoutingStrategy {
    @Override
    public Route calculateRoute(Point start, Point end) {
        System.out.println("Calculating driving route...");
        // ... complex logic for driving
        return new Route("Driving");
    }
}

// Concrete Strategy B
public class WalkingStrategy implements RoutingStrategy {
    @Override
    public Route calculateRoute(Point start, Point end) {
        System.out.println("Calculating walking route...");
        // ... complex logic for walking
        return new Route("Walking");
    }
}

// Concrete Strategy C
public class TransitStrategy implements RoutingStrategy {
    @Override
    public Route calculateRoute(Point start, Point end) {
        System.out.println("Calculating public transit route...");
        // ... complex logic for transit
        return new Route("Transit");
    }
}
```

#### 3. The Context Class

The `RoutePlanner` (context) is configured with a strategy object.

```java
// The Context
public class RoutePlanner {
    private RoutingStrategy strategy;

    // The context can be configured with a strategy at creation or later
    public void setStrategy(RoutingStrategy strategy) {
        this.strategy = strategy;
    }

    // The context delegates the work to the currently set strategy
    public Route execute(Point start, Point end) {
        if (strategy == null) {
            throw new IllegalStateException("Routing strategy not set");
        }
        return strategy.calculateRoute(start, end);
    }
}
```

#### 4. Client Code

The client chooses the appropriate strategy and passes it to the context.

```java
public class Application {
    public static void main(String[] args) {
        RoutePlanner planner = new RoutePlanner();
        Point start = new Point(0, 0);
        Point end = new Point(100, 100);

        // User chooses to drive
        planner.setStrategy(new DrivingStrategy());
        Route drivingRoute = planner.execute(start, end);

        // Later, user chooses to walk
        planner.setStrategy(new WalkingStrategy());
        Route walkingRoute = planner.execute(start, end);

        // Add a new BikingStrategy? Just create the class and set it.
        // planner.setStrategy(new BikingStrategy());
    }
}

// Dummy classes for the example
class Point { int x, y; public Point(int x, int y) { this.x = x; this.y = y; } }
class Route { String type; public Route(String type) { this.type = type; } }
```

---

## ✔ When to Use the Strategy Pattern

-   **Multiple Algorithms:** When you have multiple variants of an algorithm for a task and you want to switch between them at runtime.
-   **Eliminate Conditionals:** When you want to get rid of a complex `if-else` or `switch` statement that selects between different behaviors.
-   **Isolate Complex Logic:** When you want to isolate the business logic of a particular algorithm from the rest of the client code.
-   **Varying Behavior:** When different objects of the same class should behave differently.

## 🆚 Strategy vs. State Pattern

Strategy and State have very similar structures, but their **intent** is different:

-   **Strategy:** Focuses on providing **interchangeable algorithms** for a context to *use*. The client is often aware of the different strategies and actively chooses one.
-   **State:** Focuses on managing the **state of an object** and allowing it to change its behavior when its internal state changes. The state transition is typically managed by the context or the state objects themselves, not by the client.

## 💡 Interview Line

> **“The Strategy pattern is about encapsulating a family of algorithms and making them interchangeable. It allows a client to choose the appropriate algorithm at runtime without changing the client's code, promoting the Open/Closed Principle.”**

---

## 🚀 Next Steps

-   Explore the **State Pattern** to understand how a similar structure can be used to manage an object's internal state.
-   Review the **Template Method Pattern**, which offers another way to structure algorithms, but through inheritance rather than composition.
-   In Java 8+, consider using **lambda expressions** for simple strategies instead of creating full-blown concrete classes.
