# 🧩 Prototype Design Pattern – Deep Dive

> **Mental model:** The Prototype pattern allows you to create new objects by copying an existing object, known as the prototype. It avoids the cost of creating complex objects from scratch.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a graphical editor. Users can create various shapes like circles, rectangles, and complex, custom-made shapes composed of many smaller parts. Let's say a user creates a complex, detailed `Vehicle` object on the canvas, with hundreds of vertices and specific color gradients.

Now, the user wants to duplicate this `Vehicle` object. How would you implement this?

- **Creating from Scratch:** You could save the `Vehicle`'s configuration (type, vertices, colors, etc.) and then reconstruct a new `Vehicle` from that configuration. This is slow and inefficient, especially for complex objects. You would have to re-run the entire construction logic.

- **Duplication Logic in Client:** The client code (e.g., the UI layer) could read all the properties of the original `Vehicle` and manually set them on a new `Vehicle` instance. This tightly couples the client to the internal details of the `Vehicle` class, violating encapsulation. If you add a new property to `Vehicle`, you have to update the client's duplication logic.

What you need is a way to ask an object to make a copy of itself, without the client needing to know the details of *how* that copy is made.

---

## ✅ Prototype Solution

The Prototype pattern solves this by providing a cloning mechanism. You define a common interface for all objects that can be cloned. This interface typically has a single `clone()` method. The object itself is responsible for creating a copy with the same state.

### 🧱 Structure

```
+-------------------+
|     Prototype     |
|   (e.g., Shape)   |
|-------------------|
| + clone(): Shape  |
+-------------------+
        ^
        |
+-----------------+-----------------+
|                 |                 |
+-------------------+      +--------------------+
| ConcretePrototypeA|
|  (e.g., Circle)   |      | ConcretePrototypeB |
+-------------------+      | (e.g., Rectangle)  |
| + clone(): Shape  |      +--------------------+
|                   |      | + clone(): Shape   |
+-------------------+      +--------------------+
```

- **Prototype:** An interface that declares the `clone()` method.
- **ConcretePrototype:** A class that implements the `clone()` method. It performs a field-by-field copy of its own state to the new object.

In Java, this is often achieved by implementing the `Cloneable` marker interface and overriding the `Object.clone()` method.

### ☕ Java Example

Let's model our shape editor using the Prototype pattern.

#### 1. The Prototype (Abstract Class)

We define an abstract `Shape` class that implements `Cloneable`. It handles the cloning logic.

```java
// The Prototype: an abstract class implementing Cloneable
public abstract class Shape implements Cloneable {
    private int x;
    private int y;
    private String color;

    public Shape(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    // Copy constructor
    public Shape(Shape source) {
        this.x = source.x;
        this.y = source.y;
        this.color = source.color;
    }

    public abstract Shape clone();

    // Getters and Setters...
}
```

#### 2. Concrete Prototypes

These concrete classes define their specific properties and implement the `clone()` method by calling their copy constructor.

```java
// Concrete Prototype: Circle
public class Circle extends Shape {
    private int radius;

    public Circle(int x, int y, String color, int radius) {
        super(x, y, color);
        this.radius = radius;
    }

    // The copy constructor is crucial for the clone operation
    public Circle(Circle source) {
        super(source);
        this.radius = source.radius;
    }

    @Override
    public Shape clone() {
        // Create a new object by passing self to the copy constructor
        return new Circle(this);
    }
}

// Concrete Prototype: Rectangle
public class Rectangle extends Shape {
    private int width;
    private int height;

    public Rectangle(int x, int y, String color, int width, int height) {
        super(x, y, color);
        this.width = width;
        this.height = height;
    }

    public Rectangle(Rectangle source) {
        super(source);
        this.width = source.width;
        this.height = source.height;
    }

    @Override
    public Shape clone() {
        return new Rectangle(this);
    }
}
```

#### 3. The Client Code

The client can now clone any shape without knowing its concrete class.

```java
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        List<Shape> originalShapes = new ArrayList<>();

        // Create some original shapes
        Circle circle = new Circle(10, 20, "Red", 15);
        Rectangle rectangle = new Rectangle(30, 40, "Blue", 50, 60);

        originalShapes.add(circle);
        originalShapes.add(rectangle);

        // Clone the shapes
        List<Shape> clonedShapes = new ArrayList<>();
        for (Shape shape : originalShapes) {
            // The client just calls clone() without knowing the concrete type
            clonedShapes.add(shape.clone());
        }

        // The clonedShapes list now contains new objects that are exact copies
        // of the originals, but are completely independent.
    }
}
```

---

### Shallow vs. Deep Copy

This is the most critical aspect of the Prototype pattern.

- **Shallow Copy:** The default behavior of `Object.clone()`. It copies the primitive fields and the references to object fields. If the original object contains a reference to another object, the copy will contain a reference to the **same** object. Changing the shared object in the copy will also affect the original.
- **Deep Copy:** A deep copy involves creating new instances of all the objects that the original object references. This ensures that the original and the copy are fully independent. To achieve a deep copy, you must override the `clone()` method and explicitly create new instances for your object fields.

**Rule of Thumb:** If your object contains only primitive fields and immutable objects (like `String`), a shallow copy is sufficient. If it contains references to mutable objects, you almost always need a deep copy.

---

## ✔ When to Use the Prototype Pattern

- **Expensive Object Creation:** When creating an object is more expensive or complex than copying an existing one (e.g., requires database queries, network calls, or intensive computation).
- **Decoupling from Concrete Classes:** When your client code should not be coupled to the concrete classes of the objects it needs to create.
- **Pre-configured Objects:** When you have a set of pre-configured objects (a "prototype registry") that you want to reuse by cloning.

## 💡 Interview Line

> **“The Prototype pattern is about creating new objects by copying an existing one. It's particularly useful when object creation is expensive. The key challenge is implementing the cloning process correctly, paying close attention to the difference between a shallow and a deep copy.”**

---

## 🚀 Next Steps

- Review the **Factory Method** and **Abstract Factory** patterns to see other ways of decoupling clients from concrete classes during object creation.
- Understand how the Prototype pattern can be used to implement a **Prototype Registry** or **Cache** for managing and reusing objects.
