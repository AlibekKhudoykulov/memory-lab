package org.example;

import java.util.Arrays;

public class LeakyStack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 16;

    public LeakyStack() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    // ❌ MEMORY LEAK HERE!
    // size decreases, but elements[size] still holds a reference to the object
    // GC cannot collect these objects
    public Object pop() {
        if (size == 0) throw new IllegalStateException("Stack empty");
        return elements[--size];
    }

    // ✅ FIXED VERSION (clears obsolete reference)
    public Object popFixed() {
        if (size == 0) throw new IllegalStateException("Stack empty");
        Object result = elements[--size];
        elements[size] = null; // Clear the obsolete reference
        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }

    public int size() {
        return size;
    }
}