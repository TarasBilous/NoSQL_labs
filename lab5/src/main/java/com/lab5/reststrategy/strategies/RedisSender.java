package com.lab5.reststrategy.strategies;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Map;

@Service
public class RedisSender extends DataSender {

    private static final Logger logger = LoggerFactory.getLogger(RedisSender.class);

    private final Jedis jedis;

    private int maxNumber;
    private String mapName;
    private String fileName;

    public RedisSender(@Value("${redis.use.ssl}") final boolean useSSL,
                       @Value("${redis.max.number}") final int maxNumber,
                       @Value("${redis.cache.hostname}") final String cacheHostname,
                       @Value("${redis.cache.key}") final String cacheKey,
                       @Value("${redis.map.name}") final String mapName,
                       @Value("${redis.file.name}") final String fileName,
                       @Value("${redis.port}") final int port) {
        logger.info("Max records number: {}", maxNumber);
        logger.info("Host: {}", cacheHostname);
        logger.info("Port: {}", port);
        logger.info("Key: {}", cacheKey);
        logger.info("Catalog name: {}", mapName);

        this.maxNumber = maxNumber;
        this.mapName = mapName;
        this.fileName = fileName;

        JedisShardInfo jedisShardInfo = new JedisShardInfo(cacheHostname, port, useSSL);
        jedisShardInfo.setPassword(cacheKey);
        this.jedis = new Jedis(jedisShardInfo);
    }

    @Override
    public void sendBatch(JSONArray jsonArray, int startRaw, int endRaw) {
        jedis.hset(mapName, "File", "None");
        Map<String, String> redisData = jedis.hgetAll(mapName);

        if (checkIfFileExist(jedis)) {
            logger.info("LENGTH: {}", jsonArray.length());
            jedis.set("Raws", startRaw + ":" + endRaw);
            showData(jsonArray.length(), jsonArray, jedis, redisData);
        }
    }

    public void showData(int count, JSONArray jsonArray, Jedis jedis, Map<String, String> map) {
        jedis.hset(mapName, "Raws", String.valueOf(count));
        if (jsonArray.length() != maxNumber) {
            jedis.hset(mapName, fileName, jsonArray.toString());
            logger.info("Raws from file {}: {}", fileName, jedis.hget(mapName, fileName));
            jedis.hset(mapName, "File", fileName);

            jedis.hset(mapName, "Status", "NotFinished");
        } else {
            logger.info("Raws from file {}: {}", fileName, jedis.hget(mapName, fileName));
            jedis.hset(mapName, "Raws", "" + count);
            jedis.hset(mapName, "Status", "Completed");
            jedis.hset(mapName, "Info", "First attempt to input this file");
            logger.info(map.get("Status"));
            jedis.close();
        }
    }

    public boolean checkIfFileExist(Jedis jedis) {
        Map<String, String> map = jedis.hgetAll(mapName);
        String name = map.get("File");
        String status = map.get("Status");

        if (!name.equals(fileName)) {
            jedis.hset(mapName, "File", fileName);
        } else {
            if (status.equals("Completed")) {
                jedis.hset(mapName, "Info", "Retry to input this file");
                logger.info("Such file '{}' : already exists. Application stop", fileName);
                return false;
            }
        }
        return true;
    }
}