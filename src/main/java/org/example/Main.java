package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("PID: " + ProcessHandle.current().pid());
        System.out.println("Open VisualVM and attach to this PID!");
        System.out.println("Waiting 10 seconds...");
        Thread.sleep(10_000); // Time for VisualVM to connect

        LeakyStack stack = new LeakyStack();

        // Phase 1: Push 1 million large objects
        System.out.println("Pushing objects...");
        for (int i = 0; i < 1_000_000; i++) {
            stack.push(new byte[1024]); // each is 1KB
            if (i % 100_000 == 0) {
                System.out.println("Push: " + i + ", size: " + stack.size());
            }
        }
        System.out.println("Push complete. Check the heap!");
        Thread.sleep(10_000);

        // Phase 2: Pop all elements (LEAKY version)
        System.out.println("Popping all elements (LEAKY)...");
        for (int i = 0; i < 1_000_000; i++) {
            stack.popFixed(); // ❌ Memory leak!
        }
        System.out.println("Pop complete. Stack size: " + stack.size());
        System.out.println("NOTE: size=0, but heap is still full!");

        // Phase 3: Force GC
        System.out.println("Calling System.gc()...");
        System.gc();
        Thread.sleep(2_000);
        System.gc();

        // Phase 4: Wait 30 seconds — take a heap dump in VisualVM
        System.out.println("Waiting 30 seconds. Take a Heap Dump in VisualVM!");
        Thread.sleep(30_000);

        // Stack is still in scope, so GC cannot collect it
        System.out.println("Stack reference: " + stack.size());
    }
}