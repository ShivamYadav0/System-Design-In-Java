# ☕ Java LLD Starter Template for Chat Application

> **Goal:** Design a robust and concurrent chat system, demonstrating mastery over Java concurrency primitives and thread-safe design.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., 1-on-1 vs. group chat, message types).
2.  Identify entities (`User`, `Message`, `Channel`).
3.  **Identify Shared, Mutable State** (The most critical step for concurrency).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., performance vs. consistency).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.chat
 ├── domain        // Immutable entities (e.g., Message) & value objects.
 ├── service       // Business logic with clear concurrency controls.
 ├── strategy      // Pluggable behaviors (e.g., NotificationStrategy).
 ├── repository    // Thread-safe data access abstractions.
 ├── factory       // Object creation (e.g., MessageFactory).
 └── api           // Controllers handling concurrent user requests.
```

---

## 2️⃣ The `Entity` Template (Immutable is Best)

Make entities immutable using `final` fields and no setters. Immutable objects are inherently thread-safe.

```java
// The Message object is immutable. Once created, it cannot be changed.
public final class Message {
    private final String id;
    private final User sender;
    private final Channel channel;
    private final String content;
    private final LocalDateTime timestamp;

    // Constructor, getters only. No setters!
    // This makes the Message object thread-safe by default.
}
```

---

## 3️⃣ The `Service` Template (Handling Concurrent Operations)

Services orchestrate business logic. The key is to manage concurrent access to shared resources like chat channels.

```java
// MessageService handles the core logic of sending and retrieving messages.
public class MessageService {
    private final MessageRepository messageRepository;
    private final NotificationStrategy notificationStrategy;
    // Use a queue for decoupling message submission from processing.
    private final BlockingQueue<Message> messageProcessingQueue;

    public MessageService(MessageRepository repo, NotificationStrategy strategy) {
        this.messageRepository = repo;
        this.notificationStrategy = strategy;
        this.messageProcessingQueue = new LinkedBlockingQueue<>();
        // Start a consumer thread to process messages from the queue.
        new Thread(this::processMessages).start();
    }

    public void sendMessage(Message message) {
        // Producer: quickly add the message to the queue and return.
        // This makes the API call non-blocking.
        this.messageProcessingQueue.offer(message);
    }

    private void processMessages() {
        // Consumer: runs in a separate thread.
        while (true) {
            try {
                Message message = messageProcessingQueue.take();
                messageRepository.save(message);
                notificationStrategy.sendNotification(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public List<Message> getMessagesForChannel(Channel channel) {
        return messageRepository.findByChannel(channel);
    }
}
```

---

## 4️⃣ The `Strategy` Template (For Pluggable Behaviors)

The Strategy pattern remains the same, as strategies are often stateless and naturally thread-safe.

```java
// Pluggable notification logic. Implementations can be stateless.
public interface NotificationStrategy {
    void sendNotification(Message message);
}

// Example: Push notification strategy.
public class PushNotificationStrategy implements NotificationStrategy {
    @Override
    public void sendNotification(Message message) {
        // Logic to send a push notification.
        // This service could be a client to a remote notification service.
    }
}
```

---

## 5️⃣ The `Repository` Template (Thread-Safe Storage)

Repositories must protect their shared state. Use thread-safe collections like `ConcurrentHashMap` and `CopyOnWriteArrayList`.

```java
// Abstraction for data storage.
public interface MessageRepository {
    void save(Message message);
    List<Message> findByChannel(Channel channel);
}

// In-memory implementation designed for concurrent access.
public class InMemoryMessageRepository implements MessageRepository {
    // Use ConcurrentHashMap to allow multiple threads to read/write channels safely.
    // The value is a thread-safe list of messages.
    private final Map<String, List<Message>> messagesByChannel = new ConcurrentHashMap<>();

    @Override
    public void save(Message message) {
        String channelId = message.getChannel().getId();
        // computeIfAbsent ensures atomic creation of the message list for a new channel.
        messagesByChannel.computeIfAbsent(
            channelId,
            k -> new CopyOnWriteArrayList<>()
        ).add(message);
    }

    @Override
    public List<Message> findByChannel(Channel channel) {
        return messagesByChannel.getOrDefault(channel.getId(), Collections.emptyList());
    }
}
```
> **Trade-off:** `CopyOnWriteArrayList` is great for read-heavy workloads, as reads are lock-free. However, every write creates a new copy, which can be expensive if writes are frequent. For a write-heavy chat channel, a `synchronized` block around a standard `ArrayList` might be more performant.

---

## 6️⃣ The `Factory` Template (Stateless & Safe)

Factories are typically stateless and don't manage shared data, making them inherently thread-safe.

```java
// Creates different types of messages. This factory is stateless.
public class MessageFactory {
    public static Message createTextMessage(User sender, Channel channel, String content) {
        // Returns an immutable Message object.
        return new Message(UUID.randomUUID().toString(), sender, channel, content, LocalDateTime.now());
    }
}
```

---

## 7️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The most critical shared state is the map of channels to their message lists. Multiple users will be reading from and writing to this structure simultaneously, so it must be thread-safe."
*   **Choose Primitives:** "I've used a `ConcurrentHashMap` for the main channel lookup, which provides high-concurrency for lookups. For the message list itself, `CopyOnWriteArrayList` is a good starting point for read-heavy channels. We can discuss the trade-offs."
*   **Decouple with Queues:** "To prevent the `sendMessage` API from blocking, I've used a `BlockingQueue`. This creates a producer-consumer pattern, decoupling the incoming requests from the work of saving and sending notifications. This improves responsiveness and resilience."
*   **Embrace Immutability:** "I've designed the `Message` entity to be immutable. This is a core principle of safe concurrency, as it eliminates entire classes of bugs. Once a message is created, it cannot be changed, so it can be passed freely between threads."
*   **Mention Risks:** "Using this approach helps avoid common issues like race conditions when multiple users post in the same channel, and lost updates. We also avoid deadlocks by using high-level concurrency utilities and avoiding complex, manual lock acquisition."