package com.ratelimiter;

import java.util.HashMap;
import java.util.Map;

public class RuleCache implements RuleCacheConsumer, RuleCachePublisher {
    private final Map<String, Rule> userToRuleMap;

    public RuleCache() {
        userToRuleMap = new HashMap<>();
    }

    public void addRule() {

    }

    public Rule getRule(String userID) {
        return null;
    }
}
