# ☕ Java LLD Starter Template for File Storage System

> **Goal:** Never start from a blank screen in an LLD interview again.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  Clarify requirements
2.  Identify entities
3.  Identify what changes
4.  Plug into these templates
5.  Explain trade-offs

📌 These templates are **intentionally minimal** — extensibility > completeness.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.filestorage
 ├── domain        // entities & value objects (e.g., File, Directory, User)
 ├── service       // business logic (e.g., FileService, DirectoryService)
 ├── strategy      // pluggable behaviors (e.g., StorageStrategy)
 ├── repository    // storage abstractions (e.g., FileRepository, DirectoryRepository)
 ├── factory       // object creation (e.g., FileFactory)
 └── api           // public interfaces / controllers
```

📌 Interview tip: *Say this structure out loud* — it shows maturity.

---

## 2️⃣ The `Entity` Template (The "Noun" Objects)

```java
// Common interface for all domain models
public abstract class BaseEntity {
    private String id;
    private String name;
    private long size;
    // getters, setters, equals, hashCode
}

// Example: A file in the storage system
public class File extends BaseEntity {
    private String content;
    // other attributes
}

// Example: A directory in the storage system
public class Directory extends BaseEntity {
    private List<BaseEntity> children;
    // other attributes
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for managing files
public class FileService {
    private final FileRepository fileRepository;
    private final StorageStrategy storageStrategy;

    public FileService(FileRepository repo, StorageStrategy strategy) {
        this.fileRepository = repo;
        this.storageStrategy = strategy;
    }

    public File createFile(String name, String content, Directory parent) {
        // ... create file, save to repository, etc.
    }

    public String getFileContent(File file) {
        // ... retrieve file content
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable storage logic
public interface StorageStrategy {
    void store(File file);
    File retrieve(String fileId);
}

// Example: Different strategies for storing files
public class LocalStorageStrategy implements StorageStrategy {
    @Override
    public void store(File file) {
        // ... logic for storing file locally
    }

    @Override
    public File retrieve(String fileId) {
        // ... logic for retrieving file from local storage
    }
}

public class S3StorageStrategy implements StorageStrategy {
    @Override
    public void store(File file) {
        // ... logic for storing file in S3
    }

    @Override
    public File retrieve(String fileId) {
        // ... logic for retrieving file from S3
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for data storage
public interface FileRepository {
    Optional<File> findById(String fileId);
    List<File> findByDirectory(Directory directory);
    void save(File file);
}

// In-memory implementation for interviews
public class InMemoryFileRepository implements FileRepository {
    private final Map<String, File> files = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of files
public class FileFactory {
    public static File createFile(String name, String content) {
        return new File(name, content);
    }
}
```
