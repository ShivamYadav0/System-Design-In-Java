# ☕ Java LLD Starter Template for Movie Ticket Booking System

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
com.example.moviebooking
 ├── domain        // entities & value objects (e.g., Movie, Show, Seat, Booking)
 ├── service       // business logic (e.g., BookingService)
 ├── strategy      // pluggable behaviors (e.g., PricingStrategy)
 ├── repository    // storage abstractions (e.g., ShowRepository, BookingRepository)
 ├── factory       // object creation (e.g., SeatFactory)
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

// Example: A movie showing in the theater
public class Show extends BaseEntity {
    private Movie movie;
    private Screen screen;
    private LocalDateTime showTime;
    private Map<String, Seat> seats;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for managing bookings
public class BookingService {
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final PricingStrategy pricingStrategy;

    public BookingService(ShowRepository sRepo, BookingRepository bRepo, PricingStrategy strategy) {
        this.showRepository = sRepo;
        this.bookingRepository = bRepo;
        this.pricingStrategy = strategy;
    }

    public Booking createBooking(User user, Show show, List<Seat> seats) {
        // ... check seat availability, calculate price, create booking, etc.
    }

    public void cancelBooking(Booking booking) {
        // ... release seats, process refund, etc.
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

// Example: Different strategies for different days or seat types
public class WeekdayPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Booking booking) {
        // ... logic for weekday pricing
    }
}

public class WeekendPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(Booking booking) {
        // ... logic for weekend pricing
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface ShowRepository {
    Optional<Show> findById(String showId);
    List<Show> findByMovie(Movie movie);
    void save(Show show);
}

// In-memory implementation for interviews
public class InMemoryShowRepository implements ShowRepository {
    private final Map<String, Show> shows = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of seats
public class SeatFactory {
    public static Seat createSeat(SeatType type, String seatNumber, int row) {
        switch (type) {
            case REGULAR:
                return new RegularSeat(seatNumber, row);
            case PREMIUM:
                return new PremiumSeat(seatNumber, row);
            default:
                throw new IllegalArgumentException("Unknown seat type");
        }
    }
}
```
