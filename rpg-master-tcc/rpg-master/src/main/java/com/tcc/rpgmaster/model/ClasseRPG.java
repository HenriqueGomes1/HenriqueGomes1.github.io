package com.tcc.rpgmaster.model;

/**
 * Classes de personagem disponiveis, baseadas nas regras de D&D 5.5 (2024).
 */
public enum ClasseRPG {
    GUERREIRO("Guerreiro"),
    MAGO("Mago"),
    LADINO("Ladino"),
    CLERIGO("Clerigo"),
    BARBARO("Barbaro"),
    BARDO("Bardo"),
    PALADINO("Paladino"),
    PATRULHEIRO("Patrulheiro"),
    FEITICEIRO("Feiticeiro"),
    BRUXO("Bruxo"),
    MONGE("Monge"),
    DRUIDA("Druida");

    private final String nomeExibicao;

    ClasseRPG(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}
