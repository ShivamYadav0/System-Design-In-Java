# ☕ Java LLD Starter Template for File Storage System

> **Goal:** Design a concurrent, hierarchical file system that safely handles simultaneous reads, writes, and directory modifications.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements (e.g., file content, directory structure, permissions).
2.  Identify entities (`Entry`, `File`, `Directory`).
3.  **Identify Shared, Mutable State** (The directory tree structure and file contents).
4.  Design with concurrency-safe patterns (this template).
5.  Explain trade-offs (e.g., `ReentrantReadWriteLock` for performance).

📌 These templates are designed to handle concurrency from the start.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.filestorage
 ├── domain        // The node hierarchy (Entry, File, Directory).
 ├── service       // The FileSystemService that exposes file operations.
 └── api           // Interface for users to interact with the file system.
```

---

## 2️⃣ The `Entity` Template (Hierarchical, Synchronized Nodes)

Each node in the file system tree (both files and directories) needs to manage its own state and its relationship with its children.

```java
// A common interface for both files and directories.
public abstract class Entry {
    protected final String name;
    protected final Directory parent;
    // A read-write lock allows many readers or one writer, boosting performance.
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Entry(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }
    // Getters...
}

public class File extends Entry {
    private String content;

    public File(String name, Directory parent) { super(name, parent); }

    public String read() {
        lock.readLock().lock();
        try {
            return content;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void write(String newContent) {
        lock.writeLock().lock();
        try {
            this.content = newContent;
        } finally {
            lock.writeLock().unlock();
        }
    }
}

public class Directory extends Entry {
    // The contents of a directory must be managed by a thread-safe collection.
    private final Map<String, Entry> children = new ConcurrentHashMap<>();

    public Directory(String name, Directory parent) { super(name, parent); }

    public Optional<Entry> findChild(String name) {
        // Read operations are generally safe and fast with ConcurrentHashMap.
        return Optional.ofNullable(children.get(name));
    }

    public Entry addChild(Entry child) {
        // A write lock on the directory is needed to safely modify its contents.
        lock.writeLock().lock();
        try {
            return children.putIfAbsent(child.getName(), child);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Entry> listContents() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(children.values());
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

---

## 3️⃣ The `Service` Template (Traversing and Locking the Tree)

The service layer is responsible for traversing the path to a file/directory and ensuring locks are acquired and released correctly.

```java
// FileSystemService provides a clean API for file operations.
public class FileSystemService {
    private final Directory root;

    public FileSystemService() {
        // The root directory has no parent.
        this.root = new Directory("/", null);
    }

    public Optional<File> createFile(String path) {
        // Simplified: assumes path is like "/dir/file.txt"
        String[] parts = path.split("/");
        String fileName = parts[parts.length - 1];
        Directory parentDir = findParentDirectory(path);

        if (parentDir != null) {
            File newFile = new File(fileName, parentDir);
            parentDir.addChild(newFile);
            return Optional.of(newFile);
        }
        return Optional.empty();
    }

    public Optional<String> readFile(String path) {
        return findFile(path).map(File::read);
    }

    public void writeFile(String path, String content) {
        findFile(path).ifPresent(file -> file.write(content));
    }
    
    // Helper methods to traverse the tree (in a real system, this would be more robust)
    private Optional<File> findFile(String path) { ... }
    private Directory findParentDirectory(String path) { ... }
}
```

---

## 4️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The entire file system tree is the shared state. Multiple threads could be trying to write to the same file, read a file while it's being written, or add a file to a directory while another thread is trying to list its contents. This requires a robust locking strategy."
*   **Choose the Right Lock:** "I've chosen a `ReentrantReadWriteLock` for each `Entry` (file or directory). This is a crucial performance optimization. It allows any number of threads to read a file or directory concurrently, as long as no thread is writing to it. A write operation requires an exclusive lock, blocking all other readers and writers. This matches the typical usage pattern of file systems: many more reads than writes."
*   **Explain Fine-Grained Locking:** "The locking is fine-grained. Each file and directory has its own lock. This means a thread writing to `/dir1/fileA.txt` will *not* block another thread that is reading `/dir2/fileB.txt`. Contention only occurs when multiple threads try to access the *same* node or modify the *same* directory at the same time."
*   **Discuss Directory Concurrency:** "For the `Directory` itself, I used a `ConcurrentHashMap` to store its children. This provides thread-safe, high-performance lookups (`findChild`). However, composite operations like `addChild` or `listContents` still require an explicit lock. Why? Because `addChild` is a check-then-act operation (check if name exists, then add). To guarantee atomicity for this, we need to acquire the directory's write lock."
*   **Address Deadlock Risks:** "A more complex implementation involving moving files or directories (`mv /a/b /c`) introduces a significant risk of deadlock. For example, Thread 1 tries to move `/a` to `/b` (locks `/a`, then `/b`), while Thread 2 tries to move `/b` to `/a` (locks `/b`, then `/a`). To solve this, we must enforce a consistent lock acquisition order. For example, always lock the parent directory before the child, or always lock the directory with the lexicographically smaller name first. This prevents the circular wait condition required for a deadlock to occur."