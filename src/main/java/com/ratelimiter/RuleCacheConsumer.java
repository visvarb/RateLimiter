package com.ratelimiter;

public interface RuleCacheConsumer {
    public Rule getRule(String userID);
}
