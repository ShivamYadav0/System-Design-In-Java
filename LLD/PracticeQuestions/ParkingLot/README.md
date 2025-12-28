# ☕ Java LLD Starter Template for Parking Lot

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
com.example.parkinglot
 ├── domain        // entities & value objects (e.g., Vehicle, Ticket, Slot)
 ├── service       // business logic (e.g., ParkingService)
 ├── strategy      // pluggable behaviors (e.g., FeeCalculationStrategy)
 ├── repository    // storage abstractions (e.g., ParkingRepository)
 ├── factory       // object creation (e.g., SlotFactory)
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

// Example: A vehicle in the parking lot
public class Vehicle extends BaseEntity {
    private String licensePlate;
    private VehicleType type;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for managing the parking lot
public class ParkingService {
    private final ParkingRepository parkingRepository;
    private final FeeCalculationStrategy feeStrategy;

    public ParkingService(ParkingRepository repo, FeeCalculationStrategy strategy) {
        this.parkingRepository = repo;
        this.feeStrategy = strategy;
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        // ... find slot, create ticket, etc.
    }

    public double unparkVehicle(Ticket ticket) {
        // ... calculate fee, free up slot, etc.
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable fee calculation logic
public interface FeeCalculationStrategy {
    double calculateFee(Ticket ticket);
}

// Example: Different strategies for different vehicle types
public class CarFeeStrategy implements FeeCalculationStrategy {
    @Override
    public double calculateFee(Ticket ticket) {
        // ... logic for cars
    }
}

public class TruckFeeStrategy implements FeeCalculationStrategy {
    @Override
    public double calculateFee(Ticket ticket) {
        // ... logic for trucks
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface ParkingRepository {
    Optional<Slot> findAvailableSlot(VehicleType type);
    void saveTicket(Ticket ticket);
    Optional<Ticket> findTicketById(String ticketId);
}

// In-memory implementation for interviews
public class InMemoryParkingRepository implements ParkingRepository {
    private final Map<String, Slot> slots = new HashMap<>();
    private final Map<String, Ticket> tickets = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates slots of different types
public class SlotFactory {
    public static Slot createSlot(SlotType type, int floor) {
        switch (type) {
            case COMPACT:
                return new CompactSlot(floor);
            case LARGE:
                return new LargeSlot(floor);
            default:
                throw new IllegalArgumentException("Unknown slot type");
        }
    }
}
```
