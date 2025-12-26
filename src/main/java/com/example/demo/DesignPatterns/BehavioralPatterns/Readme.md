# 🧠 Behavioral Design Patterns – Deep Dive

> **Core question they answer:**
> **How can objects effectively communicate, collaborate, and distribute responsibilities?**

Behavioral patterns are all about the communication and interaction between objects. They help create systems where objects are loosely coupled and can work together to accomplish tasks that no single object could handle alone.

---

## 🎯 Key Goal: Encapsulating Behavior

Many behavioral patterns focus on encapsulating what varies. This could be an algorithm (Strategy), a request (Command), a state (State), or the way objects are connected (Mediator, Chain of Responsibility).

---

## ✨ The Patterns

### 1. Chain of Responsibility

*   **Analogy:** A customer service escalation path. A request goes to a junior representative first. If they can't handle it, it's passed up to a senior rep, then a manager, and so on, until someone handles it.
*   **Purpose:** Avoids coupling the sender of a request to its receiver by giving more than one object a chance to handle the request.
*   **Use When:** You have a request that can be handled by one of several objects, but you don't know which one in advance.

### 2. Command

*   **Analogy:** Ordering food at a restaurant. You (the client) create an order (the command object) and give it to the waiter (the invoker). The waiter passes it to the kitchen (the receiver), which knows how to prepare the meal. You don't need to know who the chef is or how they cook.
*   **Purpose:** Turns a request into a stand-alone object that contains all information about the request. This lets you parameterize methods with different requests, queue or log requests, and support undoable operations.
*   **Use When:** You want to queue operations, make them undoable, or decouple the object that issues a request from the object that performs it.

### 3. Interpreter

*   **Analogy:** A musician reading sheet music. The sheet music is a language, and the musician is the interpreter who translates the notes (grammar) into music (the outcome).
*   **Purpose:** Given a language, defines a representation for its grammar along with an interpreter that uses the representation to interpret sentences in the language.
*   **Use When:** You have a simple language that you need to parse and execute, like a search query or a rules engine.

### 4. Iterator

*   **Analogy:** The remote control for a TV. The remote has `next` and `previous` buttons, allowing you to cycle through channels without needing to know how the TV stores or tunes into them.
*   **Purpose:** Provides a way to access the elements of an aggregate object sequentially without exposing its underlying representation.
*   **Use When:** You need to traverse a collection, but you want to decouple your client code from the collection's specific implementation.

### 5. Mediator

*   **Analogy:** An air traffic control tower. Airplanes don't communicate directly with each other to coordinate landings and takeoffs; they all talk to the tower, which manages the communication and prevents chaos.
*   **Purpose:** Defines a central object that encapsulates how a set of objects interact. This promotes loose coupling by keeping objects from referring to each other explicitly.
*   **Use When:** You have a set of objects that communicate in complex, many-to-many ways, and you want to centralize that logic to make it more manageable.

### 6. Memento

*   **Analogy:** A video game save point. You create a "memento" of your progress that you can restore later, without the save file needing to know all the internal details of your character's state.
*   **Purpose:** Captures and externalizes an object's internal state so that the object can be restored to this state later, without violating encapsulation.
*   **Use When:** You need to implement undo/redo functionality or create checkpoints in a process.

### 7. Observer

*   **Analogy:** A magazine subscription. You (the observer) subscribe to a publisher (the subject). When a new issue is released, the publisher automatically notifies all subscribers.
*   **Purpose:** Defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.
*   **Use When:** A change in one object requires changing others, and you don't want to tightly couple the objects.

### 8. State

*   **Analogy:** The remote control buttons for `Play` and `Pause` on a media player. The behavior of the button press depends on the current state of the player (e.g., pressing `Play` does nothing if it's already playing).
*   **Purpose:** Allows an object to alter its behavior when its internal state changes. The object will appear to change its class.
*   **Use When:** An object's behavior depends on its state, and it has many conditional statements that depend on that state.

### 9. Strategy

*   **Analogy:** A navigation app. You enter a destination, and you can choose a `Strategy` for the route: the fastest route, the shortest route, or the one that avoids tolls.
*   **Purpose:** Defines a family of algorithms, encapsulates each one, and makes them interchangeable. Strategy lets the algorithm vary independently from clients that use it.
*   **Use When:** You have different variations of an algorithm, and you want to be able to switch between them at runtime.

### 10. Template Method

*   **Analogy:** Making a sandwich. The `template method` is `makeSandwich`: (1) get bread, (2) add fillings, (3) get bread. Subclasses (like `HamSandwich` or `VeggieSandwich`) must implement `addFillings`, but the overall steps are fixed.
*   **Purpose:** Defines the skeleton of an algorithm in a base class but lets subclasses override specific steps of the algorithm without changing its structure.
*   **Use When:** You want to let subclasses redefine certain steps of an algorithm, but not the algorithm's overall structure.

### 11. Visitor

*   **Analogy:** A tax accountant visiting a company. The accountant (`Visitor`) performs the same operation (`calculateTaxes`) on different types of employees (`Elements` like `Manager`, `Developer`). The employee objects just need to `accept` the visitor.
*   **Purpose:** Represents an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements on which it operates.
*   **Use When:** You have a stable set of classes, but you need to perform different operations on them, and you don't want to clutter the classes with these operations.
