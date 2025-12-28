# 🧩 Template Method Design Pattern – Deep Dive

> **Mental model:** The Template Method pattern defines the skeleton of an algorithm in a base class but lets subclasses override specific steps of the algorithm without changing its overall structure.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are developing a data processing tool that can import, process, and export data from different sources like CSV files and SQL databases. The overall process is the same for both sources:

1.  **Connect** to the data source.
2.  **Extract** the data.
3.  **Process** the data (e.g., clean it, transform it).
4.  **Disconnect** from the data source.

The overall algorithm (the "template") is fixed, but the *implementation* of certain steps, like `connect` and `extract`, is completely different for a CSV file versus a SQL database.

A naive approach would be to create two completely separate classes that might have duplicated logic for the common steps, like `process`.

```java
// NOT a good approach - lots of duplicated code

class CsvDataProcessor {
    public void processCsvFile(String filePath) {
        // 1. Connect (specific to files)
        System.out.println("Opening CSV file: " + filePath);

        // 2. Extract (specific to CSV)
        System.out.println("Parsing CSV data...");

        // 3. Process (common logic)
        System.out.println("Processing data: cleaning and transforming..."); // <-- Duplicated

        // 4. Disconnect (specific to files)
        System.out.println("Closing file.");
    }
}

class SqlDataProcessor {
    public void processSqlTable(String connectionString) {
        // 1. Connect (specific to SQL)
        System.out.println("Connecting to database...");

        // 2. Extract (specific to SQL)
        System.out.println("Executing SQL query...");

        // 3. Process (common logic)
        System.out.println("Processing data: cleaning and transforming..."); // <-- Duplicated

        // 4. Disconnect (specific to SQL)
        System.out.println("Closing database connection.");
    }
}
```

This design is poor because:

1.  **Code Duplication:** The common `process` logic is repeated in both classes. If you need to change the processing logic, you have to update it in multiple places.
2.  **Lack of a Common Interface:** There is no shared structure, making it hard to treat these processors polymorphically.

---

## ✅ Template Method Solution

The Template Method pattern solves this by creating an abstract base class that defines the main algorithm. This algorithm is implemented in a `final` method (the `templateMethod`). The template method calls several other methods, some of which are `abstract` (requiring subclasses to implement them) and some of which can be concrete (providing a default implementation).

### 🧱 Structure

```
+-------------------------+
|   AbstractClass         |
| (e.g., DataProcessor)   |
|-------------------------|
| + templateMethod() {final}|  <-- The skeleton of the algorithm
|   - step1()             |
|   - abstractStep2()     |
|   - step3()             |
|   - hook() (optional)   |
+-------------------------+
          ^
          |
          |
+---------+---------------+ 
|                         |
+-------------------+     +-------------------+
|  ConcreteClassA   |     |  ConcreteClassB   |
| (CsvProcessor)    |     | (SqlProcessor)    |
|-------------------|     |-------------------|
| + abstractStep2() |     | + abstractStep2() |
| + hook() (override)|     +-------------------+
+-------------------+
```

-   **AbstractClass:** Contains the `templateMethod` which defines the algorithm's structure. It also declares abstract methods for the steps that subclasses must implement.
-   **ConcreteClass:** Implements the abstract methods to provide specific behaviors for those steps.

### ☕ Java Example

Let's apply this to our data processing example.

#### 1. The Abstract Base Class (The Template)

This class defines the overall algorithm.

```java
// The AbstractClass
public abstract class DataProcessor {

    // The Template Method - it's final to prevent subclasses from changing the algorithm structure.
    public final void process() {
        connect();
        extractData();
        processData(); // This is a concrete method shared by all subclasses
        disconnect();
    }

    // Abstract methods - must be implemented by subclasses
    protected abstract void connect();
    protected abstract void extractData();
    protected abstract void disconnect();

    // A concrete method, part of the template. All subclasses will use this version.
    private void processData() {
        System.out.println("Processing data: cleaning, transforming, and enriching...");
    }
}
```

#### 2. The Concrete Subclasses

Each subclass provides its own implementation for the varying steps.

```java
// ConcreteClass A
public class CsvDataProcessor extends DataProcessor {
    @Override
    protected void connect() {
        System.out.println("CSV Processor: Connecting to CSV file...");
    }

    @Override
    protected void extractData() {
        System.out.println("CSV Processor: Reading data from CSV rows.");
    }

    @Override
    protected void disconnect() {
        System.out.println("CSV Processor: Closing the file handle.");
    }
}

// ConcreteClass B
public class SqlDataProcessor extends DataProcessor {
    @Override
    protected void connect() {
        System.out.println("SQL Processor: Connecting to the database...");
    }

    @Override
    protected void extractData() {
        System.out.println("SQL Processor: Executing a SQL query and fetching results.");
    }

    @Override
    protected void disconnect() {
        System.out.println("SQL Processor: Closing the database connection.");
    }
}
```

#### 3. Client Code

The client code can now work with the abstract `DataProcessor` and doesn't need to know the specifics.

```java
public class Application {
    public static void main(String[] args) {
        System.out.println("--- Processing a CSV file ---");
        DataProcessor csvProcessor = new CsvDataProcessor();
        csvProcessor.process();

        System.out.println("\n--- Processing a SQL table ---");
        DataProcessor sqlProcessor = new SqlDataProcessor();
        sqlProcessor.process();
    }
}
```

---

## ✔ When to Use the Template Method Pattern

-   **Defining an Algorithm Skeleton:** When you want to define the structure of an algorithm once and have subclasses provide the specific implementation for certain steps.
-   **Avoiding Code Duplication:** To centralize common behavior among subclasses in a single base class.
-   **Controlling Subclass Extensions:** You can define a `final` template method to ensure that subclasses can only change specific parts of an algorithm, but not the overall sequence.

## 🆚 Template Method vs. Strategy

-   **Granularity:** Template Method uses inheritance to vary parts of an algorithm. Strategy uses composition to replace the *entire* algorithm.
-   **Relationship:** Template Method is a class-level pattern (you choose the implementation via subclassing). Strategy is an object-level pattern (you can switch strategies at runtime by providing a different strategy object).

## 💡 Interview Line

> **“The Template Method pattern is an inheritance-based pattern that allows you to define the skeleton of an algorithm in a base class, while deferring some of the steps to subclasses. This lets you have a fixed algorithm structure but allows for different implementations of the individual steps, promoting code reuse.”**

---

## 🚀 Next Steps

-   Look at frameworks like **Spring**. Many of its utility classes (like `JdbcTemplate`) use the Template Method pattern. You provide the custom parts (like your SQL query and result set mapping), and the template handles the resource management (connection, statement, transaction).
-   Compare with the **Factory Method** pattern, which is also a creational pattern based on defining a method in a base class that subclasses override.
