# 🏭 Creational Design Patterns – Deep Dive

> **Core question they answer:**
> **How can objects be created in a manner that is flexible and decoupled from the client?**

Creational patterns provide various object creation mechanisms, which increase flexibility and reuse of existing code. They help hide the complexities of how your objects are created.

---

## 🎯 Key Goal: Decoupling Object Creation

The main goal is to make a system independent of how its objects are created, composed, and represented. Instead of instantiating objects directly using the `new` operator, you delegate this responsibility to a special factory method or object.

---

## ✨ The Patterns

### 1. Singleton Pattern

*   **Analogy:** The president of a country. There can only be one at any given time.
*   **Purpose:** Ensures a class has only one instance and provides a global point of access to it.
*   **Use When:** You need exactly one instance of a class to coordinate actions across the system, such as a logger, a database connection pool, or a configuration manager.

### 2. Factory Method Pattern

*   **Analogy:** A logistics company (`Creator`). The `planDelivery()` method is the factory method. Subclasses like `RoadLogistics` and `SeaLogistics` implement this method to create the right transport object (`Truck` or `Ship`).
*   **Purpose:** Defines an interface for creating an object, but lets subclasses alter the type of objects that will be created.
*   **Use When:** A class cannot anticipate the class of objects it must create. You want to provide a way for subclasses to specify the objects to create.

### 3. Abstract Factory Pattern

*   **Analogy:** A furniture store that sells matching sets (`Victorian`, `Modern`). The Abstract Factory is the `FurnitureFactory` interface. Concrete factories (`VictorianFurnitureFactory`, `ModernFurnitureFactory`) create a whole family of related products (e.g., a `VictorianChair` and a `VictorianTable`).
*   **Purpose:** Provides an interface for creating families of related or dependent objects without specifying their concrete classes.
*   **Use When:** Your system needs to be independent of how its products are created, and it needs to work with multiple families of related products.

### 4. Builder Pattern

*   **Analogy:** Ordering a custom sandwich at Subway. You (the `Director`) tell the employee (the `Builder`) what you want step-by-step ("add lettuce," "add tomato"). The builder assembles the sandwich, and you get the final product at the end.
*   **Purpose:** Separates the construction of a complex object from its representation, so that the same construction process can create different representations.
*   **Use When:** The algorithm for creating a complex object should be independent of the parts that make up the object. This is especially useful for objects with many configuration options (like an `HttpClient` with timeouts, proxies, etc.).

### 5. Prototype Pattern

*   **Analogy:** Cloning a sheep. Instead of creating a new sheep from scratch, you take an existing sheep and make an exact copy.
*   **Purpose:** Specifies the kinds of objects to create using a prototypical instance, and creates new objects by copying this prototype.
*   **Use When:** Creating an object is expensive (e.g., requires a database call), and it's easier to copy an existing instance. Also used when you want to avoid a large number of subclasses of a factory.

---

## summary

| Pattern          | Primary Goal                                                     |
| :--------------- | :--------------------------------------------------------------- |
| Singleton        | Ensure a class has only one instance.                            |
| Factory Method   | Let subclasses decide which class to instantiate.                |
| Abstract Factory | Create families of related objects.                              |
| Builder          | Construct complex objects step-by-step.                          |
| Prototype        | Create new objects by copying an existing one.                   |
