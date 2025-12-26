# 🧵 Concurrency‑First LLD Guide – FAANG Interview README

> **Goal:** Design Low Level Systems that remain **correct under concurrency**, explain trade‑offs clearly, and demonstrate *senior‑level* control over Java concurrency primitives.

This guide is **FAANG‑favorite material**. Most candidates fail here.

---

## 0️⃣ Interviewer Mindset (Critical Context)

Interviewers are NOT testing:

* Whether you remember `ReentrantLock` syntax
* Whether you can write lock‑free algorithms

They ARE testing:

* Can you **identify shared state**?
* Can you **choose the right concurrency primitive**?
* Can you **avoid race conditions & deadlocks**?
* Can you **explain correctness vs performance**?

📌 Saying *"this part needs to be thread‑safe"* already scores points.

---

## 1️⃣ Concurrency in LLD – The Right Mental Model

### Core Question

> **What data is shared, mutable, and accessed concurrently?**

Only that needs protection.

---

### Three Ways to Handle Concurrency

1️⃣ **Avoid shared state** (best)
2️⃣ **Make shared state immutable**
3️⃣ **Protect shared mutable state**

LLD is about choosing **which**.

---

## 2️⃣ Ownership Rule (Most Important Principle)

> **The class that owns the state owns the lock.**

❌ Bad Design

* External synchronization
* Locking from multiple places

✅ Good Design

* Lock is private
* State + lock live together

---

## 3️⃣ Locks in LLD (When & How to Use)

### 3.1 `synchronized` (Baseline)

```java
public synchronized void increment() {
    count++;
}
```

✅ Simple
❌ No flexibility
❌ Hard to scale

📌 Mention it, but prefer explicit locks in LLD discussions.

---

### 3.2 `ReentrantLock` (Preferred for LLD)

```java
private final ReentrantLock lock = new ReentrantLock();

public void update() {
    lock.lock();
    try {
        // critical section
    } finally {
        lock.unlock();
    }
}
```

Why interviewers like it:

* Explicit ownership
* Supports condition variables
* Fine‑grained control

---

### 3.3 Lock Granularity (Interview Gold)

| Strategy    | Description             |
| ----------- | ----------------------- |
| Coarse lock | One lock for everything |
| Fine lock   | Lock per entity         |

📌 Prefer **lock per entity** (Account, Elevator, Slot).

---

## 4️⃣ Atomic Variables (Fast & Safe)

Use atomics when:

* Single variable
* Simple state transitions

```java
private final AtomicInteger counter = new AtomicInteger(0);

counter.incrementAndGet();
```

❌ Not suitable for:

* Multiple dependent variables
* Complex invariants

---

## 5️⃣ Read–Write Locks (Advanced but Valuable)

```java
private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
```

Use when:

* Many reads
* Few writes

📌 Example: LRU Cache, configuration store.

---

## 6️⃣ Condition Variables (🔥 FAANG Favorite 🔥)

### 6.1 What Is a Condition Variable?

> A **condition** allows threads to wait until a *specific state* becomes true.

Think:

* Queue empty → wait
* Resource unavailable → wait

---

### 6.2 Why NOT `wait()` / `notify()`?

❌ Hard to manage
❌ Single condition per monitor
❌ Error‑prone

✅ `Condition` is:

* Explicit
* Safer
* Multiple conditions per lock

---

### 6.3 Basic Condition Pattern

```java
private final ReentrantLock lock = new ReentrantLock();
private final Condition notEmpty = lock.newCondition();

public void take() throws InterruptedException {
    lock.lock();
    try {
        while (queue.isEmpty()) {
            notEmpty.await();
        }
        queue.remove();
    } finally {
        lock.unlock();
    }
}
```

📌 Always use `while`, never `if` (spurious wakeups).

Extra

```java
private final Condition notFull = lock.newCondition();
private final int capacity = 10;

public void put(Object item) throws InterruptedException {
    lock.lock();
    try {
        while (queue.size() == capacity) {
            notFull.await(); // wait until space is available
        }
        queue.add(item);
        notEmpty.signal();
    } finally {
        lock.unlock();
    }
}

public Object take() throws InterruptedException {
    lock.lock();
    try {
        while (queue.isEmpty()) {
            notEmpty.await();
        }
        Object item = queue.remove();
        notFull.signal(); // notify producers there's space now
        return item;
    } finally {
        lock.unlock();
    }
}

```

---

### 6.4 Producer–Consumer (Classic Interview Example)

```java
Condition notFull = lock.newCondition();
Condition notEmpty = lock.newCondition();
```

* Producer waits on `notFull`
* Consumer waits on `notEmpty`

📌 This explanation alone screams *senior*.

---

## 7️⃣ BlockingQueue (High‑Level Alternative)

```java
BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
```

Internally uses:

* Locks
* Conditions

📌 In interviews, say:

> "We can replace this with a BlockingQueue for simplicity."

---

## 8️⃣ Thread‑Safe Design Patterns (LLD Perspective)

### 🔒 1. Thread‑Safe Singleton

Use Initialization‑on‑Demand Holder.

---

### 🧱 2. Immutable Object Pattern

* All fields final
* No setters

---

### 🧮 3. Atomic State Pattern

* Counters
* Flags

---

### 📦 4. Queue‑Based Architecture

* Decouple producers/consumers
* Natural concurrency

---

## 9️⃣ Common Concurrency Bugs (Mention These!)

❌ Race conditions
❌ Deadlocks
❌ Lost updates
❌ Visibility issues

📌 Mentioning *how you’d avoid them* matters more than code.

---

## 🔟 How to TALK Concurrency in Interviews

Say things like:

> "This service is stateless, so it’s naturally thread‑safe."

> "This entity owns mutable state, so locking is localized."

> "For blocking behavior, we use condition variables instead of busy waiting."

---

## 1️⃣1️⃣ Concurrency Mapping to LLD Problems

| Problem             | Key Primitive  |
| ------------------- | -------------- |
| Rate Limiter        | Atomics + Lock |
| LRU Cache           | ReadWriteLock  |
| Elevator            | Event Queue    |
| Parking Lot         | Lock per slot  |
| Notification System | BlockingQueue  |

---

## 🧠 Final FAANG Wisdom

> **Concurrency correctness beats performance.
> Performance can be optimized later.**

If your design is simple, explainable, and safe — you win.

---

