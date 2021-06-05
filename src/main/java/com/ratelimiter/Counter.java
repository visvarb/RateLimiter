package com.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    private volatile long lastRefreshedTimeInSeconds;
    private final AtomicInteger atomicCounter;

    public Counter(long lastRefreshedTimeInSeconds) {
        this.lastRefreshedTimeInSeconds = lastRefreshedTimeInSeconds;
        atomicCounter = new AtomicInteger(0);
    }

    private void refresh(long currentTimeInSeconds) {

        if (this.lastRefreshedTimeInSeconds != currentTimeInSeconds &&
                atomicCounter.compareAndSet(atomicCounter.get(), 0)) {
            this.lastRefreshedTimeInSeconds = currentTimeInSeconds;
        }
    }

    public AtomicInteger getAtomicCounter(long currentTimeInSeconds) {
        refresh(currentTimeInSeconds);
        return atomicCounter;
    }
}
