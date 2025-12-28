# ☕ Java LLD Starter Template for Elevator System

> **Goal:** Design a concurrent elevator control system that can efficiently manage multiple elevators and passenger requests in real-time.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., number of elevators, floors, scheduling algorithm).
2.  Identify entities (`Elevator`, `Request`, `ElevatorController`).
3.  **Identify Shared, Mutable State** (The set of pending requests and the internal state of each elevator).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., different scheduling strategies and their impact on wait times).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.elevator
 ├── domain        // Entities like Elevator, Request, and Direction.
 ├── service       // The main ElevatorController and individual Elevator worker threads.
 ├── strategy      // Pluggable behaviors (e.g., ElevatorSelectionStrategy).
 ├── repository    // Thread-safe queues for managing requests.
 └── api           // Interface for users to submit floor requests.
```

---

## 2️⃣ The `Entity` & `Worker` Template (The `Elevator` as a `Runnable`)

Each elevator should run in its own thread, processing its own set of stops independently.

```java
// The state of an elevator. This is the shared state for the Elevator worker.
public class Elevator implements Runnable {
    private final String id;
    private volatile int currentFloor = 0;
    private volatile Direction direction = Direction.IDLE;

    // Each elevator has its own set of stops to make.
    // A ConcurrentSkipListSet is a thread-safe, sorted set, perfect for managing stops.
    private final ConcurrentSkipListSet<Integer> stops = new ConcurrentSkipListSet<>();

    public Elevator(String id) { this.id = id; }

    public void addStop(int floor) {
        stops.add(floor);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Decide next move based on current stops and direction
                move();
                Thread.sleep(1000); // Simulate time to move between floors
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void move() {
        if (stops.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        // Determine the next target floor based on current direction
        Integer nextTarget = (direction == Direction.UP || direction == Direction.IDLE) ? stops.higher(currentFloor) : stops.lower(currentFloor);

        if (nextTarget == null) {
            // If no more stops in the current direction, reverse or go idle.
            direction = (direction == Direction.UP) ? Direction.DOWN : Direction.UP;
            nextTarget = (direction == Direction.UP) ? stops.higher(currentFloor) : stops.lower(currentFloor);
            if (nextTarget == null) {
                direction = Direction.IDLE;
                return;
            }
        }

        // Move towards the target
        if (nextTarget > currentFloor) currentFloor++;
        else if (nextTarget < currentFloor) currentFloor--;

        // If we arrived at a stop, process it.
        if (currentFloor == nextTarget) {
            System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
            stops.remove(currentFloor);
        }
    }
    // Getters for state (useful for the selection strategy)
}
```

---

## 3️⃣ The `Service` Template (The Central `ElevatorController`)

The controller acts as a dispatcher, taking requests and assigning them to the best elevator.

```java
// The ElevatorController is the central brain of the system.
public class ElevatorController implements Runnable {
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy selectionStrategy;
    // A thread-safe queue to hold all incoming floor requests.
    private final BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

    public ElevatorController(int numElevators, ElevatorSelectionStrategy strategy) {
        this.elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator("E" + i);
            this.elevators.add(elevator);
            new Thread(elevator, "Elevator-" + i).start(); // Start each elevator in its own thread.
        }
        this.selectionStrategy = strategy;
    }

    // This method is called by external clients (e.g., a button press).
    public void submitRequest(Request request) {
        requestQueue.offer(request);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // The controller waits for a new request to come in.
                Request request = requestQueue.take();
                // Select the best elevator for this request.
                Elevator selectedElevator = selectionStrategy.selectElevator(elevators, request);
                // Assign the request to the selected elevator.
                selectedElevator.addStop(request.getFloor());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

---

## 4️⃣ The `Strategy` Template (Pluggable Scheduling Algorithm)

The logic for choosing an elevator can be swapped out.

```java
// Pluggable logic for selecting the best elevator.
public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

// Example: A simple strategy that finds the closest idle elevator.
public class NearestIdleElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        return elevators.stream()
                .filter(e -> e.getDirection() == Direction.IDLE)
                .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - request.getFloor())))
                .orElseGet(() -> elevators.get(0)); // Fallback: just assign to the first one.
    }
}
```

---

## 5️⃣ How to TALK Concurrency in Interviews

*   **Identify Concurrency Model:** "This problem is a natural fit for the **Actor Model** or a **Thread-per-Object** concurrency model. Each `Elevator` is an independent worker with its own state and behavior, running in its own thread. The `ElevatorController` is another concurrent component that acts as a central dispatcher."
*   **Choose Primitives for Communication:** "The communication between the outside world and the system is handled by a `BlockingQueue`. This queue decouples the request submission from the request processing, making the system responsive. Pressing a button is a fast operation that just adds a request to the queue."
*   **Choose Primitives for Shared State:** "The internal state of each elevator—specifically, its list of stops—must be thread-safe because the `ElevatorController` thread writes to it, and the `Elevator`'s own thread reads from it. A `ConcurrentSkipListSet` is an excellent choice here. It's a thread-safe, sorted set, which automatically keeps the elevator's stops in a logical order for efficient movement."
*   **Explain the Division of Labor:** "The `Elevator` thread is responsible only for its own movement logic: moving floor by floor and processing its own list of stops. The `ElevatorController` thread is responsible for the system-wide logic: taking requests from the queue and using a `Strategy` to decide which elevator is best suited to handle a new request. This separation of concerns makes the system much easier to reason about."
*   **Use `volatile`:** "The `currentFloor` and `direction` of each elevator are marked as `volatile`. This is because they are written to by the elevator's own thread but are read by the `ElevatorController` thread (via the selection strategy). `volatile` ensures that the controller always sees the most up-to-date state of each elevator when making its decision."