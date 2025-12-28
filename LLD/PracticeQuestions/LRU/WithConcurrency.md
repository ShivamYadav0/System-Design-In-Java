# ☕ Java LLD Concurrency Template for LRU Cache

> **Goal:** Design a thread-safe LRU Cache that can be accessed by multiple threads simultaneously without causing data corruption or inconsistent states.

---

## 1️⃣ Core Concurrency Problem

A standard LRU Cache (like one made from a raw `HashMap` and a doubly linked list, or even the standard `LinkedHashMap`) is **not thread-safe**. If multiple threads call `get()` and `put()` concurrently, several issues can arise:

1.  **`put()` Race Conditions:** Two threads could try to add a new element at the same time. One might see the cache is not full, get preempted, and by the time it resumes, the other thread has already filled the cache. This can lead to the cache exceeding its capacity.
2.  **`get()` and `put()` Race Conditions:** A `get()` operation involves moving a node to the front of the list. A `put()` operation might also be moving a node or removing one. If these happen at the same time on different parts of the list, pointers in the linked list can get corrupted, leading to broken chains or infinite loops during traversal.
3.  **Visibility Issues:** Without proper synchronization, a value written to the cache by one thread might not be visible to another thread that subsequently reads from it.

---

## 2️⃣ The Solution: A Synchronized Wrapper or `ConcurrentHashMap`

There are two primary approaches to making an LRU cache thread-safe in Java.

### Option A: The Simple Approach (`Collections.synchronizedMap`)

You can wrap the `LinkedHashMap`-based LRU implementation with `Collections.synchronizedMap`. This decorates every method of the map with a `synchronized` block, using the map object itself as the lock.

```java
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// A thread-safe LRU Cache using a synchronized wrapper.
public class SynchronizedLruCache<K, V> {
    private final int capacity;
    private final Map<K, V> cache;

    public SynchronizedLruCache(int capacity) {
        this.capacity = capacity;
        // We create the LRU logic using LinkedHashMap first...
        Map<K, V> lruMap = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return this.size() > capacity;
            }
        };
        // ...then wrap it in a synchronized map.
        this.cache = Collections.synchronizedMap(lruMap);
    }

    public V get(K key) {
        // The synchronizedMap ensures only one thread can be in this method at a time.
        return cache.get(key);
    }

    public void put(K key, V value) {
        // The synchronizedMap ensures only one thread can be in this method at a time.
        cache.put(key, value);
    }
}
```
*   **Pro:** Very simple to implement.
*   **Con:** Poor performance. It uses a single lock for the entire cache. Every read and write operation is blocked, even if they are for different keys. This is a major bottleneck in a highly concurrent environment.

### Option B: The High-Performance Approach (Using `ConcurrentHashMap` and a `ConcurrentLinkedDeque`)

For high performance, we need more granular locking. We can build our own LRU cache by combining a `ConcurrentHashMap` for the lookups and a `ConcurrentLinkedDeque` to manage the usage order.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

// A high-performance, thread-safe LRU Cache.
public class ConcurrentLruCache<K, V> {
    private final int capacity;
    // Provides thread-safe O(1) lookups.
    private final ConcurrentHashMap<K, V> map;
    // Provides a thread-safe way to manage the access order.
    private final ConcurrentLinkedDeque<K> queue;

    public ConcurrentLruCache(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            // To update the order, we remove and re-add the key to the queue.
            // This is not a single atomic operation but is safe enough for usage tracking.
            queue.remove(key);
            queue.addFirst(key);
        }
        return value;
    }

    public void put(K key, V value) {
        if (map.containsKey(key)) {
            // Key already exists, just update its value and move to front.
            queue.remove(key);
        } else if (map.size() >= capacity) {
            // Cache is full, evict the least recently used element.
            K eldestKey = queue.pollLast();
            if (eldestKey != null) {
                map.remove(eldestKey);
            }
        }
        // Add the new item.
        map.put(key, value);
        queue.addFirst(key);
    }
}
```
*   **Pro:** Much higher concurrency. `ConcurrentHashMap` allows multiple readers and writers to operate at the same time using fine-grained locks on different segments of the map.
*   **Con:** More complex to implement. The operation to update the queue (`remove` then `addFirst`) is not atomic, which can lead to minor inaccuracies in the LRU ordering under very high contention, but it does not corrupt the cache's state. This is often an acceptable trade-off for the massive performance gain.

---

## 3️⃣ How to TALK Concurrency in Interviews

*   **Identify the Problem:** "A standard `LinkedHashMap` is not thread-safe. If we use it in a multi-threaded context, we'll face race conditions that can corrupt the internal linked list, cause the cache to exceed its capacity, and lead to visibility issues where one thread doesn't see another thread's writes."
*   **Propose the Simple Solution:** "The easiest way to make it thread-safe is to wrap it with `Collections.synchronizedMap`. This puts a single, global lock around the entire cache. It's simple and correct, but it has poor performance because it serializes all access. Every read blocks every other read and write."
*   **Propose the High-Performance Solution:** "A much better approach for a high-performance system is to build the cache from concurrent components. We can use a `ConcurrentHashMap` for the O(1) key-value storage and a `ConcurrentLinkedDeque` to manage the access order. The `ConcurrentHashMap` provides fine-grained locking, allowing many threads to access the cache simultaneously as long as they aren't working on the same internal 'segment' of the map."
*   **Discuss the Trade-offs:** "The trade-off with the high-performance approach is a slight loss of strict LRU ordering under heavy contention. When we `get` an item, we update its position in the deque by removing it and re-adding it to the front. This is two separate, non-atomic operations. It's possible for another thread to intervene, but this only affects the 'least recently used' ordering, it doesn't cause data corruption. For most applications, this is a very acceptable trade-off for the significant increase in throughput."