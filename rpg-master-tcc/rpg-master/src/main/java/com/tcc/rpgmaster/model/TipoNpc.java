package com.tcc.rpgmaster.model;

/**
 * Papel que um NPC desempenha na campanha, do ponto de vista do Mestre.
 */
public enum TipoNpc {
    ALIADO("Aliado"),
    NEUTRO("Neutro"),
    VILAO("Vilao"),
    MONSTRO("Monstro");

    private final String nomeExibicao;

    TipoNpc(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}
