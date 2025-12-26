# 🧩 Proxy Design Pattern – Deep Dive

> **Mental model:** The Proxy pattern provides a surrogate or placeholder for another object to control access to it. It is used to manage the object, adding a layer of control for security, performance, or simplicity.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are developing a document viewer application. The application needs to load and display large, high-resolution images. Loading these images into memory can be a very resource-intensive and time-consuming operation.

If the application loads all images as soon as a document is opened, it will lead to:
- **High Memory Consumption:** All images, even those not currently visible, will occupy memory.
- **Slow Initial Loading:** The user has to wait a long time for the application to start, as it's busy loading every image.
- **Wasted Resources:** The user may only view a few pages of the document, meaning most of the loading effort was unnecessary.

Directly accessing the `Image` object is inefficient. You need a way to defer the expensive loading process until it is absolutely necessary.

---

## ✅ Proxy Solution

The Proxy pattern introduces a "proxy" object that has the same interface as the real object (the `Image`). The client interacts with the proxy as if it were the real object. The proxy, in turn, manages the lifecycle of the real object.

In our scenario, we can use a **Virtual Proxy**. The proxy initially holds only the metadata of the image (like its file path), but not the actual image data. The real `Image` object is only created and loaded when the client makes a specific request to display it (e.g., by calling a `display()` method).

### 🧱 Structure

```
+--------------+      +------------------+      +-----------------+
|    Client    |----->|     Subject      |      |   RealSubject   |
| (App Code)   |      | (e.g., IImage)   |      | (e.g., RealImage)|
+--------------+      +------------------+      +-----------------+
                           ^
                           |
                           |
                    +------------------+
                    |      Proxy       |
                    | (e.g., ImageProxy)|
                    +------------------+
```

- **Subject:** An interface that both the `RealSubject` and `Proxy` implement. This allows the client to treat the proxy just like the real object.
- **RealSubject:** The actual object that contains the core logic and is often resource-intensive.
- **Proxy:** The surrogate object that controls access to the `RealSubject`. It may be responsible for creating, managing, and deleting the `RealSubject`.

### ☕ Java Example (Virtual Proxy)

Let's see how a virtual proxy works for lazy loading images.

#### 1. Subject Interface

```java
public interface Image {
    void display();
}
```

#### 2. RealSubject

This class handles the loading of a high-resolution image from a disk, which is a heavy operation.

```java
public class HighResolutionImage implements Image {
    private String imagePath;

    public HighResolutionImage(String imagePath) {
        this.imagePath = imagePath;
        loadImage(imagePath); // Load image on instantiation
    }

    private void loadImage(String imagePath) {
        System.out.println("Loading image from: " + imagePath);
        // Simulate a time-consuming operation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void display() {
        System.out.println("Displaying image: " + imagePath);
    }
}
```

#### 3. Proxy

The `ImageProxy` holds a reference to the real image but only instantiates it when `display()` is called for the first time.

```java
public class ImageProxy implements Image {
    private String imagePath;
    private HighResolutionImage realImage; // The real object is not created initially

    public ImageProxy(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public void display() {
        if (realImage == null) {
            // The real image is created only on demand (lazily)
            System.out.println("Proxy: Creating real image object now.");
            realImage = new HighResolutionImage(imagePath);
        }
        // Once created, the request is delegated to the real object
        realImage.display();
    }
}
```

#### 4. Client Code

The client code interacts with the proxy, unaware of the lazy loading mechanism.

```java
public class DocumentViewer {
    public static void main(String[] args) {
        System.out.println("Opening document with two images...");
        
        // The proxy objects are created instantly, no heavy loading yet
        Image image1 = new ImageProxy("/images/page1_hires.jpg");
        Image image2 = new ImageProxy("/images/page2_hires.jpg");

        System.out.println("Document open. Images are ready to be displayed on demand.");

        // The user scrolls to the first image. The real image is loaded and displayed now.
        System.out.println("User requests to view the first image:");
        image1.display();
        
        // The user views the first image again. No reloading is needed.
        System.out.println("
User requests to view the first image again:");
        image1.display();

        // The user scrolls to the second image.
        System.out.println("
User requests to view the second image:");
        image2.display();
    }
}
```

---

## 💡 Types of Proxies

The Proxy pattern is versatile and can be used for various purposes:

| Proxy Type        | Purpose                                                                          |
|-------------------|----------------------------------------------------------------------------------|
| **Virtual Proxy** | Delays the creation and initialization of expensive objects until needed.        |
| **Protection Proxy**| Controls access to an object based on the caller's permissions (authorization).  |
| **Remote Proxy**  | Provides a local representation for an object that exists in a different address space. |
| **Logging Proxy** | Adds logging to method invocations on the real object, for auditing purposes.       |
| **Caching Proxy** | Stores the results of expensive operations and serves them from a cache.         |

## 🆚 Proxy vs. Decorator

Though their structures look similar, their intents are different:

- **Intent:** A **Decorator** is meant to **add or enhance behavior**. A **Proxy** is meant to **control access** to an object.
- **Functionality:** A decorator adds functionality, while a proxy may or may not. For instance, a protection proxy might deny a request altogether.
- **Usage:** You can chain multiple decorators on top of one another. While you can chain proxies, it's less common and usually for different types of control (e.g., a logging proxy around a protection proxy).

## 💡 Interview Line

> **“A proxy acts as a gatekeeper to an object, controlling when and how it is accessed, whereas a decorator focuses on adding new features to it.”**

---

## 🚀 Next Steps

- Explore the **Facade Pattern**, which provides a simplified interface to a complex system.
- Understand the **Adapter Pattern**, used for making incompatible interfaces work together.
