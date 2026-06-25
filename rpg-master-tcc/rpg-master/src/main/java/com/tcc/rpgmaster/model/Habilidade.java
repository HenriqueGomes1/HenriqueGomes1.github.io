package com.tcc.rpgmaster.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Habilidade simplificada de um personagem, ligada a um unico atributo
 * (ex: "Bola de Fogo" usa Inteligencia e causa dano de fogo).
 * E gerada automaticamente pela IA com base na classe e nos atributos
 * do personagem, mas tambem pode ser criada/editada manualmente.
 */
@Entity
@Table(name = "habilidade")
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Lob
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Atributo atributoChave;

    /** Tipo de efeito da habilidade, ex: "fogo", "gelo", "cura", "psiquico". */
    private String tipoEfeito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personagem_id")
    private Personagem personagem;

    public Habilidade() {
    }

    public Habilidade(String nome, String descricao, Atributo atributoChave, String tipoEfeito) {
        this.nome = nome;
        this.descricao = descricao;
        this.atributoChave = atributoChave;
        this.tipoEfeito = tipoEfeito;
    }

    // ---- getters e setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Atributo getAtributoChave() {
        return atributoChave;
    }

    public void setAtributoChave(Atributo atributoChave) {
        this.atributoChave = atributoChave;
    }

    public String getTipoEfeito() {
        return tipoEfeito;
    }

    public void setTipoEfeito(String tipoEfeito) {
        this.tipoEfeito = tipoEfeito;
    }

    public Personagem getPersonagem() {
        return personagem;
    }

    public void setPersonagem(Personagem personagem) {
        this.personagem = personagem;
    }
}
