package com.tcc.rpgmaster.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Campanha conduzida pelo Mestre. Agrupa os personagens dos jogadores
 * e os NPCs/monstros criados (manualmente ou via IA) para a aventura.
 */
@Entity
@Table(name = "campanha")
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    @NotBlank(message = "O nome da campanha e obrigatorio")
    private String nome;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "O nome do mestre e obrigatorio")
    private String mestre;

    @Lob
    private String descricao;

    @Column(nullable = false)
    private LocalDate dataCriacao = LocalDate.now();

    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Personagem> personagens = new ArrayList<>();

    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Npc> npcs = new ArrayList<>();

    public Campanha() {
    }

    public Campanha(String nome, String mestre, String descricao) {
        this.nome = nome;
        this.mestre = mestre;
        this.descricao = descricao;
    }

    public void adicionarPersonagem(Personagem personagem) {
        personagens.add(personagem);
        personagem.setCampanha(this);
    }

    public void adicionarNpc(Npc npc) {
        npcs.add(npc);
        npc.setCampanha(this);
    }

    public int getTotalPersonagens() {
        return personagens.size();
    }

    public int getTotalNpcs() {
        return npcs.size();
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

    public String getMestre() {
        return mestre;
    }

    public void setMestre(String mestre) {
        this.mestre = mestre;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Personagem> getPersonagens() {
        return personagens;
    }

    public void setPersonagens(List<Personagem> personagens) {
        this.personagens = personagens;
    }

    public List<Npc> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<Npc> npcs) {
        this.npcs = npcs;
    }
}
