# ☕ Java LLD Starter Template for Hotel Management System

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
com.example.hotel
 ├── domain        // entities & value objects (e.g., Room, Guest, Booking)
 ├── service       // business logic (e.g., BookingService, HousekeepingService)
 ├── strategy      // pluggable behaviors (e.g., PricingStrategy)
 ├── repository    // storage abstractions (e.g., RoomRepository, BookingRepository)
 ├── factory       // object creation (e.g., RoomFactory)
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

// Example: A room in the hotel
public class Room extends BaseEntity {
    private String roomNumber;
    private RoomType type;
    private RoomStatus status;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for managing bookings
public class BookingService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PricingStrategy pricingStrategy;

    public BookingService(RoomRepository rRepo, BookingRepository bRepo, PricingStrategy strategy) {
        this.roomRepository = rRepo;
        this.bookingRepository = bRepo;
        this.pricingStrategy = strategy;
    }

    public Booking makeBooking(Guest guest, RoomType roomType, Date checkIn, Date checkOut) {
        // ... find available room, calculate price, create booking, etc.
    }

    public void cancelBooking(Booking booking) {
        // ... release room, process refund, etc.
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable pricing logic
public interface PricingStrategy {
    double calculatePrice(Booking booking);
}

// Example: Different strategies for different seasons or guest types
public class SeasonalPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Booking booking) {
        // ... logic for seasonal pricing
    }
}

public class CorporatePricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Booking booking) {
        // ... logic for corporate guests
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface RoomRepository {
    Optional<Room> findAvailableRoom(RoomType type, Date checkIn, Date checkOut);
    List<Room> findAll();
    void save(Room room);
}

// In-memory implementation for interviews
public class InMemoryRoomRepository implements RoomRepository {
    private final Map<String, Room> rooms = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of rooms
public class RoomFactory {
    public static Room createRoom(RoomType type, String roomNumber) {
        switch (type) {
            case SINGLE:
                return new SingleRoom(roomNumber);
            case DOUBLE:
                return new DoubleRoom(roomNumber);
            case SUITE:
                return new SuiteRoom(roomNumber);
            default:
                throw new IllegalArgumentException("Unknown room type");
        }
    }
}
```
