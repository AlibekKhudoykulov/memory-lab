package com.lab.experiment02;

public class User {
    private final long id;
    private final String name;
    private final byte[] data; // each user is 10KB

    public User(long id, String name) {
        this.id = id;
        this.name = name;
        this.data = new byte[10 * 1024]; // 10KB payload
    }

    public long getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "'}";
    }
}

