package com.iti.example.tripreminder.Worker;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationIDGenerator {
    private final static AtomicInteger atomicInteger = new AtomicInteger(2);
    public static int getID() {
        return atomicInteger.incrementAndGet();
    }
}