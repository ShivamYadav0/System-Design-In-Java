# 🧠 BEHAVIORAL DESIGN PATTERNS – DEEP DIVE (LLD + INTERVIEW)

> **Mental model:** Behavioral patterns are about **how objects talk to each other** to get things done.

Interviewers care only about patterns that:

*   Manage complex collaborations
*   Reduce tight coupling
*   Encapsulate behavior that changes

---

## 3️⃣ Behavioral Patterns (What Changes?)

> **What varies:** **Algorithms, responsibilities, and communication** among objects.

---

## 3.1 Chain of Responsibility Pattern — *The Escalation Pattern*

> **Mental model:** A request is passed along a chain of handlers until one of them deals with it. Think of a customer support escalation line.

### 🔍 Problem

Imagine you’re building an expense approval system.
-   A junior manager can approve expenses up to $500.
-   A senior manager can approve up to $5,000.
-   A VP can approve up to $25,000.

You could write a messy `if-else` block:

```java
class ExpenseApprover {
    public void approve(ExpenseReport report) {
        if (report.getAmount() <= 500) {
            // Logic for junior manager
        } else if (report.getAmount() <= 5000) {
            // Logic for senior manager
        } else if (report.getAmount() <= 25000) {
            // Logic for VP
        }
    }
}
```

**Problem:** This is not flexible. What if a new level (Director) is added? You have to modify this core class, which violates the Open/Closed Principle. The sender is also tightly coupled to the logic.

### ✅ Solution

Create a chain of `Approver` objects. Each approver knows its limit and has a reference to the `next` approver in the chain.

-   If it can handle the request, it does.
-   If not, it passes the request to the `next` approver.

### 🧱 Structure

```
+----------------+     +----------------+     +----------------+
|    Handler 1   |---->|    Handler 2   |---->|    Handler 3   |
| (Junior Mgr)   |     | (Senior Mgr)   |     |      (VP)      |
+----------------+     +----------------+     +----------------+
| - nextHandler  |     | - nextHandler  |     | - nextHandler  |
| + handle()     |     | + handle()     |     | + handle()     |
+----------------+     +----------------+     +----------------+
```

### ☕ Java Example

**1. The Handler Interface**

```java
// The Handler interface
public abstract class Approver {
    protected Approver nextApprover;

    public void setNext(Approver next) {
        this.nextApprover = next;
    }

    public abstract void processRequest(ExpenseReport report);
}
```

**2. Concrete Handlers**

```java
public class JuniorManager extends Approver {
    private static final double APPROVAL_LIMIT = 500;

    @Override
    public void processRequest(ExpenseReport report) {
        if (report.getAmount() <= APPROVAL_LIMIT) {
            System.out.println("Junior Manager approved expense #" + report.getId());
        } else if (nextApprover != null) {
            nextApprover.processRequest(report);
        }
    }
}

public class SeniorManager extends Approver {
    private static final double APPROVAL_LIMIT = 5000;

    @Override
    public void processRequest(ExpenseReport report) {
        if (report.getAmount() <= APPROVAL_LIMIT) {
            System.out.println("Senior Manager approved expense #" + report.getId());
        } else if (nextApprover != null) {
            nextApprover.processRequest(report);
        }
    }
}

public class VP extends Approver {
    private static final double APPROVAL_LIMIT = 25000;

    @Override
    public void processRequest(ExpenseReport report) {
        if (report.getAmount() <= APPROVAL_LIMIT) {
            System.out.println("VP approved expense #" + report.getId());
        } else {
            System.out.println("Expense #" + report.getId() + " requires executive board approval.");
        }
    }
}
```

**3. Client Code**

```java
public class Application {
    public static void main(String[] args) {
        // Build the chain
        Approver junior = new JuniorManager();
        Approver senior = new SeniorManager();
        Approver vp = new VP();

        junior.setNext(senior);
        senior.setNext(vp);

        // Process expenses
        junior.processRequest(new ExpenseReport(1, 300));   // Handled by Junior
        junior.processRequest(new ExpenseReport(2, 4500));  // Handled by Senior
        junior.processRequest(new ExpenseReport(3, 22000)); // Handled by VP
        junior.processRequest(new ExpenseReport(4, 30000)); // Needs board approval
    }
}

class ExpenseReport {
    private final int id;
    private final double amount;
    // constructor, getters
}
```

### ✔️ When to Use

-   When you don't know which object in a set will handle a request.
-   To decouple the sender of a request from its receivers.
-   When you want to issue a request to one of several objects without specifying the receiver explicitly.

> **“Chain of Responsibility lets you pass a request along a chain of handlers. It decouples the sender from the receiver, and each handler in the chain gets a chance to process the request.”**

---
## 3.2 Command Pattern — *The Encapsulation Pattern*

> **Mental model:** You package a request (a "command") into an object, which lets you pass it around, queue it, log it, or even undo it. Think of a restaurant order ticket.

### 🔍 Problem

Imagine you’re building a smart home remote. You have buttons to turn lights on, turn them off, open the garage, etc.

A naive implementation couples the remote directly to the devices:

```java
// Tightly coupled design
class SmartRemote {
    private Light livingRoomLight;
    private GarageDoor garageDoor;

    public void lightOnButtonPressed() {
        livingRoomLight.on();
    }

_     public void garageDoorOpenPressed() {
        garageDoor.open();
    }
}
```

**Problems:**
1.  **Tight Coupling:** The `SmartRemote` knows exactly what `Light` and `GarageDoor` are and what methods they have. Adding a new device (like a `Stereo`) means changing the remote's code.
2.  **No Undo/Redo:** How do you undo an action? There's no record of what was done.
3.  **No Queueing:** How do you implement a "party mode" that executes a sequence of commands?

### ✅ Solution

Decouple the remote (the **Invoker**) from the devices (the **Receivers**) using **Command** objects.

-   A `Command` object knows which `Receiver` to act on and which method to call.
-   The `Invoker` just holds a `Command` and calls its `execute()` method. It doesn't care what the command does.

### 🧱 Structure

```
+------------+       +------------------+       +----------------+
|  Invoker   |------>|      Command     |------>|    Receiver    |
|(SmartRemote)|      |   (Interface)    |      | (Light, Garage)|
+------------+       |------------------|      +----------------+
| + execute()|       | + execute()      |      | + action()     |
+------------+       | + undo()         |      +----------------+
                     +------------------+
                               ^
                               |
                +------------------------------+
                |                              |
      +------------------+          +-------------------+
      | ConcreteCommandA |          |  ConcreteCommandB |
      |  (LightOnCmd)    |          |  (GarageOpenCmd)  |
      +------------------+          +-------------------+
```

### ☕ Java Example

**1. The Receiver Classes**

```java
// Receiver 1
public class Light {
    public void on() { System.out.println("Light is ON"); }
    public void off() { System.out.println("Light is OFF"); }
}

// Receiver 2
public class GarageDoor {
    public void open() { System.out.println("Garage Door is OPEN"); }
    public void close() { System.out.println("Garage Door is CLOSED"); }
}
```

**2. The Command Interface**

```java
public interface Command {
    void execute();
    void undo();
}
```

**3. Concrete Command Classes**

```java
public class LightOnCommand implements Command {
    private Light light; // The receiver

    public LightOnCommand(Light light) { this.light = light; }

    @Override
    public void execute() { light.on(); }

    @Override
    public void undo() { light.off(); }
}

public class GarageDoorOpenCommand implements Command {
    private GarageDoor garageDoor; // The receiver

    public GarageDoorOpenCommand(GarageDoor garageDoor) { this.garageDoor = garageDoor; }

    @Override
    public void execute() { garageDoor.open(); }

    @Override
    public void undo() { garageDoor.close(); }
}
```

**4. The Invoker**

```java
// The Invoker
public class SmartRemote {
    private Command command;
    private Stack<Command> history = new Stack<>();

    public void setCommand(Command command) {
        this.command = command;
    }

    public void buttonPressed() {
        command.execute();
        history.push(command); // For undo
    }

    public void undoButtonPressed() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
```

**5. Client Code**

```java
public class Application {
    public static void main(String[] args) {
        SmartRemote remote = new SmartRemote();
        Light livingRoomLight = new Light();
        GarageDoor garageDoor = new GarageDoor();

        // Configure the remote with a command for the light
        remote.setCommand(new LightOnCommand(livingRoomLight));
        remote.buttonPressed(); // Light is ON

        // Configure the remote with a command for the garage
        remote.setCommand(new GarageDoorOpenCommand(garageDoor));
        remote.buttonPressed(); // Garage Door is OPEN

        // Undo the last action
        remote.undoButtonPressed(); // Garage Door is CLOSED
    }
}
```

### ✔️ When to Use

-   When you want to **parameterize objects with actions**.
-   To support **undo, redo, and transactional** behavior.
-   To **decouple** the object that issues a request from the object(s) that process it.

> **“The Command pattern turns a request into a stand-alone object. This decouples the invoker from the receiver, allowing you to queue requests, log them, and implement undo functionality.”**

---
## 3.3 Interpreter Pattern

(Content from `src/main/java/com/example/demo/DesignPatterns/BehavioralPatterns/Interpreter/Readme.md`)

# 🧩 Interpreter Design Pattern – Deep Dive

> **Mental model:** The Interpreter pattern provides a way to evaluate sentences in a language by building an interpreter that processes that language. It involves defining a grammatical representation for a language and an interpreter to interpret that grammar.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a rules engine for a financial application. Users need to define custom rules for fraud detection, such as `"amount > 10000 AND (country == 'US' OR device != 'MOBILE')"`.

You need a way to parse and evaluate these rule strings. The language is simple, composed of expressions (`> 10000`), logical operators (`AND`, `OR`), and parentheses.

A naive approach would be to write a monolithic parser function with a large `if-else` or `switch` statement to handle all the possible combinations of symbols and operators. You might use regular expressions to find and replace parts of the string.

```java
// NOT a good approach
public boolean evaluate(String rule, Transaction tx) {
    // This would become an unmanageable mess of string splitting, regex, and nested ifs.
    if (rule.contains("AND")) {
        // ... split the rule and recursively call ...
    } else if (rule.contains("OR")) {
        // ... it gets complicated very fast ...
    } else if (rule.contains(">")) {
        // ... parse the amount and compare ...
    }
    // ... this is not scalable or maintainable.
}
```

This approach is problematic because:

1.  **Hard to Maintain:** The parser logic is complex and centralized. Adding a new operator (e.g., `STARTS_WITH`) would require modifying the entire monolithic function.
2.  **Not Extensible:** The grammar is hardcoded. It’s difficult to add new types of expressions without significant rework.
3.  **Complex Logic:** The code becomes hard to read and debug.

---

## ✅ Interpreter Solution

The Interpreter pattern solves this by representing the grammar of the language using a set of classes. Each rule in the grammar (e.g., a terminal symbol, a non-terminal symbol, an operator) is represented by a class.

You build an Abstract Syntax Tree (AST) of these objects. To evaluate the rule, you simply call an `interpret()` method on the top-level node of the tree, which recursively calls `interpret()` on its children.

### 🧱 Structure

For a language, you typically have `TerminalExpression` (like a number or a variable) and `NonTerminalExpression` (like addition or subtraction that combines other expressions).

```
+----------------------+
| AbstractExpression   |
| (e.g., Expression)   |
|----------------------|
| + interpret(context) |
+----------------------+
          ^
          |
+---------+---------------------+
|                               |
+-----------------------+     +---------------------------+
|  TerminalExpression   |     |   NonTerminalExpression   |
| (e.g., Number)        |     | (e.g., Add, Subtract)     |
|-----------------------|     |---------------------------|
| + interpret(context)  |     | - left:  Expression       |
+-----------------------+     | - right: Expression       |
                              | + interpret(context)      |
                              +---------------------------+
```

-   **AbstractExpression (`Expression`):** An interface or abstract class with an `interpret` method.
-   **TerminalExpression (`NumberExpression`):** Represents a literal or a variable in the grammar. It has no children.
-   **NonTerminalExpression (`AddExpression`, `AndExpression`):** Represents a composition of other expressions (e.g., `left + right`). It holds references to other `Expression` objects.
-   **Context:** An object that contains global information (like variable values) that the interpreter might need.

### ☕ Java Example

Let's implement a simple interpreter for basic math operations: `+` and `-`.
We want to parse and evaluate a string like `"5 + 10 - 3"`.

#### 1. The Expression Interface

```java
// The AbstractExpression
public interface Expression {
    int interpret();
}
```

#### 2. The Terminal Expression

This represents a number.

```java
// The TerminalExpression
public class NumberExpression implements Expression {
    private final int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    public NumberExpression(String number) {
        this.number = Integer.parseInt(number.trim());
    }

    @Override
    public int interpret() {
        return this.number;
    }
}
```

#### 3. The Non-Terminal Expressions

These represent the operations.

```java
// A NonTerminalExpression for Addition
public class AddExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() + right.interpret();
    }
}

// A NonTerminalExpression for Subtraction
public class SubtractExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() - right.interpret();
    }
}
```

#### 4. The Parser (Client Code)

The client is responsible for parsing the input string and building the Abstract Syntax Tree.

```java
import java.util.Stack;

public class Application {
    public static Expression parse(String expressionStr) {
        // ... (Parser logic as before)
    }

    public static void main(String[] args) {
        String expressionStr = "5 + 10 - 3"; // Should evaluate to 12

        Expression expression = parse(expressionStr);

        int result = expression.interpret();
        System.out.println("Result of '" + expressionStr + "' is: " + result);

    }
}
```
*Note: The parser logic in a real-world scenario is the most complex part and often involves algorithms like Shunting-yard to handle operator precedence and parentheses correctly. The example above uses a simplified parser for clarity.*

---

## ✔ When to Use the Interpreter Pattern

-   **Simple Language:** When you need to interpret a simple language and can represent sentences in that language as an Abstract Syntax Tree.
-   **Extensible Grammar:** When the grammar is relatively simple, but you expect to add new operators or expressions later.
-   **Common Interface:** When you have many different variations of operations that can be composed together.

## ❌ When to Avoid

-   **Complex Grammars:** The pattern is not suitable for complex grammars. For these, using a dedicated parser generator tool like ANTLR or JavaCC is a much better approach.
-   **Performance-Critical Applications:** The pattern involves creating many small objects, which might not be the most performant solution.

> **“The Interpreter pattern is used to define a grammar for a simple language and provide an interpreter to evaluate expressions in that language. It’s great for simple rule engines or query languages, but for complex grammars, a parser generator is better.”**

---
## 3.4 Iterator Pattern

(Content from `src/main/java/com/example/demo/DesignPatterns/BehavioralPatterns/Iterator/Readme.md`)

# 🧩 Iterator Design Pattern – Deep Dive

> **Mental model:** The Iterator pattern provides a way to access the elements of an aggregate object (like a list or a collection) sequentially without exposing its underlying representation.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a social media application. You have a `UserProfile` class that holds a collection of `photos`. You might store these photos in an `ArrayList` today, but tomorrow you might want to switch to a `HashSet`.

```java
public class UserProfile {
    // What if we want to change this from ArrayList to something else?
    private ArrayList<Photo> photos;

    public ArrayList<Photo> getPhotos() {
        return photos;
    }
}
```

Now, different parts of your application need to loop through these photos.

```java
// Client Code
UserProfile profile = new UserProfile();
// ... add photos ...
ArrayList<Photo> userPhotos = profile.getPhotos(); // <-- Problem 1: Exposing internal structure

// Looping mechanism is tied to the specific collection type
for (int i = 0; i < userPhotos.size(); i++) { // <-- Problem 2: Client controls iteration
    Photo photo = userPhotos.get(i);
    System.out.println("Displaying photo: " + photo.getTitle());
}
```

This approach has two main problems:

1.  **Exposing Internal Representation:** The `UserProfile` class is forced to expose its internal `ArrayList`. This is a violation of encapsulation.
2.  **Client-Managed Traversal:** The client code is responsible for the traversal logic.

---

## ✅ Iterator Solution

The Iterator pattern extracts the traversal behavior from the collection and puts it into a separate object called an `Iterator`.

-   The collection (the `Aggregate`) provides a method to get an `Iterator`.
-   The `Iterator` provides a unified interface for traversing the collection, typically with methods like `hasNext()` and `next()`.

### ☕ Java Example

#### 1. Make the Aggregate `Iterable`

```java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// The Concrete Aggregate, now implementing Iterable
public class UserProfile implements Iterable<Photo> {
    private List<Photo> photos;

    public UserProfile() {
        this.photos = new ArrayList<>();
    }

    public void addPhoto(Photo photo) { this.photos.add(photo); }

    @Override
    public Iterator<Photo> iterator() {
        return photos.iterator();
    }
}

class Photo { /* ... */ }
```

#### 2. The Client Code

```java
public class Application {
    public static void main(String[] args) {
        UserProfile profile = new UserProfile();
        profile.addPhoto(new Photo("Vacation at Beach"));
        profile.addPhoto(new Photo("New Puppy"));

        // Because UserProfile implements Iterable, we can use the much cleaner
        // for-each loop, which uses the Iterator pattern behind the scenes!
        for (Photo photo : profile) {
            System.out.println("Displaying photo: " + photo.getTitle());
        }
    }
}
```
Now, if you change `ArrayList` to `LinkedList` or `HashSet` inside `UserProfile`, the client code **does not need to be changed at all**.

---

## ✔ When to Use the Iterator Pattern

-   **Hiding Internal Structure:** When you want to provide a uniform way to traverse different data structures without exposing their internal details.
-   **Supporting Multiple Traversals:** When you need to support multiple, simultaneous traversals of a collection.
-   **Simplifying Client Code:** When you want to simplify the client's interface for accessing collection elements.

> **“The Iterator pattern provides a standard way to traverse a collection without exposing its underlying implementation. In Java, this is achieved through the `Iterable` and `Iterator` interfaces.”**

---

(Continuing with other behavioral patterns...)
