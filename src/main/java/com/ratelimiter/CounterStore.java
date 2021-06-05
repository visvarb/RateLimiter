package com.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterStore {
    private final int BUFFER_SIZE = 5;
    private final Counter[] counterLot;
    private final int counterWindowInSeconds;

    public CounterStore(int counterWindowInSeconds) {

        this.counterWindowInSeconds = counterWindowInSeconds;
        this.counterLot = new Counter[BUFFER_SIZE];
        for (int index = 0; index < BUFFER_SIZE;index++)
            this.counterLot[index] = new Counter(0);
    }

    public AtomicInteger getCounter(long timeStampInSeconds) {
        int index = (int) (timeStampInSeconds / this.counterWindowInSeconds) % BUFFER_SIZE;
        Counter counter = counterLot[index];
        return counter.getAtomicCounter(timeStampInSeconds);
    }

    public AtomicInteger getPreviousCounter(long timeStampInSeconds) {
        int index = (int) ((timeStampInSeconds / this.counterWindowInSeconds) - 1) % BUFFER_SIZE;
        Counter counter = counterLot[index];
        return counter.getAtomicCounter(timeStampInSeconds);
    }
}
