# ☕ Java LLD Starter Template for Ride-Sharing Service

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

📌 These templates are **intentionally # ☕ Java LLD Starter Template for Ride-Sharing Service

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
com.example.ridesharing
 ├── domain        // entities & value objects (e.g., Rider, Driver, Trip)
 ├── service       // business logic (e.g., MatchingService, TripService)
 ├── strategy      // pluggable behaviors (e.g., PricingStrategy, DriverMatchingStrategy)
 ├── repository    // storage abstractions (e.g., TripRepository, DriverRepository)
 ├── factory       // object creation (e.g., TripFactory)
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

// Example: A trip requested by a rider
public class Trip extends BaseEntity {
    private Rider rider;
    private Driver driver;
    private Location startLocation;
    private Location endLocation;
    private TripStatus status;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for matching riders and drivers
public class MatchingService {
    private final DriverRepository driverRepository;
    private final DriverMatchingStrategy matchingStrategy;

    public MatchingService(DriverRepository dRepo, DriverMatchingStrategy strategy) {
        this.driverRepository = dRepo;
        this.matchingStrategy = strategy;
    }

    public Driver findDriver(Rider rider, Location pickupLocation) {
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers(pickupLocation);
        return matchingStrategy.findBestDriver(rider, availableDrivers);
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable driver matching logic
public interface DriverMatchingStrategy {
    Driver findBestDriver(Rider rider, List<Driver> availableDrivers);
}

// Example: Different strategies for matching drivers
public class NearestDriverMatchingStrategy implements DriverMatchingStrategy {
    @Override
    public Driver findBestDriver(Rider rider, List<Driver> availableDrivers) {
        // ... logic to find the nearest driver
    }
}

public class HighestRatedDriverMatchingStrategy implements DriverMatchingStrategy {
    @Override
    public Driver findBestDriver(Rider rider, List<Driver> availableDrivers) {
        // ... logic to find the driver with the highest rating
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface DriverRepository {
    List<Driver> findAvailableDrivers(Location location);
    Optional<Driver> findById(String driverId);
    void save(Driver driver);
}

// In-memory implementation for interviews
public class InMemoryDriverRepository implements DriverRepository {
    private final Map<String, Driver> drivers = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of trips
public class TripFactory {
    public static Trip createTrip(Rider rider, Location start, Location end) {
        return new Trip(rider, start, end);
    }
}
```
minimal** — extensibility > completeness.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.ridesharing
 ├── domain        // entities & value objects (e.g., Rider, Driver, Trip)
 ├── service       // business logic (e.g., MatchingService, TripService)
 ├── strategy      // pluggable behaviors (e.g., PricingStrategy, DriverMatchingStrategy)
 ├── repository    // storage abstractions (e.g., TripRepository, DriverRepository)
 ├── factory       // object creation (e.g., TripFactory)
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

// Example: A trip requested by a rider
public class Trip extends BaseEntity {
    private Rider rider;
    private Driver driver;
    private Location startLocation;
    private Location endLocation;
    private TripStatus status;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for matching riders and drivers
public class MatchingService {
    private final DriverRepository driverRepository;
    private final DriverMatchingStrategy matchingStrategy;

    public MatchingService(DriverRepository dRepo, DriverMatchingStrategy strategy) {
        this.driverRepository = dRepo;
        this.matchingStrategy = strategy;
    }

    public Driver findDriver(Rider rider, Location pickupLocation) {
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers(pickupLocation);
        return matchingStrategy.findBestDriver(rider, availableDrivers);
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable driver matching logic
public interface DriverMatchingStrategy {
    Driver findBestDriver(Rider rider, List<Driver> availableDrivers);
}

// Example: Different strategies for matching drivers
public class NearestDriverMatchingStrategy implements DriverMatchingStrategy {
    @Override
    public Driver findBestDriver(Rider rider, List<Driver> availableDrivers) {
        // ... logic to find the nearest driver
    }
}

public class HighestRatedDriverMatchingStrategy implements DriverMatchingStrategy {
    @Override
    public Driver findBestDriver(Rider rider, List<Driver> availableDrivers) {
        // ... logic to find the driver with the highest rating
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface DriverRepository {
    List<Driver> findAvailableDrivers(Location location);
    Optional<Driver> findById(String driverId);
    void save(Driver driver);
}

// In-memory implementation for interviews
public class InMemoryDriverRepository implements DriverRepository {
    private final Map<String, Driver> drivers = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of trips
public class TripFactory {
    public static Trip createTrip(Rider rider, Location start, Location end) {
        return new Trip(rider, start, end);
    }
}
```
