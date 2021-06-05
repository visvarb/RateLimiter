package com.ratelimiter;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SmoothRateLimiter smoothRateLimiter = new SmoothRateLimiter(2, 2);

        int acceptedCount = 0, rejectedCount = 0;
        System.out.println(System.currentTimeMillis()/1000);
        for (int i = 0; i < 10; i++) {
            //System.out.println("request Time:" + System.currentTimeMillis()/1000);
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
