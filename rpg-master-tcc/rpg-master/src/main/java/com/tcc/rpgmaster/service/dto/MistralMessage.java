package com.tcc.rpgmaster.service.dto;

/**
 * Representa uma mensagem no formato de chat da API da Mistral
 * (compativel com o padrao "role" / "content" usado pela maioria
 * das APIs de IA generativa por chat).
 */
public class MistralMessage {

    private String role;
    private String content;

    public MistralMessage() {
    }

    public MistralMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
