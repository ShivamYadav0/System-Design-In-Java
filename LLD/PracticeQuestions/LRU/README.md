# ☕ Java LLD Starter Template for LRU Cache

> **Goal:** Design a data structure for a Least Recently Used (LRU) cache, which evicts the least recently used item when the cache is full.

---

## 0️⃣ How to Use This README in Interviews

**Think first, code second.**

Interview flow:

1.  **State the Core Data Structures:** "To achieve O(1) for both `get` and `put`, an LRU cache requires two data structures: a **hash map** for quick lookups and a **doubly linked list** to keep track of the usage order."
2.  **Explain the Logic:** Describe how `get` moves an element to the front of the list and how `put` adds a new element to the front, potentially removing the last element if the capacity is exceeded.
3.  **The `LinkedHashMap` Shortcut:** "Java provides a perfect implementation for this out of the box: the `LinkedHashMap` class."
4.  Implement the class using `LinkedHashMap`.

---

## 1️⃣ Core Data Structures and Logic

An efficient LRU cache must perform its `get(key)` and `put(key, value)` operations in O(1) time.

1.  **Hash Map (`HashMap<K, Node<V>>`)**: This provides the O(1) lookup. The key is the key of our cache entry, and the value is a direct reference to the node in our linked list.
2.  **Doubly Linked List (`Node<V>`)**: This is used to maintain the order of usage. The most recently used items are at the head (front) of the list, and the least recently used items are at the tail (back).

### How Operations Work:

*   **`get(key)`**:
    1.  Look up the key in the hash map.
    2.  If it exists, you have the node. Move this node to the front of the doubly linked list.
    3.  Return the value.
*   **`put(key, value)`**:
    1.  Check if the key already exists in the hash map.
        *   If yes, update its value and move the corresponding node to the front of the list.
    2.  If the key does not exist:
        *   Create a new node.
        *   Add it to the front of the list.
        *   Add the key and the new node reference to the hash map.
        *   Check if the cache size exceeds its capacity.
            *   If yes, remove the node from the tail of the list and remove its key from the hash map.

---

## 2️⃣ The `LinkedHashMap` Implementation (The Easy Way)

Java's `LinkedHashMap` can be configured to act as an LRU cache by overriding the `removeEldestEntry` method.

*   It internally uses a hash map and a doubly linked list.
*   The special constructor `LinkedHashMap(initialCapacity, loadFactor, accessOrder)` is key. Setting `accessOrder` to `true` makes it order entries by access, not insertion.

```java
import java.util.LinkedHashMap;
import java.util.Map;

// A generic LRU Cache implementation using LinkedHashMap.
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LruCache(int capacity) {
        // The 'true' for accessOrder is what makes this an LRU cache.
        // Every time an entry is accessed (get), it's moved to the end of the list.
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    /**
     * This method is called by put and putAll after inserting a new entry.
     * We override it to implement the eviction policy.
     * It returns true if the eldest entry should be removed.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // The map will automatically remove the least recently used entry
        // if this method returns true.
        return this.size() > this.capacity;
    }

    public static void main(String[] args) {
        LruCache<Integer, String> cache = new LruCache<>(2);

        cache.put(1, "A"); // Cache: {1:"A"}
        cache.put(2, "B"); // Cache: {1:"A", 2:"B"}

        System.out.println(cache.get(1)); // Returns "A". Cache is now {2:"B", 1:"A"} (1 is most recent)

        cache.put(3, "C"); // Cache full. Evicts eldest (2). Cache: {1:"A", 3:"C"}

        System.out.println(cache.get(2)); // Returns null, as it was evicted.

        System.out.println(cache); // Output: {1=A, 3=C}
    }
}
```
