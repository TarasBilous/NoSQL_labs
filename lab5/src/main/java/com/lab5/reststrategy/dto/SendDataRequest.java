package com.lab5.reststrategy.dto;

public class SendDataRequest {

    private String dataUrl;
    private String dataDestination;

    public SendDataRequest(String dataUrl, String dataDestination) {
        this.dataUrl = dataUrl;
        this.dataDestination = dataDestination;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDataDestination() {
        return dataDestination;
    }

    public void setDataDestination(String dataDestination) {
        this.dataDestination = dataDestination;
    }
}