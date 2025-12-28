# 🧩 Bridge Design Pattern – Deep Dive

> **Mental model:** The Bridge pattern decouples an abstraction from its implementation so that the two can vary independently. It is used to avoid a class explosion by favoring composition over inheritance.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are developing a user interface toolkit that allows you to draw different shapes, such as `Circle` and `Square`. You also need to support rendering these shapes on different operating systems (e.g., Windows, macOS, Linux).

If you use inheritance to model this, you will quickly run into a **class explosion**. You would need a class for each combination of shape and operating system:

- `WindowsCircle`
- `MacCircle`
- `LinuxCircle`
- `WindowsSquare`
- `MacSquare`
- `LinuxSquare`

This hierarchy is rigid and unmanageable. Adding a new shape (e.g., `Triangle`) would require you to create three new classes (`WindowsTriangle`, `MacTriangle`, `LinuxTriangle`). Similarly, adding a new operating system (e.g., `Android`) would require you to add a new class for every existing shape.

This is a classic example of where inheritance fails to provide a flexible solution.

---

## ✅ Bridge Solution

The Bridge pattern solves this problem by separating the two dimensions of variation (`Shape` and `Renderer`) into two separate class hierarchies. The `Shape` hierarchy (the **Abstraction**) will hold a reference to an object from the `Renderer` hierarchy (the **Implementation**).

This "bridge" allows you to combine any shape with any renderer at runtime.

### 🧱 Structure

```
+------------------+       +--------------------+
|   Abstraction    |------>|    Implementor     |
|    (e.g., Shape) |       |   (e.g., Renderer)   |
+------------------+       +--------------------+
        ^
        |
+------------------+       +--------------------+
| RefinedAbstraction|
| (e.g., Circle, Square) |
+------------------+       +--------------------+
                                       ^
                                       |
                       +-----------------+-----------------+
                       |                                 |
             +-----------------------+     +-----------------------+
             | ConcreteImplementorA  |     | ConcreteImplementorB  |
             | (e.g., WindowsRenderer)|     |  (e.g., MacRenderer)  |
             +-----------------------+     +-----------------------+
```

- **Abstraction:** The high-level control layer for some entity. It defines the client-facing interface and holds a reference to an `Implementor` object.
- **RefinedAbstraction:** Extends the `Abstraction` to provide concrete variations (e.g., `Circle`, `Square`).
- **Implementor:** An interface that defines the operations for the implementation. The `Abstraction` only depends on this interface.
- **ConcreteImplementor:** Concrete classes that implement the `Implementor` interface (e.g., `WindowsRenderer`, `MacRenderer`).

### ☕ Java Example

Let's apply the Bridge pattern to our shape-drawing problem.

#### 1. The Implementor Interface

This interface defines the low-level rendering operations that are specific to each platform.

```java
// Implementor Interface
public interface Renderer {
    void renderCircle(float radius);
    void renderSquare(float side);
}
```

#### 2. Concrete Implementors

These are the concrete platform-specific renderers.

```java
// Concrete Implementor A
public class WindowsRenderer implements Renderer {
    @Override
    public void renderCircle(float radius) {
        System.out.println("Drawing a circle on Windows with radius " + radius);
    }

    @Override
    public void renderSquare(float side) {
        System.out.println("Drawing a square on Windows with side " + side);
    }
}

// Concrete Implementor B
public class MacRenderer implements Renderer {
    @Override
    public void renderCircle(float radius) {
        System.out.println("Drawing a circle on macOS with radius " + radius);
    }

    @Override
    public void renderSquare(float side) {
        System.out.println("Drawing a square on macOS with side " + side);
    }
}
```

#### 3. The Abstraction

The abstract `Shape` class holds a reference to a `Renderer` object. The drawing logic is delegated to this renderer.

```java
// Abstraction
public abstract class Shape {
    protected Renderer renderer; // The bridge

    public Shape(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract void draw();
    public abstract void resize(float factor);
}
```

#### 4. Refined Abstractions

These are the concrete shape classes. They use the `renderer` to perform their drawing operations.

```java
// Refined Abstraction: Circle
public class Circle extends Shape {
    private float radius;

    public Circle(Renderer renderer, float radius) {
        super(renderer);
        this.radius = radius;
    }

    @Override
    public void draw() {
        renderer.renderCircle(radius); // Delegation through the bridge
    }

    @Override
    public void resize(float factor) {
        this.radius *= factor;
    }
}

// Refined Abstraction: Square
public class Square extends Shape {
    private float side;

    public Square(Renderer renderer, float side) {
        super(renderer);
        this.side = side;
    }

    @Override
    public void draw() {
        renderer.renderSquare(side); // Delegation
    }

    @Override
    public void resize(float factor) {
        this.side *= factor;
    }
}
```

#### 5. Client Code

The client can now create any combination of `Shape` and `Renderer` independently.

```java
public class DrawingClient {
    public static void main(String[] args) {
        // Create renderers for different OS
        Renderer windowsRenderer = new WindowsRenderer();
        Renderer macRenderer = new MacRenderer();

        // Create a circle to be drawn on Windows
        Shape circleOnWindows = new Circle(windowsRenderer, 5);
        System.out.println("--- Drawing Circle on Windows ---");
        circleOnWindows.draw();

        // Create a square to be drawn on macOS
        Shape squareOnMac = new Square(macRenderer, 10);
        System.out.println("\n--- Drawing Square on macOS ---");
        squareOnMac.draw();
        
        // You can easily switch the implementation at runtime
        Shape circleOnMac = new Circle(macRenderer, 7);
        System.out.println("\n--- Drawing Circle on macOS ---");
        circleOnMac.draw();
    }
}
```

---

## ✔ When to Use the Bridge Pattern

- **Decoupling Abstraction and Implementation:** When you want to avoid a permanent binding between an abstraction and its implementation. This is useful if the implementation needs to be selected or switched at runtime.
- **Avoiding Class Explosion:** When you have two independent dimensions of variation (e.g., shape and renderer, or UI widget and theme) and you want to avoid creating a subclass for every possible combination.
- **Independent Evolution:** When both the abstractions and their implementations should be extensible by subclassing independently.

## 🆚 Bridge vs. Adapter

- **Intent:** This is the most critical difference. The **Bridge** pattern is a **design decision made upfront** to decouple parts of a system so they can evolve independently. The **Adapter** pattern is used **after the fact** to make two existing, incompatible interfaces work together.
- **Structure:** Bridge is about composing an abstraction and an implementor. Adapter is about wrapping an existing class (the adaptee) to provide a different interface.

## 💡 Interview Line

> **“The Bridge pattern is a strategic choice to prevent class explosion by separating abstraction from implementation. Think of it as building a bridge between two hierarchies so you can pick one from each side, whereas an Adapter is more of a tactical fix to make two existing things compatible.”**

---

## 🚀 Next Steps

- Review the **Adapter Pattern** to solidify your understanding of the differences.
- Explore the **Strategy Pattern**, which is another behavioral pattern that relies on composition but focuses on encapsulating algorithms rather than implementations.
