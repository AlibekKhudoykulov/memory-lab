# Memory Leak Lab 🧪

A hands-on Java project demonstrating **memory leaks** caused by obsolete references in a custom stack implementation. Use this with **VisualVM** to observe heap behavior in real time.

## 📖 What You'll Learn

- How obsolete object references cause memory leaks in Java
- Why the Garbage Collector (GC) cannot reclaim objects that are still referenced
- How to detect memory leaks using VisualVM and heap dumps
- The correct way to fix obsolete reference leaks

## 🏗️ Project Structure

```
src/main/java/org/example/
├── Main.java        # Entry point — runs the leak demonstration
└── LeakyStack.java  # Custom stack with both leaky and fixed pop methods
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
   mvn exec:java -Dexec.mainClass="org.example.Main"
   ```
   Or run directly:
   ```bash
   java -cp target/classes org.example.Main
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

| Phase | Heap Usage |
|-------|-----------|
| Push 1M objects (1KB each) | ~1 GB allocated |
| Pop all (leaky) | ~1 GB remains — NOT freed |
| Pop all (fixed) | Drops significantly after GC |

## 🔑 Key Takeaway

> Whenever a class manages its own memory (like arrays), the programmer must manually null out obsolete references. Otherwise, the GC has no way to know those references are no longer needed.

— *Effective Java, Item 7: Eliminate obsolete object references*

## 📝 License

This project is for educational purposes.

