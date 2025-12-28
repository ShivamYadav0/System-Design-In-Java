# 🧩 Memento Design Pattern – Deep Dive

> **Mental model:** The Memento pattern allows you to capture and restore an object's previous state without revealing the details of its implementation. Think of it as a fancy undo/redo or checkpoint system.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a text editor. The `Editor` class contains the text content, cursor position, font style, etc. You want to implement an "undo" feature. Every time the user performs an action (like typing, resizing, or changing style), you need to save a snapshot of the editor's state. When the user hits "Undo", you need to restore the editor to its previous state.

A naive approach might be to have some other object (e.g., a `History` class) directly reach into the `Editor` object and copy all its state.

```java
// The object whose state we want to save
public class Editor {
    private String content;
    private int cursorX, cursorY;
    private String fontName;
    // ... getters and setters for all these fields
}

// The object that tries to manage history
public class History {
    public void save(Editor editor) {
        // Problem: The History object needs to know about all the internal fields of the Editor.
        // This breaks encapsulation!
        String content = editor.getContent();
        int x = editor.getCursorX();
        // ... and so on. It's very intrusive.
    }
}
```

This approach is deeply flawed:

1.  **Violation of Encapsulation:** The `History` object is tightly coupled to the `Editor`'s internal structure. If you add a new field to `Editor` (e.g., `fontSize`), you must also update the `History` class to save and restore it. This is a maintenance nightmare.
2.  **State Can Be Modified:** If the state is saved as a set of public fields or a simple data object, another part of the program could accidentally modify the saved state, corrupting the history.

---

## ✅ Memento Solution

The Memento pattern solves this by delegating the responsibility of creating and restoring a state snapshot to the object that actually owns the state (the `Editor`). The pattern introduces three roles:

-   **Originator (`Editor`):** The object that knows how to save and restore its own state. It creates a `Memento` containing a snapshot of its current state.
-   **Memento (`EditorState`):** A simple, opaque object that stores the state of the Originator. It should be immutable or at least not allow external objects (except the Originator) to change its contents. This protects the integrity of the saved state.
-   **Caretaker (`History`):** An object that is responsible for keeping the Mementos. It asks the Originator for a Memento to save a checkpoint and passes a Memento back to the Originator to undo an action. The Caretaker *never* inspects or modifies the content of the Memento.

### 🧱 Structure

```
+------------------+ creates/restores from +------------------+
|    Originator    |<>--------------------->|      Memento     |
|     (Editor)     |                       |  (EditorState)   |
|------------------|                       |------------------|
| - state          |                       | - state          |
| + save()         |                       +------------------+
| + restore(m)     |
+------------------+                                  ^
         ^                                            |
         | (requests save/restore)                    | (holds)
         |
+------------------+
|    Caretaker     |
|     (History)    |
|------------------|
| - mementos: Stack|
| + undo()         |
| + doSomething()  |
+------------------+
```

### ☕ Java Example

#### 1. The Memento Class

The key here is to make the state `final` so the Memento is immutable. The state is only accessible to the `Originator` (in Java, this can be enforced with package-private visibility or, as shown here, by convention).

```java
// The Memento
public class EditorState {
    // The state is final and private. No one can change it after creation.
    private final String content;

    public EditorState(String content) {
        this.content = content;
    }

    // The Originator needs this to restore its state, but no one else should.
    // We make it package-private or rely on the Originator being the only one who knows
    // how to use the state.
    String getContent() {
        return content;
    }
}
```

#### 2. The Originator Class

```java
// The Originator
public class Editor {
    private String content;

    public void type(String words) {
        this.content = (this.content == null ? "" : this.content) + words;
    }

    // Creates a Memento containing a snapshot of its current state.
    public EditorState save() {
        System.out.println("Originator: Saving state...");
        return new EditorState(this.content);
    }

    // Restores its state from a Memento object.
    public void restore(EditorState memento) {
        this.content = memento.getContent();
        System.out.println("Originator: State restored.");
    }

    public String getContent() {
        return content;
    }
}
```

#### 3. The Caretaker Class

```java
import java.util.Stack;

// The Caretaker
public class History {
    // The Caretaker holds a stack of Mementos but never looks inside them.
    private Stack<EditorState> history = new Stack<>();
    private Editor editor;

    public History(Editor editor) {
        this.editor = editor;
    }

    // Triggered by an action in the UI
    public void save() {
        history.push(editor.save());
    }

    // The "Undo" button action
    public void undo() {
        if (!history.isEmpty()) {
            System.out.println("Caretaker: Undoing...");
            editor.restore(history.pop());
        } else {
            System.out.println("Caretaker: Nothing to undo.");
        }
    }
}
```

#### 4. Client Code

```java
public class Application {
    public static void main(String[] args) {
        Editor editor = new Editor();
        History history = new History(editor);

        editor.type("This is the first sentence. ");
        history.save(); // Save the state after the first action

        editor.type("This is the second. ");
        history.save(); // Save the state after the second action

        editor.type("And this is the third.");

        System.out.println("\nCurrent Content: " + editor.getContent());

        // Now, let's undo!
        history.undo(); // Reverts the third sentence
        System.out.println("After first undo: " + editor.getContent());

        history.undo(); // Reverts the second sentence
        System.out.println("After second undo: " + editor.getContent());

        history.undo(); // Nothing left to undo
    }
}
```

---

## ✔ When to Use the Memento Pattern

-   **Undo/Redo Functionality:** This is the classic use case.
-   **Checkpoints/Transactions:** When you need to save an object's state to be able to roll back to it later, such as at checkpoints in a complex process or before committing a database transaction.
-   **Preserving Encapsulation:** When you must save an object's state but want to avoid breaking its encapsulation by exposing its internal implementation details.

## 💡 Interview Line

> **“The Memento pattern is used to implement features like undo or checkpoints. It lets you capture and externalize an object's internal state so it can be restored later, all without violating the object’s encapsulation. The object itself (the Originator) is responsible for creating a state snapshot (a Memento), and a Caretaker object holds onto the Memento without knowing what’s inside it.”**

---

## 🚀 Next Steps

-   Explore the **Command Pattern**. Memento is often used in conjunction with the Command pattern, where a command object is responsible for performing an action and can hold a Memento to allow that action to be undone.
-   Consider how to handle the storage of Mementos. For a simple undo history, a `Stack` is perfect. For more complex scenarios, you might need a different data structure.
