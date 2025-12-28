# ☕ Java LLD Concurrency Template for Logger System

> **Goal:** Design a high-performance, asynchronous logging system that does not block application threads while writing logs.

---

## 1️⃣ Core Concurrency Problem

The primary goal of a concurrent logger is to **decouple log submission from log writing**. Application threads should be able to fire off a log message and continue their work immediately, without waiting for the I/O operation (e.g., writing to a file or sending over the network) to complete. A naive logger that writes directly in the calling thread can become a major performance bottleneck, as application threads will be blocked on slow I/O.

This is a classic **Producer-Consumer** problem. Application threads are *producers* of log messages, and a dedicated background thread is the *consumer* that writes them to the target.

---

## 2️⃣ The `Service` Template (The Asynchronous Logger)

We introduce a `BlockingQueue` to hold log messages and a dedicated consumer thread to process them.

```java
// The core Logger class, now designed for asynchronous operation.
public class AsyncLogger implements Runnable {
    private final String name;
    private final LogLevel levelThreshold;
    private final List<LogTarget> targets;

    // A thread-safe queue to act as a buffer between producers and the consumer.
    private final BlockingQueue<LogMessage> messageQueue = new LinkedBlockingQueue<>();
    private final Thread workerThread;

    public AsyncLogger(String name, LogLevel threshold, List<LogTarget> targets) {
        this.name = name;
        this.levelThreshold = threshold;
        this.targets = targets;
        this.workerThread = new Thread(this, "Logger-Worker-" + name);
        this.workerThread.start();
    }

    // This is the fast, non-blocking method called by application threads (Producers).
    public void log(LogLevel level, String message) {
        if (level.ordinal() >= levelThreshold.ordinal()) {
            LogMessage logMessage = new LogMessage(level, message, ...);
            // offer() is non-blocking and will add the message to the queue instantly.
            messageQueue.offer(logMessage);
        }
    }

    // This is the main loop for the dedicated consumer thread.
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // take() blocks until a message is available in the queue.
                LogMessage message = messageQueue.take();
                // Write the message to all targets.
                for (LogTarget target : targets) {
                    target.write(message);
                }
            } catch (InterruptedException e) {
                // Allow the thread to terminate gracefully.
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

---

## 3️⃣ The `Strategy` Template (Thread-Safe Log Targets)

Log targets, especially those writing to a shared resource like a single file, must be thread-safe. However, in our asynchronous design, only a *single consumer thread* ever calls `target.write()`, which greatly simplifies concurrency management at this level.

```java
// Interface for a place where logs can be sent.
public interface LogTarget {
    void write(LogMessage message);
}

// A file-based log target.
public class FileLogTarget implements LogTarget {
    private final Formatter formatter;
    // A synchronized writer is crucial if multiple threads could ever access this.
    // In our design, it's a good safety measure.
    private final PrintWriter writer;

    public FileLogTarget(String filePath, Formatter formatter) throws IOException {
        this.formatter = formatter;
        // The 'true' flag enables auto-flushing.
        this.writer = new PrintWriter(new FileWriter(filePath, true), true);
    }

    @Override
    public synchronized void write(LogMessage message) {
        // The synchronized keyword here provides a lock on this method.
        // While our current design has only one writer thread, this makes the
        // component safe if the design were to change to have multiple writers.
        writer.println(formatter.format(message));
    }
}
```

---

## 4️⃣ How to TALK Concurrency in Interviews

*   **Identify the Performance Bottleneck:** "The biggest problem with a simple logger is that it forces application threads to perform I/O. Writing to a file, the console, or a network socket is slow. If the application logs frequently, threads will spend more time waiting for logs to write than doing their actual work. The logger becomes a bottleneck."
*   **Propose the Producer-Consumer Pattern:** "To solve this, I've designed an asynchronous logger based on the producer-consumer pattern. Application threads are the producers; they create a `LogMessage` and quickly put it onto a `BlockingQueue`. This `offer` operation is non-blocking and extremely fast. A single, dedicated consumer thread is the consumer. It waits on the queue, and when a message arrives, it does the slow work of writing it to the various `LogTarget`s."
*   **Choose the Right Primitive:** "A `BlockingQueue` is the perfect concurrency primitive for this pattern. It's a thread-safe queue that handles all the synchronization for us. The producers use the non-blocking `offer()` method, and the consumer uses the blocking `take()` method, which efficiently waits until there is work to do."
*   **Discuss Decoupling and Resilience:** "This design decouples the application's logic from the logging mechanism. The application doesn't need to know or care how or where the log is being written. This also improves resilience. If a file write operation is slow, it only delays the dedicated logger thread; it doesn't slow down the entire application."
*   **Mention Graceful Shutdown:** "In a production system, we would need a `shutdown()` method for the logger. This method would interrupt the worker thread and should ideally wait for the queue to be empty before terminating, ensuring all submitted logs are written. This prevents losing logs when the application is shutting down."