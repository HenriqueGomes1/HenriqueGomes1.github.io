package com.tcc.rpgmaster.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Personagem de jogador (PC). Herda os atributos e bonus de {@link Criatura}
 * e adiciona o que e especifico de quem e controlado por um jogador:
 * classe, nivel, a campanha a qual pertence e suas habilidades.
 */
@Entity
@Table(name = "personagem")
public class Personagem extends Criatura {

    @Enumerated(EnumType.STRING)
    private ClasseRPG classe;

    private int nivel = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campanha_id")
    private Campanha campanha;

    @OneToMany(mappedBy = "personagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habilidade> habilidades = new ArrayList<>();

    public Personagem() {
        super();
    }

    public Personagem(String nome, ClasseRPG classe, int forca, int destreza, int constituicao,
                       int inteligencia, int sabedoria, int carisma) {
        super(nome, forca, destreza, constituicao, inteligencia, sabedoria, carisma);
        this.classe = classe;
    }

    public void adicionarHabilidade(Habilidade habilidade) {
        habilidades.add(habilidade);
        habilidade.setPersonagem(this);
    }

    // ---- getters e setters ----

    public ClasseRPG getClasse() {
        return classe;
    }

    public void setClasse(ClasseRPG classe) {
        this.classe = classe;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public Campanha getCampanha() {
        return campanha;
    }

    public void setCampanha(Campanha campanha) {
        this.campanha = campanha;
    }

    public List<Habilidade> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<Habilidade> habilidades) {
        this.habilidades = habilidades;
    }
}
