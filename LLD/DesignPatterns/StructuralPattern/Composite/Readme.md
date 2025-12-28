# 🧩 Composite Design Pattern – Deep Dive

> **Mental model:** The Composite pattern allows you to compose objects into tree-like structures to represent part-whole hierarchies. It lets clients treat individual objects and compositions of objects uniformly.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a file system representation. A file system consists of two types of objects: **files** and **directories** (or folders).

- A `File` has properties like name and size.
- A `Directory` can contain other `Directory` objects and `File` objects. It also has a name, and its total size is the sum of the sizes of all its contents.

You want to write client code that can work with both files and directories without having to distinguish between them. For example, you might want a function that calculates the total size of a selected item, whether that item is a single file or a directory containing many files and subdirectories.

Writing separate logic for a file and a directory everywhere in the client code would be cumbersome and repetitive:

```java
// Client code without Composite pattern
Object item = getFileSystemItem();

if (item instanceof File) {
    System.out.println("Size: " + ((File) item).getSize());
} else if (item instanceof Directory) {
    System.out.println("Total size: " + ((Directory) item).calculateSize()); // Needs a special method
}
```

This code is not scalable. If you add a new type of object (e.g., a `Shortcut` or `SymbolicLink`), you would have to change the client code everywhere.

---

## ✅ Composite Solution

The Composite pattern provides a solution by creating a common interface that represents both the individual objects (`File`) and the composite objects (`Directory`). This allows the client to treat them the same way.

### 🧱 Structure

```
+----------------------+
|      Component       |
| (e.g., FileSystemItem)|
|----------------------|
| + operation()        |
+----------------------+
        ^
        |
+----------------+----------------+
|                |                |
+--------------+-----------+  +------------------------+
|           Leaf           |  |        Composite         |
|       (e.g., File)       |  |     (e.g., Directory)    |
|--------------------------|  |------------------------|
| + operation()            |  | - children: Component[]  |
|                          |  |------------------------|
|                          |  | + operation()          |
|                          |  | + add(Component)       |
|                          |  | + remove(Component)    |
+--------------------------+  +------------------------+
```

- **Component:** An interface or abstract class that declares the common operations for both simple (`Leaf`) and complex (`Composite`) objects. (e.g., `calculateSize()`).
- **Leaf:** A class that implements the `Component` and represents an individual object that has no children. It defines the behavior for the primitive objects in the composition.
- **Composite:** A class that implements the `Component` and represents a composite object that can have children. It stores child components and implements the `Component` operations by delegating them to its children.

### ☕ Java Example

Let's apply this to our file system example.

#### 1. The Component Interface

This interface provides a common method for both files and directories.

```java
public interface FileSystemItem {
    String getName();
    int getSize();
}
```

#### 2. The Leaf Class

The `File` class is a leaf because it cannot contain other items.

```java
public class File implements FileSystemItem {
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        // The size of a file is its own size
        System.out.println("Calculating size of file: " + name);
        return this.size;
    }
}
```

#### 3. The Composite Class

The `Directory` class is the composite. It can hold a collection of `FileSystemItem` objects.

```java
import java.util.ArrayList;
import java.util.List;

public class Directory implements FileSystemItem {
    private String name;
    private List<FileSystemItem> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public void addItem(FileSystemItem item) {
        children.add(item);
    }

    public void removeItem(FileSystemItem item) {
        children.remove(item);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        System.out.println("Calculating size of directory: " + name);
        // The size of a directory is the sum of the sizes of its children
        int totalSize = 0;
        for (FileSystemItem item : children) {
            totalSize += item.getSize();
        }
        return totalSize;
    }
}
```

#### 4. The Client Code

The client can now treat files and directories uniformly.

```java
public class FileSystemClient {
    public static void main(String[] args) {
        // Create a file system tree structure
        Directory root = new Directory("root");
        Directory music = new Directory("music");
        Directory pictures = new Directory("pictures");

        File song1 = new File("song1.mp3", 10);
        File song2 = new File("song2.mp3", 15);

        File pic1 = new File("pic1.jpg", 20);
        Directory vacationPics = new Directory("vacation");
        File vacationPic1 = new File("vacation1.jpg", 25);

        // Build the tree
        root.addItem(music);
        root.addItem(pictures);
        music.addItem(song1);
        music.addItem(song2);
        pictures.addItem(pic1);
        pictures.addItem(vacationPics);
        vacationPics.addItem(vacationPic1);

        // The client can now calculate the size of any item, be it a file or a directory,
        // without knowing its specific type.
        System.out.println("--- Calculating size of a file ---");
        System.out.println("Size of song1.mp3: " + song1.getSize());

        System.out.println("\n--- Calculating size of a directory ---");
        System.out.println("Size of music directory: " + music.getSize());
        
        System.out.println("\n--- Calculating size of the entire root directory ---");
        System.out.println("Total size of root: " + root.getSize());
    }
}
```

---

## ✔ When to Use the Composite Pattern

- **Part-Whole Hierarchies:** When your model can be represented as a tree structure of objects.
- **Uniformity:** When you want clients to be able to treat individual objects and compositions of objects in the same way.
- **Recursive Structures:** It is a natural fit for recursive data structures like file systems, UI component trees (e.g., in Swing or JavaFX), or nested categories.

## 💡 Interview Line

> **“The Composite pattern is about creating tree structures where the nodes and leaves are treated the same way. It allows for recursive composition and uniform client handling.”**

---

## 🚀 Next Steps

- Explore the **Bridge Pattern**, which focuses on decoupling an abstraction from its implementation.
- Understand the **Flyweight Pattern**, which is another structural pattern used for optimizing memory usage by sharing objects.
