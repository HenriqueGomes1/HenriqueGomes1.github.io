package com.tcc.rpgmaster.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

/**
 * Classe abstrata que representa qualquer ser vivo do mundo de jogo.
 *
 * E a base da hierarquia de heranca do sistema: tanto {@link Personagem}
 * (controlado por um jogador) quanto {@link Npc} (controlado pelo Mestre)
 * sao Criaturas e compartilham os seis atributos de D&D 5.5 e a logica
 * de calculo de bonus de atributo.
 */
@MappedSuperclass
public abstract class Criatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @Column(nullable = false)
    private int forca = 10;

    @Column(nullable = false)
    private int destreza = 10;

    @Column(nullable = false)
    private int constituicao = 10;

    @Column(nullable = false)
    private int inteligencia = 10;

    @Column(nullable = false)
    private int sabedoria = 10;

    @Column(nullable = false)
    private int carisma = 10;

    @Lob
    private String descricao;

    protected Criatura() {
        // construtor padrao exigido pelo JPA
    }

    protected Criatura(String nome, int forca, int destreza, int constituicao,
                        int inteligencia, int sabedoria, int carisma) {
        this.nome = nome;
        this.forca = forca;
        this.destreza = destreza;
        this.constituicao = constituicao;
        this.inteligencia = inteligencia;
        this.sabedoria = sabedoria;
        this.carisma = carisma;
    }

    /**
     * Calcula o bonus (modificador) de um valor de atributo segundo a regra
     * oficial de D&D 5.5: bonus = piso((valor - 10) / 2).
     */
    public static int calcularBonus(int valorAtributo) {
        return (int) Math.floor((valorAtributo - 10) / 2.0);
    }

    public int getBonusForca() {
        return calcularBonus(forca);
    }

    public int getBonusDestreza() {
        return calcularBonus(destreza);
    }

    public int getBonusConstituicao() {
        return calcularBonus(constituicao);
    }

    public int getBonusInteligencia() {
        return calcularBonus(inteligencia);
    }

    public int getBonusSabedoria() {
        return calcularBonus(sabedoria);
    }

    public int getBonusCarisma() {
        return calcularBonus(carisma);
    }

    /**
     * Retorna o valor numerico do atributo informado. Util para a
     * integracao com a IA, que sugere habilidades ligadas a um atributo.
     */
    public int getValorAtributo(Atributo atributo) {
        return switch (atributo) {
            case FORCA -> forca;
            case DESTREZA -> destreza;
            case CONSTITUICAO -> constituicao;
            case INTELIGENCIA -> inteligencia;
            case SABEDORIA -> sabedoria;
            case CARISMA -> carisma;
        };
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

    public int getForca() {
        return forca;
    }

    public void setForca(int forca) {
        this.forca = forca;
    }

    public int getDestreza() {
        return destreza;
    }

    public void setDestreza(int destreza) {
        this.destreza = destreza;
    }

    public int getConstituicao() {
        return constituicao;
    }

    public void setConstituicao(int constituicao) {
        this.constituicao = constituicao;
    }

    public int getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(int inteligencia) {
        this.inteligencia = inteligencia;
    }

    public int getSabedoria() {
        return sabedoria;
    }

    public void setSabedoria(int sabedoria) {
        this.sabedoria = sabedoria;
    }

    public int getCarisma() {
        return carisma;
    }

    public void setCarisma(int carisma) {
        this.carisma = carisma;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
