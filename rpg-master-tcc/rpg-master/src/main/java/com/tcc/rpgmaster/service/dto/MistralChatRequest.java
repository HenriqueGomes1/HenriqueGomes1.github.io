package com.tcc.rpgmaster.service.dto;

import java.util.List;

/**
 * Corpo da requisicao enviada para o endpoint
 * https://api.mistral.ai/v1/chat/completions
 */
public class MistralChatRequest {

    private String model;
    private List<MistralMessage> messages;
    private double temperature = 0.8;

    public MistralChatRequest() {
    }

    public MistralChatRequest(String model, List<MistralMessage> messages, double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<MistralMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<MistralMessage> messages) {
        this.messages = messages;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
