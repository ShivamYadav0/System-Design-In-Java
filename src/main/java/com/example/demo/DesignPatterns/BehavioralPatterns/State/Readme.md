# 🧩 State Design Pattern – Deep Dive

> **Mental model:** The State pattern allows an object to alter its behavior when its internal state changes. The object will appear to change its class.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are modeling a `Document` in a workflow system. A document can be in several states: `Draft`, `Moderation`, `Published`, and `Archived`. The actions you can perform on the document depend entirely on its current state.

-   A `Draft` can be published, which moves it to `Moderation`.
-   A document in `Moderation` can be approved (moves to `Published`) or rejected (moves back to `Draft`).
-   A `Published` document can be archived.
-   An `Archived` document has no further actions.

A naive approach is to use a large `if/else` or `switch` statement inside the document class to handle state-specific behavior.

```java
public class Document {
    private String state; // "DRAFT", "MODERATION", "PUBLISHED"
    private User author;

    public Document(User author) {
        this.author = author;
        this.state = "DRAFT";
    }

    public void publish() {
        if (state.equals("DRAFT")) {
            System.out.println("Moving to moderation...");
            this.state = "MODERATION";
        } else if (state.equals("MODERATION")) {
            System.out.println("Admin is approving...");
            this.state = "PUBLISHED";
        } else if (state.equals("PUBLISHED")) {
            System.out.println("Error: Already published!");
        } // ... and so on
    }

    public void reject() {
        if (state.equals("MODERATION")) {
            System.out.println("Returning to draft...");
            this.state = "DRAFT";
        } else {
            System.out.println("Error: Cannot reject in state " + state);
        }
    }
    // ... other methods with similar conditional logic
}
```

This approach is problematic:

1.  **Massive Conditionals:** The methods become bloated with state-checking logic.
2.  **Violation of Open/Closed Principle:** Adding a new state (e.g., `NeedsCorrection`) requires modifying every single method in the `Document` class.
3.  **State Management is Scattered:** The rules for state transitions are spread across multiple methods, making them hard to understand and manage.

---

## ✅ State Solution

The State pattern solves this by creating a separate class for each possible state. The main object, called the "context" (`Document`), holds a reference to a state object that represents its current state. Instead of implementing the behavior itself, the context delegates the behavior to the current state object.

### 🧱 Structure

```
+----------------+<>----->+-------------------+
|    Context     |       |       State       |
|   (Document)   |       |   (DocumentState) |
|----------------|       |-------------------|
| - state        |       | + handlePublish() |
| + request()----|-----> | + handleReject()  |
| + setState()   |       +-------------------+
+----------------+               ^
                                 |
           +---------------------+---------------------+
           |                     |                     |
+-------------------+ +-----------------------+ +---------------------+
|  ConcreteStateA   | |   ConcreteStateB    | |   ConcreteStateC    |
|   (DraftState)    | | (ModerationState)   | |  (PublishedState)   |
+-------------------+ +-----------------------+ +---------------------+
```

-   **Context (`Document`):** Maintains an instance of a `ConcreteState` subclass that defines the current state. It delegates state-specific requests to this instance.
-   **State (`DocumentState`):** An interface or abstract class that defines the methods for all state-specific behaviors.
-   **ConcreteState (`DraftState`, `ModerationState`, etc.):** Each class implements the behavior associated with a particular state of the `Context`. When a state transition is needed, one concrete state can replace itself with another.

### ☕ Java Example

#### 1. The State Interface/Abstract Class

This defines the actions that depend on the state.

```java
// The State Abstract Class
public abstract class DocumentState {
    protected Document document;

    public void setContext(Document document) {
        this.document = document;
    }

    public abstract void publish();
    public abstract void reject();
    public abstract void archive();
}
```

#### 2. Concrete State Implementations

Each state is its own class, encapsulating its own rules.

```java
// Concrete State for "Draft"
public class DraftState extends DocumentState {
    @Override
    public void publish() {
        System.out.println("Moving document from Draft to Moderation.");
        document.changeState(new ModerationState()); // State transition
    }
    @Override public void reject() { System.out.println("Cannot reject a draft."); }
    @Override public void archive() { System.out.println("Cannot archive a draft."); }
}

// Concrete State for "Moderation"
public class ModerationState extends DocumentState {
    @Override
    public void publish() {
        System.out.println("Approving document. Moving to Published.");
        document.changeState(new PublishedState()); // State transition
    }
    @Override
    public void reject() {
        System.out.println("Rejecting document. Returning to Draft.");
        document.changeState(new DraftState()); // State transition
    }
    @Override public void archive() { System.out.println("Cannot archive a document in moderation."); }
}

// Concrete State for "Published"
public class PublishedState extends DocumentState {
    @Override public void publish() { System.out.println("Document is already published."); }
    @Override public void reject() { System.out.println("Cannot reject a published document."); }
    @Override
    public void archive() {
        System.out.println("Archiving the document.");
        document.changeState(new ArchivedState()); // State transition
    }
}

// Final State
public class ArchivedState extends DocumentState {
    @Override public void publish() { System.out.println("Cannot publish an archived document."); }
    @Override public void reject() { System.out.println("Cannot reject an archived document."); }
    @Override public void archive() { System.out.println("Document is already archived."); }
}
```

#### 3. The Context Class

The `Document` class now delegates all state-specific logic.

```java
// The Context
public class Document {
    private DocumentState state;

    public Document() {
        // Initial state
        this.state = new DraftState();
        this.state.setContext(this);
    }

    // The context delegates behavior to the current state object.
    public void publish() {
        state.publish();
    }

    public void reject() {
        state.reject();
    }

    public void archive() {
        state.archive();
    }

    // Method for states to trigger a transition
    void changeState(DocumentState newState) {
        this.state = newState;
        this.state.setContext(this);
    }
}
```

#### 4. Client Code

```java
public class Application {
    public static void main(String[] args) {
        Document doc = new Document();
        System.out.println("--- Document created (in Draft state) ---");

        doc.reject();  // Does nothing
        doc.publish(); // Moves to Moderation

        System.out.println("
--- Document in Moderation ---");
        doc.reject();  // Moves back to Draft

        System.out.println("
--- Document back in Draft ---");
        doc.publish(); // Moves to Moderation again

        System.out.println("
--- Document in Moderation ---");
        doc.publish(); // Moves to Published

        System.out.println("
--- Document in Published ---");
        doc.archive(); // Moves to Archived
    }
}
```

---

## ✔ When to Use the State Pattern

-   **State-Dependent Behavior:** When an object's behavior depends on its state, and it must change its behavior at runtime depending on that state.
-   **Numerous Conditionals:** When you have large `if/else` or `switch` statements that select behavior based on the object's current state.
-   **Cleaner State Transitions:** When you want to formalize and centralize the state transition logic, making it more explicit and easier to manage.

## 🆚 State vs. Strategy Pattern

State and Strategy have nearly identical class diagrams, but their **intent** is fundamentally different.

-   **State:** The focus is on managing the **internal state** of an object and allowing the object's behavior to change as its state changes. State transitions are often managed internally by the context or the states themselves.
-   **Strategy:** The focus is on providing **interchangeable algorithms** for a client to use. The client is usually aware of the different strategies and actively chooses one to pass to the context object.

## 💡 Interview Line

> **“The State pattern allows an object to change its behavior when its internal state changes. We encapsulate each state into a separate class and the context object delegates its work to the current state object. It’s a clean alternative to having large, state-based conditional logic in your context class.”**

---

## 🚀 Next Steps

-   Review the **Strategy Pattern** to fully grasp the difference in intent despite the structural similarities.
-   Explore the **Finite State Machine (FSM)** concept, as the State pattern is essentially an object-oriented implementation of an FSM.
