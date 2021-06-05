package com.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterStore {
    private final int STORE_SIZE = 5;
    private final Counter[] counterLot;
    private final int counterWindowInSeconds;

    public CounterStore(int counterWindowInSeconds) {

        this.counterWindowInSeconds = counterWindowInSeconds;
        this.counterLot = new Counter[STORE_SIZE];
        for (int index = 0; index < STORE_SIZE; index++)
            this.counterLot[index] = new Counter(0);
    }

    public AtomicInteger getCounter(long timeStampInSeconds) {
        long windowNumber = timeStampInSeconds / this.counterWindowInSeconds;
        int index = (int) windowNumber % STORE_SIZE;

        Counter counter = counterLot[index];

        long windowStartTimestamp = windowNumber * this.counterWindowInSeconds;
        return counter.getAtomicCounter(windowStartTimestamp);
    }
}
