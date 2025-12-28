# ☕ Java LLD Starter Template for Ride-Sharing Service

> **Goal:** Design a concurrent ride-sharing system that efficiently matches available drivers with riders in real-time.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., driver availability, rider requests, matching logic).
2.  Identify entities (`Driver`, `Rider`, `Trip`, `Location`).
3.  **Identify Shared, Mutable State** (The pool of `AVAILABLE` drivers).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., how matching strategy affects fairness and wait times).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.ridesharing
 ├── domain        // Entities like Driver, Rider, and their states.
 ├── service       // The core MatchingService that runs as a continuous process.
 ├── strategy      // Pluggable behaviors (e.g., DriverMatchingStrategy).
 ├── repository    // Thread-safe collections for drivers and riders.
 └── api           // Interfaces for riders to request rides and drivers to update status.
```

---

## 2️⃣ The `Entity` Template (Managing Driver and Rider State)

 The state of Drivers and Riders must be managed atomically as they move between available, busy, etc.

```java
// Enum for managing Driver state atomically.
public enum DriverStatus {
    AVAILABLE,
    BUSY
}

// The Driver's state is a shared resource.
public class Driver {
    private final String driverId;
    private final AtomicReference<DriverStatus> status = new AtomicReference<>(DriverStatus.AVAILABLE);
    private volatile Location currentLocation;

    // ... constructor

    // Atomically transition the driver from AVAILABLE to BUSY.
    public boolean assignToTrip() {
        return status.compareAndSet(DriverStatus.AVAILABLE, DriverStatus.BUSY);
    }

    public void completeTrip() {
        status.set(DriverStatus.AVAILABLE);
    }

    public void updateLocation(Location newLocation) {
        this.currentLocation = newLocation;
    }

    // Getters for status and location (volatile read).
}

// A simple class to represent a ride request.
public class RideRequest {
    private final Rider rider;
    private final Location pickupLocation;
    // constructor, getters
}
```

---

## 3️⃣ The `Service` Template (The Central `MatchingService`)

A central service, running in its own thread, continuously tries to match pending requests with available drivers.

```java
// The MatchingService is a long-running process that pairs riders and drivers.
public class MatchingService implements Runnable {
    private final DriverRepository driverRepository;
    private final DriverMatchingStrategy matchingStrategy;

    // Thread-safe queues for pending ride requests.
    private final BlockingQueue<RideRequest> pendingRequests = new LinkedBlockingQueue<>();

    public MatchingService(DriverRepository dRepo, DriverMatchingStrategy strategy) {
        this.driverRepository = dRepo;
        this.matchingStrategy = strategy;
    }

    // Called by the Rider via an API controller.
    public void submitRequest(RideRequest request) {
        pendingRequests.offer(request);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Wait for a rider to submit a request.
                RideRequest request = pendingRequests.take();

                // Find available drivers.
                List<Driver> availableDrivers = driverRepository.findAvailableDrivers();

                // Find the best driver using the strategy.
                Optional<Driver> bestDriverOpt = matchingStrategy.findBestDriver(request, availableDrivers);

                // If a driver is found, try to assign them the trip.
                bestDriverOpt.ifPresent(driver -> {
                    // The assignToTrip() method is atomic.
                    if (driver.assignToTrip()) {
                        // Success! Create the trip.
                        System.out.println("Matched Rider " + request.getRider().getId() + " with Driver " + driver.getDriverId());
                        // In a real system, you would create and persist a Trip object here.
                    } else {
                        // This driver was snatched by another concurrent matching process.
                        // Re-queue the request to try again in the next cycle.
                        pendingRequests.offer(request);
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

---

## 4️⃣ The `Repository` Template (Thread-Safe Driver Pool)

The repository provides safe, concurrent access to the pool of all drivers.

```java
// Abstraction for driver data storage.
public interface DriverRepository {
    List<Driver> findAvailableDrivers();
    void addDriver(Driver driver);
}

// In-memory implementation for storing and querying drivers.
public class InMemoryDriverRepository implements DriverRepository {
    // The master list of all drivers in the system.
    private final List<Driver> allDrivers = new CopyOnWriteArrayList<>();

    @Override
    public void addDriver(Driver driver) {
        allDrivers.add(driver);
    }

    @Override
    public List<Driver> findAvailableDrivers() {
        // Stream over the list and filter for available drivers.
        // This is thread-safe because CopyOnWriteArrayList provides a consistent snapshot.
        return allDrivers.stream()
                .filter(driver -> driver.getStatus().get() == DriverStatus.AVAILABLE)
                .collect(Collectors.toList());
    }
}
```

---

## 5️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The primary shared resource is the pool of `AVAILABLE` drivers. Multiple concurrent ride requests will be vying for the same set of drivers, creating a race condition for who gets assigned a driver."
*   **Choose Concurrency Model:** "I've designed this with a central `MatchingService` that runs in a dedicated thread. It pulls ride requests from a `BlockingQueue`. This decouples the rider's request action from the matching process itself, ensuring the rider gets a fast response."
*   **Explain the Race Condition & Solution:** "A race condition occurs when our `MatchingService` finds an `AVAILABLE` driver, but before it can assign the trip, another process (or another instance of the service) assigns that same driver to a different rider. My solution is to use an `AtomicReference` for the `DriverStatus` and an atomic `compare-and-set` (CAS) operation in the `assignToTrip` method. The service finds the best driver, and then *atomically attempts* to change their status from `AVAILABLE` to `BUSY`. If this CAS operation fails, we know we lost the race, and we simply re-queue the ride request to try again."
*   **Discuss Data Structures:** "I'm using a `BlockingQueue` for pending ride requests, which is a standard, thread-safe producer-consumer pattern. For the master list of drivers, a `CopyOnWriteArrayList` is a good choice. Since the list of all drivers doesn't change frequently, but we read it very often to find available ones, this data structure is highly optimized. Reads are lock-free and very fast, while writes (adding a new driver) are a bit slower, which is an acceptable trade-off."
*   **Use `volatile`:** "The `currentLocation` of a driver is marked `volatile`. This is because the driver's GPS update will happen in one thread, but the `MatchingService` will read that location from another thread to calculate proximity. `volatile` guarantees that the matching thread always sees the latest location update."