package com.lab.experiment02;

public class Main02 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("PID: " + ProcessHandle.current().pid());
        System.out.println("Open VisualVM and attach!");
        Thread.sleep(10_000);

        System.out.println("=== Phase 1: Adding users to cache ===");

        // Every 500ms, create 1000 new users
        // In real projects: caching user on each HTTP request
        for (int batch = 1; batch <= 50; batch++) {
            for (int i = 0; i < 1000; i++) {
                long userId = (long) batch * 1000 + i;
                User user = new User(userId, "User-" + userId);
                UserCache.put(user);
            }
            System.out.printf("Batch %d, Cache size: %d, Heap used: %d MB%n",
                    batch,
                    UserCache.size(),
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024
            );
            Thread.sleep(500);
        }

        System.out.println("\n=== Phase 2: Users are no longer needed ===");
        System.out.println("But static cache still holds references to them!");
        System.out.println("Calling System.gc()...");
        System.gc();
        Thread.sleep(2_000);
        System.gc();

        System.out.println("Cache size: " + UserCache.size());
        System.out.println("Heap is still full! This is a LEAK.");

        System.out.println("\n=== Wait 30 seconds — take a Heap Dump ===");
        Thread.sleep(30_000);

        System.out.println("\n=== Phase 3: FIX — clearing the cache ===");
        UserCache.evictAll();
        System.gc();
        Thread.sleep(2_000);
        System.gc();

        System.out.println("Cache size: " + UserCache.size());
        System.out.println("Now check the heap — it should drop!");
        Thread.sleep(30_000);
    }
}

