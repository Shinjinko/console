package com.console.app.main.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {
    private final AtomicLong counter = new AtomicLong(0);

    public synchronized long increment() {
        return counter.incrementAndGet();
    }

    public long getCount() {
        return counter.get();
    }

    public synchronized void reset() {
        counter.set(0);
    }
}