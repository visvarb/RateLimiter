package com.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public class SmoothRateLimiter implements RateLimiter {

    private final int maxLimit;
    private volatile AtomicInteger[] counterLot;
    private final int BUFFER_SIZE = 5;
    private final long refreshWindowInSeconds;
    private volatile long lastRefreshedIndex;

    public SmoothRateLimiter(int maxLimit, long refreshWindowInSeconds) {
        this.refreshWindowInSeconds = refreshWindowInSeconds;
        this.maxLimit = maxLimit;
        this.counterLot = new AtomicInteger[BUFFER_SIZE];

        for (int index = 0; index < BUFFER_SIZE; index++)
            counterLot[index] = new AtomicInteger(0);
    }

    @Override
    public boolean request() {

        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = (currentTimeMillis / 1000);

        int currentWindowElapsedSeconds = Math.toIntExact(currentTimeSeconds % this.refreshWindowInSeconds);
        double forecastFactor = (this.refreshWindowInSeconds - currentWindowElapsedSeconds) * 1d / this.refreshWindowInSeconds;

        //get index of counters associated with current minute
        int currentCounterIndex = Math.toIntExact(currentTimeSeconds / this.refreshWindowInSeconds) % BUFFER_SIZE;
        int prevCounterIndex = (Math.toIntExact(currentTimeSeconds / this.refreshWindowInSeconds) - 1) % BUFFER_SIZE;

        //get previous and current counter
        AtomicInteger currentCounter = this.counterLot[currentCounterIndex];
        AtomicInteger previousCounter = this.counterLot[prevCounterIndex];

        //If currentCounter index has moved ahead rest the value in currentCounter before use
        if (this.lastRefreshedIndex != currentCounterIndex && currentCounter.compareAndSet(currentCounter.get(), 0)) {
            this.lastRefreshedIndex = currentCounterIndex;
        }

        double expectedCount = currentCounter.get() + previousCounter.get() * forecastFactor;

        //if calculated count is bound to go beyond limit at this rate then discard the request
        if (expectedCount > maxLimit) {
            return false;
        }

        currentCounter.incrementAndGet();
        return true;
    }
}
