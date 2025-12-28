# ☕ Java LLD Starter Template for File System (Composite Pattern)

> **Goal:** Design a hierarchical file system structure using the Composite design pattern to treat files and directories uniformly.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  **Identify the Pattern:** "This problem, which involves a tree-like structure where we want to treat individual objects (files) and composite objects (directories) in the same way, is a perfect fit for the **Composite Pattern**."
2.  Define the `Component` Interface (`FileSystemEntry`).
3.  Implement the `Leaf` Class (`File`).
4.  Implement the `Composite` Class (`Directory`).
5.  Show how a client can use them uniformly.

---

## 1️⃣ The Composite Pattern Explained

The Composite pattern's intent is to compose objects into tree structures to represent part-whole hierarchies. It lets clients treat individual objects (`File`) and compositions of objects (`Directory`) uniformly.

### 登場人物 (The "Actors")

*   **Component (`FileSystemEntry`):** An interface that declares common operations for both simple (leaf) and complex (composite) objects in the hierarchy. (e.g., `getName()`, `getSize()`).
*   **Leaf (`File`):** A basic object that has no children. It implements the Component interface.
*   **Composite (`Directory`):** An object that has children (other Components). It implements the Component interface, but its methods often delegate the work to its children.
*   **Client:** The code that uses the Component interface to interact with objects in the composition.

---

## 2️⃣ The `Component` Interface (`FileSystemEntry`)

This is the common contract for both files and directories.

```java
// The Component interface
public interface FileSystemEntry {
    String getName();
    int getSize(); // A common operation we can call on both files and directories
}
```

---

## 3️⃣ The `Leaf` Class (`File`)

A `File` is a leaf node in the tree. It has a name and a size, but no children.

```java
// The Leaf class
public class File implements FileSystemEntry {
    private final String name;
    private final int size;

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
        // A file returns its own size.
        return this.size;
    }
}
```

---

## 4️⃣ The `Composite` Class (`Directory`)

A `Directory` is a composite node. It holds a collection of other `FileSystemEntry` objects (which can be either Files or other Directories).

```java
// The Composite class
public class Directory implements FileSystemEntry {
    private final String name;
    private final List<FileSystemEntry> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public void addEntry(FileSystemEntry entry) {
        children.add(entry);
    }

    public void removeEntry(FileSystemEntry entry) {
        children.remove(entry);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSize() {
        // A directory calculates its size by summing the sizes of all its children.
        // This is where the power of the pattern shines.
        int totalSize = 0;
        for (FileSystemEntry entry : children) {
            totalSize += entry.getSize();
        }
        return totalSize;
    }
}
```

---

## 5️⃣ Client Usage

The client can now use the `FileSystemEntry` interface without needing to know if it's a file or a directory, simplifying the code.

```java
public class FileSystemClient {
    public static void main(String[] args) {
        // Create a root directory
        Directory root = new Directory("root");

        // Create files
        File file1 = new File("file1.txt", 100);
        File file2 = new File("file2.txt", 150);

        // Create a subdirectory
        Directory subDir = new Directory("subDir");
        File file3 = new File("file3.txt", 200);
        subDir.addEntry(file3);

        // Add all entries to the root
        root.addEntry(file1);
        root.addEntry(file2);
        root.addEntry(subDir);

        // Calculate total size of the root directory without knowing the internal structure
        // The client treats files and directories uniformly.
        System.out.println("Total size of root: " + root.getSize()); // Output: Total size of root: 450
    }
}
```