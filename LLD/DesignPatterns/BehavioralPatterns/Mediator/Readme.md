# 🧩 Mediator Design Pattern – Deep Dive

> **Mental model:** The Mediator pattern provides a central object (the Mediator) that manages all communication between a group of other objects (Colleagues). This prevents the objects from referring to each other directly, promoting loose coupling.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are designing a complex GUI form for flight booking. The form has several interacting components:

-   A dropdown for `TripType` ("One Way" or "Round Trip").
-   A `DepartureDate` text field.
-   A `ReturnDate` text field.
-   A `Book` button.

The logic is as follows:

-   Initially, `ReturnDate` is disabled.
-   If the user selects `TripType` = "Round Trip", the `ReturnDate` field must be enabled.
-   If the user then switches back to "One Way", `ReturnDate` should be disabled again.
-   The `Book` button should only be enabled if all required fields are valid (e.g., `DepartureDate` is filled).

A naive approach would be to have each component hold direct references to the other components it needs to interact with.

```java
// NOT a good approach
public class FlightForm {
    // Each component knows about the others...
    private Dropdown tripTypeDropdown;
    private TextField departureDateField;
    private TextField returnDateField;
    private Button bookButton;

    // ... constructor to initialize and link them ...

    public void onTripTypeChanged() {
        if (tripTypeDropdown.getValue().equals("Round Trip")) {
            returnDateField.setEnabled(true);
        } else {
            returnDateField.setEnabled(false);
        }
    }

    public void onDateFieldChanged() {
        if (departureDateField.hasText()) {
            bookButton.setEnabled(true);
        } else {
            bookButton.setEnabled(false);
        }
    }
    // ... and so on. This becomes a mess.
}
```

This leads to a "spaghetti code" architecture:

1.  **High Coupling:** Every component is tightly coupled to several others. `tripTypeDropdown` knows about `returnDateField`. `departureDateField` knows about `bookButton`. This creates a tangled web of dependencies.
2.  **Hard to Maintain:** If you add a new component, like a "Flexible Dates" checkbox, you might have to modify several existing component classes to react to its state.
3.  **Not Reusable:** The `Dropdown` or `TextField` classes are not reusable in other contexts because they are tied to the specific logic of this particular form.

---

## ✅ Mediator Solution

The Mediator pattern solves this by creating a central `Mediator` object that handles all the communication. The components (`Colleagues`) no longer talk to each other; they only notify the `Mediator` of an event (e.g., "my value changed"). The `Mediator` then orchestrates the necessary actions on other components.

### 🧱 Structure

```
                                 +--------------------+
                                 |      Mediator      |
                                 |     (Interface)    |
                                 |--------------------|
                                 | + notify(sender)   |
                                 +--------------------+
                                          ^
                                          | (implements)
+----------------+         +--------------+-------------+
|    Colleague   |<>------>|      ConcreteMediator      |<----->+--------------------+
|   (Component)  |         | (e.g., FlightFormMediator) |       | ConcreteColleagueA |
|----------------|         +----------------------------+       | (e.g., Dropdown)   |
| - mediator     |                    ^                          +--------------------+
| + setMediator()|                    |
+----------------+                    | (knows about)
         ^                          |
         | (implements)             V
         |                 +--------------------+
         +-----------------| ConcreteColleagueB |
                           | (e.g., TextField)  |
                           +--------------------+
```

-   **Mediator (`FlightMediator`):** An interface that defines the communication method for colleagues.
-   **ConcreteMediator (`FlightFormMediator`):** Implements the coordination logic. It holds references to all the `Colleague` objects.
-   **Colleague (`GUIComponent`):** An interface or abstract class for the components.
-   **ConcreteColleague (`Dropdown`, `Button`):** A component that communicates with the `Mediator` when its state changes.

### ☕ Java Example (Chat Room)

A classic example is a chat room, where users (Colleagues) don't message each other directly but send messages to the chat room (Mediator), which then broadcasts them.

#### 1. The Mediator Interface

```java
// The Mediator Interface
public interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}
```

#### 2. The Colleague Class

```java
// The abstract Colleague
public abstract class User {
    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public abstract void send(String message);
    public abstract void receive(String message);
}
```

#### 3. The Concrete Mediator

This object contains all the complex interaction logic.

```java
import java.util.ArrayList;
import java.util.List;

// The Concrete Mediator
public class ChatRoom implements ChatMediator {
    private List<User> users;

    public ChatRoom() {
        this.users = new ArrayList<>();
    }

    @Override
    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public void sendMessage(String message, User sender) {
        System.out.println(sender.name + " sends: " + message);
        for (User user : users) {
            // Message should not be received by the user who is sending it
            if (user != sender) {
                user.receive(message);
            }
        }
    }
}
```

#### 4. The Concrete Colleague

```java
// A Concrete Colleague
public class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
        mediator.addUser(this); // Add user to the chat room
    }

    @Override
    public void send(String message) {
        mediator.sendMessage(message, this);
    }

    @Override
    public void receive(String message) {
        System.out.println(this.name + " received: " + message);
    }
}
```

#### 5. Client Code

```java
public class Application {
    public static void main(String[] args) {
        ChatMediator chatRoom = new ChatRoom();

        User user1 = new ChatUser(chatRoom, "Alice");
        User user2 = new ChatUser(chatRoom, "Bob");
        User user3 = new ChatUser(chatRoom, "Charlie");

        System.out.println("--- Alice sends a message ---");
        user1.send("Hi everyone!");

        System.out.println("\n--- Bob sends a message ---");
        user2.send("Hey Alice!");
    }
}
```

---

## ✔ When to Use the Mediator Pattern

-   **Complex Communication:** When you have a set of objects that communicate in complex, many-to-many ways, and the resulting dependency graph is a mess.
-   **Decoupling:** When you want to reuse individual components, but they currently depend too much on other specific components.
-   **Centralized Control:** When you want to centralize complex logic that is currently distributed among several objects.

## 🆚 Mediator vs. Observer

-   **Communication:** Observer is about one-way notifications from a subject to its dependents. Mediator is about centralizing complex, multi-directional communication. Colleagues can both send to and receive from the Mediator.
-   **Coupling:** In Observer, the Subject doesn't know about the concrete observers, only the `Observer` interface. Observers don't know about each other. In Mediator, the Mediator *must* know about the concrete `Colleagues` to orchestrate them. The benefit is that the `Colleagues` don't know about each other.

## 💡 Interview Line

> **“The Mediator pattern is used to reduce coupling and simplify complex communication between multiple objects. Instead of objects interacting directly in a many-to-many relationship, they all communicate through a central Mediator. This makes the individual objects reusable and the overall communication logic easier to manage and understand.”**

---

## 🚀 Next Steps

-   Review the **Observer Pattern** to understand the differences in handling communication.
-   Look at GUI frameworks like Swing or JavaFX. The relationship between UI controls and the underlying controller or form logic often follows the Mediator pattern.
