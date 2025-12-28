# ☕ Java LLD Starter Template for Logger System

> **Goal:** Never start from a blank screen in an LLD interview again.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.logger
 ├── domain        // The LogMessage entity.
 ├── service       // The main Logger and its components.
 ├── strategy      // Pluggable behaviors (e.g., LogTarget, Formatter).
 ├── factory       // Object creation (e.g., LogMessageFactory).
 └── api           // The public interface for clients to submit log messages.
```

---

## 2️⃣ The `Entity` Template (The `LogMessage` Object)

```java
// Represents a single log message.
public class LogMessage {
    private final LogLevel level;
    private final String message;
    private final LocalDateTime timestamp;
    private final String source;

    // constructor, getters
}

public enum LogLevel {
    INFO, WARN, ERROR, DEBUG
}
```

---

## 3️⃣ The `Service` Template (The `Logger` Itself)

```java
// The core Logger class that clients will interact with.
public class Logger {
    private final String name;
    private final LogLevel levelThreshold;
    private final List<LogTarget> targets;

    public Logger(String name, LogLevel threshold, List<LogTarget> targets) {
        this.name = name;
        this.levelThreshold = threshold;
        this.targets = targets;
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    private void log(LogLevel level, String message) {
        if (level.ordinal() >= levelThreshold.ordinal()) {
            LogMessage logMessage = new LogMessage(level, message, ...);
            for (LogTarget target : targets) {
                target.write(logMessage);
            }
        }
    }
}
```

---

## 4️⃣ The `Strategy` Template (For Pluggable Log Destinations)

This is the most important part of a logger design, allowing for flexible output.

```java
// Interface for a place where logs can be sent.
public interface LogTarget {
    void write(LogMessage message);
}

// Example: A strategy to write logs to the console.
public class ConsoleLogTarget implements LogTarget {
    private final Formatter formatter;

    public ConsoleLogTarget(Formatter formatter) { this.formatter = formatter; }

    @Override
    public void write(LogMessage message) {
        System.out.println(formatter.format(message));
    }
}

// Example: A strategy to write logs to a file.
public class FileLogTarget implements LogTarget {
    // ... similar structure, but writes to a file.
}

// Interface for formatting the log message.
public interface Formatter {
    String format(LogMessage message);
}

// Example: A simple text formatter.
public class SimpleTextFormatter implements Formatter {
    @Override
    public String format(LogMessage message) {
        return String.format("[%s] %s: %s", message.getTimestamp(), message.getLevel(), message.getMessage());
    }
}
```

---

## 5️⃣ The `Factory` Template (For Creating Logger Instances)

```java
// A factory to configure and create Logger instances.
public class LoggerFactory {
    public static Logger createLogger(String name) {
        // In a real system, this would read configuration (e.g., from a file)
        // to set the log level and targets.

        List<LogTarget> targets = new ArrayList<>();
        targets.add(new ConsoleLogTarget(new SimpleTextFormatter()));

        return new Logger(name, LogLevel.INFO, targets);
    }
}
```
