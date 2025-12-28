# ☕ Java LLD Starter Template for Chat Application

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
com.example.chat
 ├── domain        // entities & value objects (e.g., User, Message, Channel)
 ├── service       // business logic (e.g., MessageService, ChannelService)
 ├── strategy      // pluggable behaviors (e.g., NotificationStrategy)
 ├── repository    // storage abstractions (e.g., MessageRepository, UserRepository)
 ├── factory       // object creation (e.g., MessageFactory)
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

// Example: A message in a chat channel
public class Message extends BaseEntity {
    private User sender;
    private Channel channel;
    private String content;
    private long timestamp;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for sending and receiving messages
public class MessageService {
    private final MessageRepository messageRepository;
    private final NotificationStrategy notificationStrategy;

    public MessageService(MessageRepository repo, NotificationStrategy strategy) {
        this.messageRepository = repo;
        this.notificationStrategy = strategy;
    }

    public void sendMessage(Message message) {
        messageRepository.save(message);
        notificationStrategy.sendNotification(message);
    }

    public List<Message> getMessagesForChannel(Channel channel) {
        return messageRepository.findByChannel(channel);
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable notification logic
public interface NotificationStrategy {
    void sendNotification(Message message);
}

// Example: Different strategies for sending notifications
public class PushNotificationStrategy implements NotificationStrategy {
    @Override
    public void sendNotification(Message message) {
        // ... logic to send a push notification
    }
}

public class EmailNotificationStrategy implements NotificationStrategy {
    @Override
    public void sendNotification(Message message) {
        // ... logic to send an email notification
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface MessageRepository {
    void save(Message message);
    List<Message> findByChannel(Channel channel);
}

// In-memory implementation for interviews
public class InMemoryMessageRepository implements MessageRepository {
    private final Map<String, List<Message>> messages = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of messages
public class MessageFactory {
    public static Message createTextMessage(User sender, Channel channel, String content) {
        return new TextMessage(sender, channel, content);
    }

    public static Message createImageMessage(User sender, Channel channel, String imageUrl) {
        return new ImageMessage(sender, channel, imageUrl);
    }
}
```
