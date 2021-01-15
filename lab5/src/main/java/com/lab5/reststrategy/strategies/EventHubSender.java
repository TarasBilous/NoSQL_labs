package com.lab5.reststrategy.strategies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class EventHubSender extends DataSender {

    private static final Logger logger = LoggerFactory.getLogger(EventHubSender.class);

    private EventHubClient ehClient;

    public EventHubSender(@Value("${event.hub.namespace}") final String eventHubNamespace,
                          @Value("${event.hub.name}") final String eventHubName,
                          @Value("${event.hub.connection.string}") final String eventHubConnectionString,
                          @Value("${event.hub.primary.key}") final String eventHubPrimaryKey) {
        final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
                .setNamespaceName(eventHubNamespace)
                .setEventHubName(eventHubName)
                .setSasKeyName(eventHubConnectionString)
                .setSasKey(eventHubPrimaryKey);

        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        try {
            this.ehClient = EventHubClient.createSync(connStr.toString(), executorService);
        } catch (EventHubException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBatch(JSONArray jsonArray, int startRaw, int endRaw) {
        final Gson gson = new GsonBuilder().create();
        logger.info("Raws from {} to {}", startRaw, endRaw + jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            logger.info("Document index: {}", i);
            byte[] payloadBytes = gson.toJson(jsonObject).getBytes(Charset.defaultCharset());
            EventData sendEvent = EventData.create(payloadBytes);

            try {
                ehClient.sendSync(sendEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
