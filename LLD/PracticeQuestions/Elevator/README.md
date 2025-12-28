# ☕ Java LLD Starter Template for Elevator System

> **Goal:** Never start from a blank screen in an LLD interview again.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements
2.  Identify entities
3.  Identify what changes
4.  Plug into these templates
5.  Explain trade-offs

📌 These templates are **intentionally minimal** — extensibility > completeness.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.elevator
 ├── domain        // entities & value objects (e.g., Elevator, Floor, Request)
 ├── service       // business logic (e.g., ElevatorControlService)
 ├── strategy      // pluggable behaviors (e.g., ElevatorSelectionStrategy)
 ├── repository    // storage abstractions (e.g., RequestRepository)
 ├── factory       // object creation (e.g., RequestFactory)
 └── api           // public interfaces / controllers
```

📌 Interview tip: *Say this structure out loud* — it shows maturity.

---

## 2️⃣ The `Entity` Template (The "Noun" Objects)

```java
// Common interface for all domain models
public abstract class BaseEntity {
    private String id;
    // getters, setters, equals, hashCode
}

// Example: An elevator car
public class Elevator extends BaseEntity {
    private int currentFloor;
    private Direction direction;
    private SortedSet<Integer> stops;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for controlling the elevators
public class ElevatorControlService {
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy selectionStrategy;

    public ElevatorControlService(List<Elevator> elevators, ElevatorSelectionStrategy strategy) {
        this.elevators = elevators;
        this.selectionStrategy = strategy;
    }

    public void handleRequest(Request request) {
        Elevator selectedElevator = selectionStrategy.selectElevator(elevators, request);
        selectedElevator.addStop(request.getFloor());
    }

    public void moveElevators() {
        // ... logic to move each elevator one step
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable elevator selection logic
public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

// Example: Different strategies for selecting an elevator
public class NearestElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        // ... logic to find the nearest elevator
    }
}

public class LeastBusyElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        // ... logic to find the elevator with the fewest stops
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface RequestRepository {
    void save(Request request);
    List<Request> findAll();
}

// In-memory implementation for interviews
public class InMemoryRequestRepository implements RequestRepository {
    private final List<Request> requests = new ArrayList<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of requests
public class RequestFactory {
    public static Request createRequest(int floor, Direction direction) {
        return new Request(floor, direction);
    }
}
```
