# ☕ Java LLD Concurrency Template for File System

> **Goal:** Design a concurrent, hierarchical file system that safely handles simultaneous reads, writes, and directory modifications using fine-grained locking.

---

## 1️⃣ Core Concurrency Problem

A file system is an inherently concurrent environment. The key challenges are:

1.  **Read/Write Conflicts:** One thread might try to read a file while another thread is in the middle of writing new content to it, leading to a corrupted or partial read.
2.  **Directory Modification Conflicts:** One thread could be adding a file to a directory while another thread is trying to list its contents, leading to an inconsistent view.
3.  **Deadlocks:** Naive locking can lead to deadlocks. For example, Thread 1 tries to move `/dirA/file1` to `/dirB` (locks `dirA`, then wants to lock `dirB`), while Thread 2 tries to move `/dirB/file2` to `/dirA` (locks `dirB`, then wants to lock `dirA`).

---

## 2️⃣ The `Component` & `Composite` with `ReentrantReadWriteLock`

To solve this, each node in the tree (`File` and `Directory`) must manage its own access. A `ReentrantReadWriteLock` is the perfect tool for this job because file systems typically have many more reads than writes.

*   **Read Lock:** Allows multiple threads to read concurrently.
*   **Write Lock:** Ensures only a single thread can modify at a time.

```java
// The abstract base class for all file system entries.
public abstract class FileSystemEntry {
    protected final String name;
    protected Directory parent;
    // Each entry in the file system gets its own read-write lock.
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FileSystemEntry(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() { return name; }
    public abstract int getSize();
}

// The Leaf node: A File
public class File extends FileSystemEntry {
    private String content;

    public File(String name, Directory parent) { super(name, parent); }

    public String read() {
        lock.readLock().lock(); // Acquire a read lock
        try {
            return content;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void write(String newContent) {
        lock.writeLock().lock(); // Acquire a write lock
        try {
            this.content = newContent;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public int getSize() { /* return content size */}
}

// The Composite node: A Directory
public class Directory extends FileSystemEntry {
    // The contents of a directory must be managed by a thread-safe map.
    private final Map<String, FileSystemEntry> children = new ConcurrentHashMap<>();

    public Directory(String name, Directory parent) { super(name, parent); }

    public void addEntry(FileSystemEntry entry) {
        // To modify the directory's structure, we need a write lock on the directory itself.
        lock.writeLock().lock();
        try {
            children.put(entry.getName(), entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> listContents() {
        lock.readLock().lock(); // To read the list of contents, a read lock is sufficient.
        try {
            return new ArrayList<>(children.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getSize() {
        lock.readLock().lock();
        try {
            // Sum the size of all children. This requires acquiring locks on children
            // in a real implementation, but is simplified here.
            return children.values().stream().mapToInt(FileSystemEntry::getSize).sum();
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

---

## 3️⃣ The `Service` Template (Traversing and Path Locking)

Operations that span multiple nodes, like creating a file at a path, require careful lock acquisition.

```java
public class FileSystemService {
    private final Directory root = new Directory("/", null);

    // Creates a file at a path like "/dir1/newfile.txt"
    public void createFile(String path) {
        // 1. Split the path: ["dir1", "newfile.txt"]
        String[] parts = path.trim().split("/");
        if (parts.length == 0) return;

        Directory current = root;
        // 2. Lock and traverse down the path.
        // Acquire read locks on all parent directories in the path.
        for (int i = 0; i < parts.length - 1; i++) {
            current.lock.readLock().lock(); // Lock before traversing
            // In a real system, you would get the child and then unlock the parent.
            // This example simplifies to avoid complex lock-handover logic.
        }

        // 3. Acquire a write lock on the final parent directory to add the new file.
        Directory parent = findDirectory(path); // Simplified find logic
        parent.lock.writeLock().lock();
        try {
            String fileName = parts[parts.length - 1];
            if (!parent.children.containsKey(fileName)) {
                 parent.addEntry(new File(fileName, parent));
            }
        } finally {
            parent.lock.writeLock().unlock();
            // Unlock all the parent read-locks in reverse order.
        }
    }
}
```

---

## 4️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The entire file system tree, starting from the root directory, is the shared state. Multiple threads can try to access and modify any part of this tree simultaneously."
*   **Choose the Right Lock (ReadWriteLock):** "I chose a `ReentrantReadWriteLock` for every file and directory. This is a significant performance optimization because it allows unlimited concurrent readers. A write operation requires an exclusive lock, but file systems are typically read-heavy, so this fits the access pattern perfectly."
*   **Explain Fine-Grained Locking:** "The locking is fine-grained, meaning each node has its own lock. This is crucial for performance. A thread writing to `/home/user/doc.txt` will acquire a write lock on `doc.txt` and a read lock on `/`, `/home`, and `/user`. This will *not* block another thread from reading `/etc/config.conf` because they don't share any locked nodes in their paths."
*   **Address the Path Traversal Problem:** "When performing an operation at a path like `/a/b/c`, you can't just lock `c`. You must ensure that the path `/a/b` doesn't change while you are traversing it. A common strategy is called **lock coupling** or **hand-over-hand locking**. A thread locks the parent, gets the child, locks the child, and then releases the lock on the parent. This ensures the path is stable during traversal."
*   **Discuss Deadlock Prevention:** "Operations like `move /a/b /c/d` are a classic source of deadlocks. If one thread moves `/a` to `/b` and another moves `/b` to `/a`, they can deadlock. The standard solution is to enforce a **lock ordering**. For example, always lock the entry whose absolute path comes first lexicographically. This prevents the circular wait condition necessary for a deadlock."