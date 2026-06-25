package com.tcc.rpgmaster.model;

/**
 * Os seis atributos base de D&D 5.5, usados, entre outras coisas,
 * para indicar qual atributo "potencializa" uma habilidade
 * (ex: uma habilidade de bola de fogo usa Inteligencia).
 */
public enum Atributo {
    FORCA("Forca"),
    DESTREZA("Destreza"),
    CONSTITUICAO("Constituicao"),
    INTELIGENCIA("Inteligencia"),
    SABEDORIA("Sabedoria"),
    CARISMA("Carisma");

    private final String nomeExibicao;

    Atributo(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}
