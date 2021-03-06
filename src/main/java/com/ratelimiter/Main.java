package com.ratelimiter;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SmoothRateLimiter smoothRateLimiter = new SmoothRateLimiter(1, 3);

        int acceptedCount = 0, rejectedCount = 0;
        System.out.println(System.currentTimeMillis()/1000);
        for (int i = 0; i < 10; i++) {
            if(smoothRateLimiter.request())
                acceptedCount++;
            else
                rejectedCount++;
            Thread.sleep(1000);
        }
        System.out.println(System.currentTimeMillis()/1000);
        System.out.println("Accepted Count:" + acceptedCount);
        System.out.println("Rejected Count:" + rejectedCount);
    }
}
