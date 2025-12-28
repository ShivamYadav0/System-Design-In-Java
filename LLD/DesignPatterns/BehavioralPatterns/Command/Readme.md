# 🧩 Command Design Pattern – Deep Dive

> **Mental model:** The Command pattern turns a request into a stand-alone object that contains all information about the request. This lets you parameterize methods with different requests, delay or queue a request's execution, and support undoable operations.

---

## 🔍 Problem (Realistic Scenario)

Imagine you're developing a rich text editor application. This application has a user interface with various controls:

-   A toolbar with buttons (e.g., "Bold," "Italic," "Save").
-   A main menu with menu items (e.g., "File -> Save," "Edit -> Copy").
-   Keyboard shortcuts (e.g., `Ctrl+S` for Save).

All these different UI elements are supposed to trigger actions in the application's business logic (e.g., making text bold, saving the document).

A naive approach would be to code the logic directly inside the event listeners for each UI element:

```java
// NOT a good approach
ToolbarButton saveButton = new ToolbarButton();
MenuItem saveMenuItem = new MenuItem();
Document doc = new Document();

saveButton.addActionListener(event -> {
    doc.save();
});

saveMenuItem.addActionListener(event -> {
    doc.save();
});

// And what about keyboard shortcuts? More duplicate code?
```

This leads to several problems:

1.  **Tight Coupling:** The UI components (the `Button`, the `MenuItem`) are tightly coupled to the business logic (`Document`). The button *knows* the specifics of the `Document` class and its `save` method.
2.  **Code Duplication:** The same logic (`doc.save()`) is repeated in multiple places.
3.  **No Undo/Redo:** How would you implement an "undo" feature? You can't easily reverse the `doc.save()` operation. There's no record of what was executed.
4.  **Inflexibility:** What if you want to queue operations, execute them later, or log them? The direct invocation makes this impossible.

---

## ✅ Command Solution

The Command pattern decouples the object that invokes an operation (the *Invoker*) from the object that performs the action (the *Receiver*). It does this by introducing a *Command* object that encapsulates the request itself.

### 🧱 Structure

```
+---------+          +----------------+          +------------------+          +----------+
| Client  | creates  | ConcreteCommand|--------->|     Receiver     |          | Invoker  |
|         |--------->| (e.g.,SaveCommand)|
          |          | - receiver: Receiver |          | (e.g., Document) |          | (e.g., Button) |
|         | configures | + execute()    |
          |          |                |          |------------------|          |----------|
|         |          +----------------+          | + action()       |          | + click()|
|         |                 ^                    +------------------+          |          |
|         |                 | (implements)                                       |          |
+---------+          +----------------+                                       | holds a  |
                     |  Command (Interface) |
                                                                             | command  |
                     |----------------|
                                                                             V          |
                     | + execute()    |                                       +----------+
                     +----------------+
```

-   **Command:** An interface with a single method, usually `execute()`. For undoable operations, it may also have an `undo()` method.
-   **ConcreteCommand:** Implements the `Command` interface. It holds a reference to the `Receiver` (the object that will do the work). The `execute()` method in the concrete command calls the appropriate method on the `receiver`.
-   **Receiver:** The object that contains the actual business logic. It knows how to perform the operations.
-   **Invoker:** The object that triggers the command (e.g., a button, a menu item). It holds a reference to a `Command` object and calls its `execute()` method. The invoker is completely decoupled from the receiver.
-   **Client:** Creates the `Receiver`, creates the `ConcreteCommand` (linking it with the `Receiver`), and configures the `Invoker` with the `ConcreteCommand`.

### ☕ Java Example (with Undo)

Let's model our text editor with an undoable `Bold` command.

#### 1. The Command Interface

```java
// The Command Interface
public interface Command {
    void execute();
    void undo();
}
```

#### 2. The Receiver

This is the object that does the actual work.

```java
// The Receiver
public class TextEditor {
    private String text = "Hello World";

    public void makeBold(String selection) {
        System.out.println("Making text bold: " + selection);
        // In a real app, you would modify the text's formatting
    }

    public void removeBold(String selection) {
        System.out.println("Removing bold from: " + selection);
    }

    public String getText() { return text; }
}
```

#### 3. The Concrete Command

This encapsulates the request and links the receiver.

```java
// A Concrete Command
public class BoldCommand implements Command {
    private final TextEditor editor;
    private final String selection;

    public BoldCommand(TextEditor editor, String selection) {
        this.editor = editor;
        this.selection = selection;
    }

    @Override
    public void execute() {
        editor.makeBold(selection);
    }

    @Override
    public void undo() {
        editor.removeBold(selection);
    }
}
```

#### 4. The Invoker and Command History

The invoker triggers commands, and a history stack enables undo.

```java
import java.util.Stack;

// The Invoker and Command History
public class EditorUI {
    private final Stack<Command> history = new Stack<>();

    // The invoker method takes a command and executes it
    public void onDoSomething(Command command) {
        command.execute();
        history.push(command);
    }

    // The undo method pops the last command and calls its undo method
    public void onUndo() {
        if (!history.isEmpty()) {
            Command lastCommand = history.pop();
            lastCommand.undo();
        }
    }
}
```

#### 5. Client Code

The client wires everything together.

```java
public class Application {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor(); // The Receiver
        EditorUI ui = new EditorUI();       // The Invoker

        // User wants to make "World" bold
        // The Client creates a command and passes it to the invoker
        Command boldWorld = new BoldCommand(editor, "World");
        ui.onDoSomething(boldWorld);

        // User wants to make "Hello" bold
        Command boldHello = new BoldCommand(editor, "Hello");
        ui.onDoSomething(boldHello);

        // Now, undo the last action
        System.out.println("--- Undoing last action ---");
        ui.onUndo(); // This will undo the "Hello" bold command

        // Undo again
        System.out.println("--- Undoing again ---");
        ui.onUndo(); // This will undo the "World" bold command
    }
}
```

---

## ✔ When to Use the Command Pattern

-   **Decoupling:** When you want to decouple the object that issues a request from the object(s) that process it.
-   **Undo/Redo:** When you need to support undo and redo operations. The command history is a natural fit for this.
-   **Queuing and Scheduling:** When you want to queue operations, execute them at different times, or execute them on a different thread (e.g., a task queue).
-   **Macro Commands:** You can create a `MacroCommand` that is a list of other commands, allowing you to execute complex sequences of operations as a single unit.

## 💡 Interview Line

> **“The Command pattern encapsulates a request as an object, which lets you decouple senders and receivers. This is incredibly powerful because it allows you to parameterize objects with actions, queue requests, and easily implement undoable operations by storing a history of commands.”**

---

## 🚀 Next Steps

-   Explore the **Composite Pattern**, which can be used to create `MacroCommand` objects that are composed of smaller commands.
-   Review the **Memento Pattern**, which is often used in conjunction with the Command pattern to save the state of the receiver before a command is executed, allowing for a more robust undo mechanism.
