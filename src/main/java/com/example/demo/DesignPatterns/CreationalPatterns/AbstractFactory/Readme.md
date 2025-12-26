# 🧩 Abstract Factory Design Pattern – Deep Dive

> **Mental model:** The Abstract Factory pattern provides an interface for creating families of related or dependent objects without specifying their concrete classes.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a UI toolkit that supports multiple operating systems (e.g., Windows and macOS). Your toolkit needs to create UI elements like buttons and checkboxes that look and feel native to the OS they are running on.

- A `Button` on Windows looks different from a `Button` on macOS.
- A `Checkbox` on Windows looks different from a `Checkbox` on macOS.

The key constraint is that you cannot mix and match. An application must use a consistent set of UI elements from the **same family** (all Windows-style or all macOS-style). You need to ensure that a Windows-themed application never accidentally creates a macOS-style button.

Without a dedicated pattern, your client code might look like this:

```java
public class Application {
    public void renderUI(String osType) {
        Button button;
        Checkbox checkbox;

        if (osType.equals("Windows")) {
            button = new WindowsButton();
            checkbox = new WindowsCheckbox();
        } else if (osType.equals("macOS")) {
            button = new MacButton();
            checkbox = new MacCheckbox();
        } else {
            throw new IllegalArgumentException("Unsupported OS");
        }

        button.paint();
        checkbox.paint();
    }
}
```

This code has a major problem: the client is responsible for creating the correct type of UI element. If you add a new element (e.g., a `TextBox`), you have to go back and modify this logic everywhere, making it hard to maintain and error-prone.

---

## ✅ Abstract Factory Solution

The Abstract Factory pattern solves this by introducing a new layer of abstraction: a factory for creating other factories. It groups together a set of factory methods for creating related objects (a "family").

### 🧱 Structure

```
+-----------------------+      +-------------------+  +--------------------+
| AbstractFactory       |----->|  AbstractProductA |  |  AbstractProductB  |
| (e.g., GUIFactory)    |      |  (e.g., Button)   |  | (e.g., Checkbox)   |
|-----------------------|      +-------------------+  +--------------------+
| + createButton()      |             ^
| + createCheckbox()    |             |
+-----------------------+      +------+------+
        ^
        |
+-----------------+-----------------+
|                 |                 |
+-----------------------+      +-----------------------+
|   ConcreteFactory1    |      |   ConcreteFactory2    |
| (e.g., WindowsFactory)|      |   (e.g., MacFactory)  |
+-----------------------+      +-----------------------+
          |                             |
          | creates                     | creates
          V                             V
+-----------------------+      +-----------------------+
| ConcreteProductA1     |      | ConcreteProductA2     |
| (e.g., WindowsButton) |      |   (e.g., MacButton)   |
+-----------------------+      +-----------------------+
          |                             |
          | creates                     | creates
          V                             V
+-----------------------+      +-----------------------+
| ConcreteProductB1     |      | ConcreteProductB2     |
| (e.g., WindowsCheckbox)|     |  (e.g., MacCheckbox)  |
+-----------------------+      +-----------------------+
```

- **AbstractFactory:** An interface that declares a set of methods for creating abstract products (e.g., `createButton()`, `createCheckbox()`).
- **ConcreteFactory:** A class that implements the `AbstractFactory` interface to create a family of concrete products (e.g., `WindowsFactory` creates `WindowsButton` and `WindowsCheckbox`).
- **AbstractProduct:** An interface for a type of product (e.g., `Button`).
- **ConcreteProduct:** Concrete classes that implement the `AbstractProduct` interface and are created by a corresponding `ConcreteFactory`.

### ☕ Java Example

Let's build the UI toolkit using this pattern.

#### 1. Abstract Product Interfaces

```java
// Abstract Product A
public interface Button {
    void paint();
}

// Abstract Product B
public interface Checkbox {
    void paint();
}
```

#### 2. Concrete Product Implementations

Create a family of products for each OS.

```java
// Concrete Products for Windows
public class WindowsButton implements Button {
    public void paint() { System.out.println("Painting a Windows-style button."); }
}
public class WindowsCheckbox implements Checkbox {
    public void paint() { System.out.println("Painting a Windows-style checkbox."); }
}

// Concrete Products for macOS
public class MacButton implements Button {
    public void paint() { System.out.println("Painting a macOS-style button."); }
}
public class MacCheckbox implements Checkbox {
    public void paint() { System.out.println("Painting a macOS-style checkbox."); }
}
```

#### 3. The Abstract Factory Interface

This factory interface defines methods for creating a full set of related UI elements.

```java
// Abstract Factory
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
```

#### 4. Concrete Factory Implementations

Each concrete factory is responsible for creating a consistent family of products.

```java
// Concrete Factory for Windows
public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

// Concrete Factory for macOS
public class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}
```

#### 5. Client Code

The client code chooses which factory to use at the beginning. After that, it works only with the abstract interfaces, ensuring UI consistency.

```java
public class Application {
    private final Button button;
    private final Checkbox checkbox;

    public Application(GUIFactory factory) {
        // The application receives a factory and uses it to create UI elements.
        // It doesn't know (or care) which concrete factory it is.
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        button.paint();
        checkbox.paint();
    }

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();
        GUIFactory factory;

        // At the start, we decide which family of products to use.
        if (os.contains("win")) {
            factory = new WindowsFactory();
        } else {
            factory = new MacFactory(); // Default to Mac for this example
        }

        // The rest of the application is completely decoupled from the concrete product classes.
        Application app = new Application(factory);
        app.renderUI();
    }
}
```

---

## ✔ When to Use the Abstract Factory Pattern

- **Families of Related Products:** When your system needs to be independent of how its products are created, composed, and represented, and you want to work with families of related products.
- **Enforcing Constraints:** When you want to ensure that the products used together belong to the same family (e.g., all UI elements have the same look and feel).
- **Configuration-dependent Creation:** When the choice of which product family to use is determined at runtime (e.g., based on a configuration setting or environment property).

## 🆚 Abstract Factory vs. Factory Method

- **Scope:** Factory Method is a **single method** used to create one type of product. Abstract Factory is an **object with multiple factory methods** used to create a family of related products.
- **Usage:** You often use Factory Method to implement the methods of an Abstract Factory.
- **Complexity:** Abstract Factory is a higher level of abstraction and is more complex. You can start with Factory Method and evolve to Abstract Factory as your system grows.

## 💡 Interview Line

> **“Abstract Factory is about creating families of related objects. Think of it as a factory for creating other factories, where each sub-factory produces a consistent set of items, like all Windows UI elements or all macOS UI elements.”**

---

## 🚀 Next Steps

- Explore the **Builder Pattern**, which excels at creating a single, complex object in a step-by-step manner.
- Understand the **Prototype Pattern**, which is used to create new objects by copying an existing object.
