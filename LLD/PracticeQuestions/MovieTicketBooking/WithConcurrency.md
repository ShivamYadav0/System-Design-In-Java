# ☕ Java LLD Starter Template for Movie Ticket Booking System

> **Goal:** Design a highly concurrent booking system that prevents double-booking of seats without using slow, pessimistic locks.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., seat selection, pricing, timeouts on locked seats).
2.  Identify entities (`Movie`, `Show`, `Screen`, `Seat`, `Booking`).
3.  **Identify Shared, Mutable State** (The `status` of each individual `Seat`).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., optimistic (CAS) vs. pessimistic (locking) concurrency control).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.moviebooking
 ├── domain        // Entities, including the critical, mutable Seat.
 ├── service       // Business logic with atomic, multi-step booking process.
 ├── strategy      // Pluggable behaviors (e.g., PricingStrategy).
 ├── repository    // Thread-safe data access abstractions.
 ├── factory       // Object creation (e.g., SeatFactory).
 └── api           // Controllers handling concurrent booking requests.
```

---

## 2️⃣ The `Entity` Template (Managing Atomic Seat State)

The state of each `Seat` is the most contended resource. It must be managed atomically.

```java
// Enum for managing the state of a seat in a thread-safe way.
public enum SeatStatus {
    AVAILABLE,
    LOCKED, // Temporarily held by a user during the booking process
    BOOKED
}

// The Seat is the core shared, mutable resource.
public class Seat {
    private final String seatId;
    // AtomicReference ensures atomic transitions between states.
    private final AtomicReference<SeatStatus> status;
    // To know who locked the seat.
    private final AtomicReference<String> lockedBy = new AtomicReference<>();

    public Seat(String seatId) {
        this.seatId = seatId;
        this.status = new AtomicReference<>(SeatStatus.AVAILABLE);
    }

    // Atomically tries to lock the seat for a user.
    public boolean lock(String userId) {
        if (status.compareAndSet(SeatStatus.AVAILABLE, SeatStatus.LOCKED)) {
            lockedBy.set(userId);
            return true;
        }
        return false;
    }

    // Atomically books a seat that was previously locked by the same user.
    public boolean book(String userId) {
        // Ensure the user who locked the seat is the one booking it.
        if (lockedBy.get() != null && lockedBy.get().equals(userId)) {
            return status.compareAndSet(SeatStatus.LOCKED, SeatStatus.BOOKED);
        }
        return false;
    }

    // Releases a lock if the booking process fails or times out.
    public void unlock(String userId) {
        // Only the user who locked the seat can unlock it.
        if (lockedBy.get() != null && lockedBy.get().equals(userId)) {
            status.compareAndSet(SeatStatus.LOCKED, SeatStatus.AVAILABLE);
            lockedBy.set(null);
        }
    }
}
```

---

## 3️⃣ The `Service` Template (Two-Phase Atomic Booking)

The booking process must be transactional: either all seats are booked, or none are.

```java
// BookingService handles the multi-step, concurrent booking logic.
public class BookingService {
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;

    // ... constructor

    public Optional<Booking> createBooking(String userId, String showId, List<Seat> seatsToBook) {
        List<Seat> lockedSeats = new ArrayList<>();
        boolean allSeatsLocked = true;

        // Phase 1: Attempt to lock all seats.
        for (Seat seat : seatsToBook) {
            if (seat.lock(userId)) {
                lockedSeats.add(seat);
            } else {
                allSeatsLocked = false;
                break; // One seat failed to lock, so we must abort.
            }
        }

        // If any lock failed, roll back by unlocking the seats we did manage to lock.
        if (!allSeatsLocked) {
            for (Seat lockedSeat : lockedSeats) {
                lockedSeat.unlock(userId);
            }
            return Optional.empty(); // Indicate booking failure.
        }

        // Phase 2: All seats are locked by us. Proceed to booking.
        try {
            for (Seat lockedSeat : lockedSeats) {
                if (!lockedSeat.book(userId)) {
                    // This is a severe inconsistency and should not happen if logic is correct.
                    throw new IllegalStateException("Failed to book a seat that was just locked.");
                }
            }

            Booking newBooking = new Booking(userId, showId, lockedSeats);
            bookingRepository.save(newBooking);
            return Optional.of(newBooking);

        } catch (Exception e) {
            // In case of any error during booking, unlock all seats.
            for (Seat lockedSeat : lockedSeats) {
                lockedSeat.unlock(userId);
            }
            return Optional.empty();
        }
    }
}
```

---

## 4️⃣ The `Repository` Template (Thread-Safe Show & Seat Storage)

The repository provides access to shows and their seats.

```java
// In-memory implementation for storing shows and their seats.
public class InMemoryShowRepository implements ShowRepository {
    // A map from a show ID to the list of seats for that show.
    private final Map<String, List<Seat>> seatsByShow = new ConcurrentHashMap<>();

    public void addShow(String showId, int numSeats) {
        List<Seat> seats = new CopyOnWriteArrayList<>();
        for (int i = 0; i < numSeats; i++) {
            seats.add(new Seat("S" + i));
        }
        seatsByShow.put(showId, seats);
    }

    public List<Seat> getSeatsForShow(String showId) {
        return seatsByShow.getOrDefault(showId, Collections.emptyList());
    }
}
```

---

## 5️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The state of each individual `Seat` (`AVAILABLE`, `LOCKED`, `BOOKED`) is the critical shared resource. Multiple users will try to modify the state of the same seats simultaneously, which is the central concurrency problem."
*   **Choose Primitives:** "I've used `AtomicReference<SeatStatus>` for each seat. This allows for lock-free, optimistic concurrency using `compare-and-set` (CAS) operations. This is far more scalable than acquiring a pessimistic lock on the entire show or a row of seats, as it allows concurrent bookings of different seats to proceed in parallel without blocking."
*   **Explain the Two-Phase Process:** "Booking is a multi-step, transactional operation. I've implemented a two-phase locking mechanism. **Phase 1** is `lock`: we atomically try to transition all desired seats from `AVAILABLE` to `LOCKED`. If we fail to acquire a lock on any seat, we roll back by `unlock`ing all seats we successfully locked. **Phase 2** is `book`: only if all locks are acquired, we finalize the transaction by transitioning the seats from `LOCKED` to `BOOKED`."
*   **Prevent Lost Updates and Double-Booking:** "This CAS-based approach prevents double-booking. The `seat.lock()` method is atomic. If two users call it on the same seat at the same time, the CAS operation guarantees that only one will succeed. The other will fail and will be unable to proceed with the booking."
*   **Discuss Livelock and Timeouts:** "A potential issue is that a user could lock seats and then abandon the process. This would keep the seats locked forever. In a production system, the `LOCKED` state would need an associated timestamp. A background job would periodically scan for seats that have been locked for too long (e.g., >5 minutes) and automatically unlock them, making them available again."