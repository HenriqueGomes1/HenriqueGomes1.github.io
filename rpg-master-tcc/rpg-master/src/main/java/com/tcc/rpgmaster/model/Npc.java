package com.tcc.rpgmaster.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * NPC ou monstro controlado pelo Mestre. Herda os atributos e bonus de
 * {@link Criatura} e adiciona informacoes uteis para conducao da mesa:
 * tipo (aliado/vilao/monstro), nivel de desafio e pontos de vida.
 *
 * Pode ser criado manualmente pelo Mestre ou gerado pela IA a partir
 * de um pedido em linguagem natural (ex: "um goblin batedor sorrateiro").
 */
@Entity
@Table(name = "npc")
public class Npc extends Criatura {

    @Enumerated(EnumType.STRING)
    private TipoNpc tipo;

    /** Nivel de desafio (CR), ex: "1/4", "2", "5". */
    private String nivelDesafio;

    private int pontosVida = 10;

    /** Indica se este registro foi originado por geracao via IA. */
    private boolean geradoPorIa = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campanha_id")
    private Campanha campanha;

    public Npc() {
        super();
    }

    public Npc(String nome, TipoNpc tipo, int forca, int destreza, int constituicao,
               int inteligencia, int sabedoria, int carisma) {
        super(nome, forca, destreza, constituicao, inteligencia, sabedoria, carisma);
        this.tipo = tipo;
    }

    // ---- getters e setters ----

    public TipoNpc getTipo() {
        return tipo;
    }

    public void setTipo(TipoNpc tipo) {
        this.tipo = tipo;
    }

    public String getNivelDesafio() {
        return nivelDesafio;
    }

    public void setNivelDesafio(String nivelDesafio) {
        this.nivelDesafio = nivelDesafio;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public void setPontosVida(int pontosVida) {
        this.pontosVida = pontosVida;
    }

    public boolean isGeradoPorIa() {
        return geradoPorIa;
    }

    public void setGeradoPorIa(boolean geradoPorIa) {
        this.geradoPorIa = geradoPorIa;
    }

    public Campanha getCampanha() {
        return campanha;
    }

    public void setCampanha(Campanha campanha) {
        this.campanha = campanha;
    }
}
