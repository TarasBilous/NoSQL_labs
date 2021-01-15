package com.lab5.reststrategy;

import com.lab5.reststrategy.strategies.RedisSender;
import com.lab5.reststrategy.strategies.EventHubSender;
import com.lab5.reststrategy.strategies.DataSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Strategist {

    private Map<String, DataSender> strategies;

    @Autowired
    public Strategist(EventHubSender eventHub, RedisSender redis) {
        strategies = createStrategies(eventHub, redis);
    }

    public DataSender getStrategy(String strategyName) {
        return strategies.get(strategyName);
    }

    private Map<String, DataSender> createStrategies(EventHubSender eventHub, RedisSender redis) {
        Map<String, DataSender> strategies = new HashMap<>();
        strategies.put("eventHub", eventHub);
        strategies.put("redis", redis);
        return strategies;
    }
}