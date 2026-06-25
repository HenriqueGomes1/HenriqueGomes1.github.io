package com.tcc.rpgmaster.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Resposta do endpoint de chat completions da Mistral.
 * Ignora campos desconhecidos para nao quebrar caso a API
 * retorne metadados extras (uso de tokens, etc).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MistralChatResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    /**
     * Extrai o texto da primeira resposta gerada, ja tratado.
     */
    public String getPrimeiraRespostaTexto() {
        if (choices == null || choices.isEmpty()) {
            return null;
        }
        MistralMessage message = choices.get(0).getMessage();
        return message != null ? message.getContent() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private MistralMessage message;

        public MistralMessage getMessage() {
            return message;
        }

        public void setMessage(MistralMessage message) {
            this.message = message;
        }
    }
}
