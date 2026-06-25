package com.tcc.rpgmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicacao RPG Master.
 *
 * Sistema de apoio a mesas de RPG (regras D&D 5.5 / 2024) com dois modulos:
 *  - Lado do Jogador (verde): fichas de personagem com atributos e habilidades.
 *  - Lado do Mestre (roxo): campanhas e geracao de NPCs/monstros via IA Generativa.
 */
@SpringBootApplication
public class RpgMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpgMasterApplication.class, args);
    }
}
