package com.ratelimiter;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/*
* Takes as input the MAX limit of requests allowed per window and the size of window in seconds
* Rate limits requests on the basis of sliding window algorithm
* */
public class SmoothRateLimiter implements RateLimiter {

    private final int maxRequestsInWindow;
    private final CounterStore counterStore;
    private final int BUFFER_SIZE = 5;
    private final int windowSizeInSeconds;
    private volatile long lastRefreshedIndex;

    public SmoothRateLimiter(int maxRequestsInWindow, int windowSizeInSeconds) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequestsInWindow = maxRequestsInWindow;
        this.counterStore = new CounterStore(windowSizeInSeconds);
    }

    @Override
    public boolean request() {

        long currentTimeSeconds = System.currentTimeMillis() / 1000;

        int currentWindowElapsedSeconds = (int) (currentTimeSeconds % this.windowSizeInSeconds);
        double forecastFactor = (this.windowSizeInSeconds - currentWindowElapsedSeconds) * 1d / this.windowSizeInSeconds;

        //get previous and current counter
        AtomicInteger currentCounter = counterStore.getCounter(currentTimeSeconds);
        AtomicInteger previousCounter = counterStore.getPreviousCounter(currentTimeSeconds);

        double expectedCount = currentCounter.get() + previousCounter.get() * forecastFactor;

        //if calculated count is bound to go beyond limit at this rate then discard the request
        if (expectedCount > this.maxRequestsInWindow) {
            return false;
        }

        currentCounter.incrementAndGet();
        return true;
    }
}
