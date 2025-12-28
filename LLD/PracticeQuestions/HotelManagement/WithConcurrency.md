# ☕ Java LLD Starter Template for Hotel Management System

> **Goal:** Design a concurrent hotel booking system that can handle multiple guests booking and checking out of rooms simultaneously, preventing overbooking.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., room types, date-based booking, housekeeping).
2.  Identify entities (`Room`, `Booking`, `Guest`).
3.  **Identify Shared, Mutable State** (The set of bookings for each room over time).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., locking the entire room vs. locking a specific date range).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.hotel
 ├── domain        // Entities, including the critical Room and its Booking schedule.
 ├── service       // Business logic with clear concurrency controls for booking.
 ├── strategy      // Pluggable behaviors (e.g., PricingStrategy).
 ├── repository    // Thread-safe data access abstractions.
 ├── factory       // Object creation (e.g., RoomFactory).
 └── api           // Controllers handling concurrent booking requests.
```

---

## 2️⃣ The `Entity` Template (Managing a Room's Booking Schedule)

The core of the concurrency problem lies in safely managing the booking calendar for each room.

```java
// The Room entity, which holds its own booking schedule.
public class Room {
    private final String roomNumber;
    private final RoomType type;

    // A thread-safe, sorted map to store bookings by their start date.
    // Key: Start date of the booking. Value: The Booking object.
    private final ConcurrentNavigableMap<LocalDate, Booking> bookings = new ConcurrentSkipListMap<>();

    public Room(String roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }

    // Atomically checks for overlaps and adds a new booking.
    public boolean addBooking(Booking newBooking) {
        LocalDate newStart = newBooking.getCheckInDate();
        LocalDate newEnd = newBooking.getCheckOutDate();

        // Find a potential conflict: the latest booking that starts on or before the new one.
        Map.Entry<LocalDate, Booking> potentialConflict = bookings.floorEntry(newStart);
        if (potentialConflict != null && potentialConflict.getValue().getCheckOutDate().isAfter(newStart)) {
            return false; // The new booking starts before the previous one ends.
        }

        // Find a potential conflict: the earliest booking that starts after the new one begins.
        potentialConflict = bookings.ceilingEntry(newStart);
        if (potentialConflict != null && potentialConflict.getKey().isBefore(newEnd)) {
            return false; // The new booking ends after the next one starts.
        }
        
        // Atomically add the booking only if it's not already present.
        // This prevents the exact same booking from being added twice.
        return bookings.putIfAbsent(newStart, newBooking) == null;
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking.getCheckInDate());
    }
    
    // Getters...
}
```
> **Note:** The `addBooking` logic here has a race condition (a check-then-act problem). A `ReentrantLock` is the proper way to solve this, as shown in the service layer.

---

## 3️⃣ The `Service` Template (Synchronizing Booking Operations)

While the data structure is thread-safe, the composite `check-then-act` logic must be synchronized.

```java
// BookingService orchestrates the search and booking process.
public class BookingService {
    private final RoomRepository roomRepository;

    // A map from room number to a dedicated lock object for that room.
    private final Map<String, ReentrantLock> roomLocks;

    public BookingService(RoomRepository roomRepo) {
        this.roomRepository = roomRepo;
        // Initialize a lock for each room to allow for fine-grained locking.
        this.roomLocks = roomRepo.findAllRooms().stream()
            .collect(Collectors.toConcurrentMap(Room::getRoomNumber, room -> new ReentrantLock()));
    }

    public Optional<Booking> makeBooking(Guest guest, RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        // Find all rooms of the desired type.
        List<Room> availableRooms = roomRepository.findRoomsByType(roomType);

        // Iterate through rooms and try to book one.
        for (Room room : availableRooms) {
            ReentrantLock lock = roomLocks.get(room.getRoomNumber());
            lock.lock(); // Acquire the lock for this specific room.
            try {
                Booking newBooking = new Booking(guest, room, checkIn, checkOut);
                // The addBooking logic is now protected by a lock, making it atomic.
                if (room.addBooking(newBooking)) {
                    return Optional.of(newBooking);
                }
            } finally {
                lock.unlock(); // Always release the lock.
            }
        }
        return Optional.empty(); // No available room found.
    }
}
```

---

## 4️⃣ The `Repository` Template (Thread-Safe Room Storage)

The repository provides safe access to the collection of rooms.

```java
// Abstraction for room data storage.
public interface RoomRepository {
    List<Room> findAllRooms();
    List<Room> findRoomsByType(RoomType type);
}

// In-memory implementation for storing rooms.
public class InMemoryRoomRepository implements RoomRepository {
    private final Map<String, Room> roomsByNumber = new ConcurrentHashMap<>();

    public void addRoom(Room room) {
        roomsByNumber.put(room.getRoomNumber(), room);
    }

    @Override
    public List<Room> findAllRooms() {
        return new ArrayList<>(roomsByNumber.values());
    }

    @Override
    public List<Room> findRoomsByType(RoomType type) {
        return roomsByNumber.values().stream()
                .filter(room -> room.getType() == type)
                .collect(Collectors.toList());
    }
}
```

---

## 5️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The critical shared state is the booking calendar for each `Room`. Multiple threads (representing different guests) will try to book overlapping date ranges for the same room, which could lead to overbooking."
*   **Identify the Race Condition:** "Using a thread-safe collection like `ConcurrentNavigableMap` is a good start, but it doesn't solve the core problem. The logic to check for date overlaps involves multiple steps: check for a booking before, check for a booking after, and then insert. This is a classic **check-then-act** race condition. A thread could be preempted after checking, and another thread could add a conflicting booking before the first thread resumes."
*   **Choose Primitives to Solve It:** "To make the entire multi-step `addBooking` operation atomic, we must use a lock. The best approach is **fine-grained locking**. I've created a dedicated `ReentrantLock` for each individual `Room`. When attempting to book a room, a thread must first acquire the lock for that specific room. This ensures exclusive access to that room's calendar, preventing any other thread from modifying it at the same time."
*   **Explain the Benefits of Fine-Grained Locking:** "Using a lock per room is far more scalable than a single global lock for the whole `BookingService`. It means that different threads trying to book different rooms can proceed in parallel without blocking each other. Contention only occurs when two threads try to book the exact same room at the same time."
*   **Discuss Data Structures:** "A `ConcurrentSkipListMap` is used for the booking schedule. It's a thread-safe and sorted map, which is highly efficient for range queries, like finding bookings that overlap with a given date range. Methods like `floorEntry` and `ceilingEntry` are perfect for this use case."