package com.console.app.main.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {
    private final AtomicLong userVisits = new AtomicLong(0);

    public synchronized void incrementUserVisits() {
        userVisits.incrementAndGet();
    }

    public long getUserVisitsCount() {
        return userVisits.get();
    }

}