package com.lab.experiment02;

import java.util.HashMap;
import java.util.Map;

public class UserCache {

    // ❌ STATIC + never cleared = LEAK
    private static final Map<Long, User> CACHE = new HashMap<>();

    public static void put(User user) {
        CACHE.put(user.getId(), user);
    }

    public static User get(Long id) {
        return CACHE.get(id);
    }

    public static int size() {
        return CACHE.size();
    }

    // In real projects, this method often doesn't exist or is never called
    public static void evictAll() {
        CACHE.clear();
    }
}

