package com.ratelimiter;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/*
* Takes as input the MAX limit of requests allowed per window and the size of window in seconds
* Rate limits requests on the basis of sliding window algorithm
* */
public class SmoothRateLimiter implements RateLimiter {

    private final int maxRequestsInWindow;
    private final AtomicInteger[] counterLot;
    private final int BUFFER_SIZE = 5;
    private final int windowSizeInSeconds;
    private volatile long lastRefreshedIndex;

    public SmoothRateLimiter(int maxRequestsInWindow, int windowSizeInSeconds) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequestsInWindow = maxRequestsInWindow;
        this.counterLot = new AtomicInteger[BUFFER_SIZE];

        for (int index = 0; index < BUFFER_SIZE; index++)
            counterLot[index] = new AtomicInteger(0);
    }

    @Override
    public boolean request() {

        long currentTimeSeconds = System.currentTimeMillis() / 1000;

        int currentWindowElapsedSeconds = (int) (currentTimeSeconds % this.windowSizeInSeconds);
        double forecastFactor = (this.windowSizeInSeconds - currentWindowElapsedSeconds) * 1d / this.windowSizeInSeconds;

        //get index of counters associated with current minute
        int currentCounterIndex = (int) (currentTimeSeconds / this.windowSizeInSeconds) % BUFFER_SIZE;
        int prevCounterIndex = (int) (((currentTimeSeconds / this.windowSizeInSeconds) - 1) % BUFFER_SIZE);

        //get previous and current counter
        AtomicInteger currentCounter = this.counterLot[currentCounterIndex];
        AtomicInteger previousCounter = this.counterLot[prevCounterIndex];

        //If currentCounter index has moved ahead reset currentCounter to 0 before use
        if (this.lastRefreshedIndex != currentCounterIndex &&
                currentCounter.compareAndSet(currentCounter.get(), 0)) {
            this.lastRefreshedIndex = currentCounterIndex;
        }

        double expectedCount = currentCounter.get() + previousCounter.get() * forecastFactor;

        //if calculated count is bound to go beyond limit at this rate then discard the request
        if (expectedCount > this.maxRequestsInWindow) {
            return false;
        }

        currentCounter.incrementAndGet();
        return true;
    }

    public void print() {
        for (int index = 0; index < BUFFER_SIZE; index++)
            System.out.println(counterLot[index].get());
    }
}
