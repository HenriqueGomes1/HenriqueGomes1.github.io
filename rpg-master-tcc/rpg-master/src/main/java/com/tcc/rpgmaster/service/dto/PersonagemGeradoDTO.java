package com.tcc.rpgmaster.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Estrutura esperada da resposta JSON da IA ao gerar a descricao
 * e a habilidade simplificada de um Personagem.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonagemGeradoDTO {

    private String descricao;
    private String habilidadeNome;
    private String habilidadeDescricao;
    private String atributoChave;
    private String tipoEfeito;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getHabilidadeNome() {
        return habilidadeNome;
    }

    public void setHabilidadeNome(String habilidadeNome) {
        this.habilidadeNome = habilidadeNome;
    }

    public String getHabilidadeDescricao() {
        return habilidadeDescricao;
    }

    public void setHabilidadeDescricao(String habilidadeDescricao) {
        this.habilidadeDescricao = habilidadeDescricao;
    }

    public String getAtributoChave() {
        return atributoChave;
    }

    public void setAtributoChave(String atributoChave) {
        this.atributoChave = atributoChave;
    }

    public String getTipoEfeito() {
        return tipoEfeito;
    }

    public void setTipoEfeito(String tipoEfeito) {
        this.tipoEfeito = tipoEfeito;
    }
}
