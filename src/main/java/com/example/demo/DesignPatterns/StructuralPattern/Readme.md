# 🏛️ Structural Design Patterns – Deep Dive

> **Core question they answer:**
> **How can objects and classes be composed to form larger, more flexible structures?**

Structural patterns focus on simplifying how different parts of a system are connected. They help ensure that if one part of a system changes, the entire system doesn't have to be redesigned.

--- 

## 🎯 Key Goal: Composition over Inheritance

Many structural patterns are about using object composition (HAS-A relationships) to add new functionality, as opposed to classical inheritance (IS-A relationships). This leads to more flexible and less brittle systems.

---

## ✨ The Patterns

### 1. Adapter Pattern

*   **Analogy:** A travel adapter that lets you plug your US-style charger into a European wall socket.
*   **Purpose:** Allows objects with incompatible interfaces to work together.
*   **Use When:** You need to integrate a new library or a legacy component that has a different API from the rest of your system.

### 2. Decorator Pattern

*   **Analogy:** Putting toppings on an ice cream. You start with a plain base and add new "layers" of flavor (functionality).
*   **Purpose:** Adds new responsibilities to an object dynamically without altering its class.
*   **Use When:** You need to add behavior to objects at runtime, and subclassing would be impractical (e.g., too many combinations).

### 3. Proxy Pattern

*   **Analogy:** A corporate firewall or a check used to pay for something. You don't interact with the dangerous internet or the bank directly; you go through a controlled substitute.
*   **Purpose:** Provides a surrogate or placeholder for another object to control access to it.
*   **Use When:** You need to add a layer of control over an object, such as lazy initialization, access control, or logging.

### 4. Facade Pattern

*   **Analogy:** The ignition switch in a car. You just turn one key, and a complex series of actions happens under the hood (checking fuel, starting the engine, etc.).
*   **Purpose:** Provides a single, simplified interface to a complex subsystem of classes.
*   **Use When:** You have a complex system that is hard to use, and you want to provide a simple entry point for common tasks.

### 5. Composite Pattern

*   **Analogy:** A company's organizational chart. A manager can have other managers or individual employees reporting to them. The client (e.g., HR) can treat them all the same (e.g., `calculateSalary()`).
*   **Purpose:** Composes objects into tree structures to represent part-whole hierarchies. It lets clients treat individual objects and compositions of objects uniformly.
*   **Use When:** You need to represent a hierarchy of objects, like a GUI layout, a file system, or a document structure.

### 6. Bridge Pattern

*   **Analogy:** A light switch (abstraction) and a light bulb (implementation). You can have different kinds of switches (toggle, dimmer) controlling different kinds of bulbs (incandescent, LED). You can mix and match them.
*   **Purpose:** Decouples an abstraction from its implementation so that the two can vary independently.
*   **Use When:** You have a class that has variations in both its core logic (abstraction) and its platform-specific or detailed implementation. This helps avoid a "class explosion" from using inheritance for both dimensions.

### 7. Flyweight Pattern

*   **Analogy:** The characters in a word processor. The letter 'A' object is created only once. Every time you type 'A', the editor reuses that same object, but with different contextual information (like position on the page).
*   **Purpose:** Minimizes memory usage by sharing as much data as possible with other similar objects.
*   **Use When:** You need to create a very large number of similar objects that would consume too much memory.

---

##  summary

| Pattern   | Primary Goal                                        |
| :-------- | :-------------------------------------------------- |
| Adapter   | Make two incompatible interfaces work together.     |
| Decorator | Add features to an object dynamically.              |
| Proxy     | Control access to an object.                        |
| Facade    | Simplify the interface to a complex system.         |
| Composite | Treat a group of objects the same as a single one.  |
| Bridge    | Decouple abstraction from implementation.           |
| Flyweight | Share objects to reduce memory usage.               |
