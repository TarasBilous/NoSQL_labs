package com.lab5.reststrategy.controllers;

import com.lab5.reststrategy.dto.SendDataRequest;
import com.lab5.reststrategy.strategies.DataSender;
import com.lab5.reststrategy.Strategist;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class SendDataController {

    private Strategist strategist;

    public SendDataController(Strategist logService) {
        this.strategist = logService;
    }

    @PostMapping("/lab")
    public void addNewUrl(@RequestBody SendDataRequest request) {
        String strategyName = request.getDataDestination();
        DataSender dataSender = this.strategist.getStrategy(strategyName);
        
        try {
            dataSender.sendData(request.getDataUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}