# Memory Leak Lab 🧪

A hands-on Java project demonstrating **memory leaks** caused by obsolete references in a custom stack implementation. Use this with **VisualVM** to observe heap behavior in real time.

## 📖 What You'll Learn

- How obsolete object references cause memory leaks in Java
- Why the Garbage Collector (GC) cannot reclaim objects that are still referenced
- How to detect memory leaks using VisualVM and heap dumps
- The correct way to fix obsolete reference leaks

## 🏗️ Project Structure

```
src/main/java/com/lab/
├── experiment01/
│   ├── Main.java        # Experiment 1 — obsolete reference leak in a custom stack
│   └── LeakyStack.java  # Custom stack with both leaky and fixed pop methods
└── experiment02/
    ├── Main02.java      # Experiment 2 — unbounded static cache leak
    ├── User.java        # Simple user entity (10KB each)
    └── UserCache.java   # Static HashMap cache that never evicts
```

## 🚀 How to Run

### Prerequisites
- Java 11+
- Maven
- [VisualVM](https://visualvm.github.io/) (recommended for monitoring)

### Steps

1. **Build the project:**
   ```bash
   mvn compile
   ```

2. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.lab.experiment01.Main"
   ```
   Or run directly:
   ```bash
   java -cp target/classes com.lab.experiment01.Main
   ```

3. **Attach VisualVM** to the printed PID within 10 seconds.

4. **Observe the heap** — watch memory grow during the push phase and notice it does NOT shrink after popping (when using the leaky version).

5. **Take a Heap Dump** in VisualVM during the 30-second wait to inspect retained objects.

## 🐛 The Bug

In `LeakyStack.pop()`:
```java
public Object pop() {
    if (size == 0) throw new IllegalStateException("Stack empty");
    return elements[--size]; // ❌ elements[size] still references the object!
}
```

Even though `size` decreases, the array still holds references to popped objects. The GC sees them as reachable and **cannot collect them**.

## ✅ The Fix

In `LeakyStack.popFixed()`:
```java
public Object popFixed() {
    if (size == 0) throw new IllegalStateException("Stack empty");
    Object result = elements[--size];
    elements[size] = null; // ✅ Clear the obsolete reference
    return result;
}
```

Setting the array slot to `null` allows the GC to reclaim the object.

## 📊 Expected Behavior

### Experiment 1: Obsolete Reference Leak

| Phase | Heap Usage |
|-------|-----------|
| Push 1M objects (1KB each) | ~1 GB allocated |
| Pop all (leaky) | ~1 GB remains — NOT freed |
| Pop all (fixed) | Drops significantly after GC |

### Experiment 2: Unbounded Static Cache Leak

| Phase | Heap Usage |
|-------|-----------|
| Add 50K users (10KB each) to static cache | ~500 MB allocated |
| Call System.gc() without clearing cache | ~500 MB remains — NOT freed |
| Call `evictAll()` + GC | Drops back to baseline |

## 🐛 Experiment 2: The Bug

In `UserCache`:
```java
private static final Map<Long, User> CACHE = new HashMap<>();
```

The cache is **static** and **never evicted**. Every user added stays in memory forever, even if the application no longer needs them. This simulates a common real-world leak: caching HTTP session data or database entities without a TTL or eviction policy.

### ✅ The Fix

- Call `UserCache.evictAll()` when cached data is no longer needed
- In production, use bounded caches with eviction (e.g., Guava `CacheBuilder`, Caffeine, or `WeakHashMap`)
- Set a max size or TTL (time-to-live) on cache entries

### How to Run Experiment 2

```bash
java -cp target/classes com.lab.experiment02.Main02
```

## 🔑 Key Takeaway

> Whenever a class manages its own memory (like arrays), the programmer must manually null out obsolete references. Otherwise, the GC has no way to know those references are no longer needed.

— *Effective Java, Item 7: Eliminate obsolete object references*

## 📝 License

This project is for educational purposes.
