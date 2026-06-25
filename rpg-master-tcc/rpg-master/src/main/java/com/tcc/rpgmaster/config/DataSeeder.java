package com.tcc.rpgmaster.config;

import com.tcc.rpgmaster.model.Atributo;
import com.tcc.rpgmaster.model.Campanha;
import com.tcc.rpgmaster.model.ClasseRPG;
import com.tcc.rpgmaster.model.Habilidade;
import com.tcc.rpgmaster.model.Npc;
import com.tcc.rpgmaster.model.Personagem;
import com.tcc.rpgmaster.model.TipoNpc;
import com.tcc.rpgmaster.repository.CampanhaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Popula o banco H2 em memoria com uma campanha de exemplo assim que a
 * aplicacao sobe, para que a demonstracao do TCC nunca comece com as
 * telas vazias. Roda apenas se ainda nao houver nenhuma campanha
 * cadastrada (evita duplicar dados em reinicializacoes futuras).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CampanhaRepository campanhaRepository;

    public DataSeeder(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = campanhaRepository;
    }

    @Override
    public void run(String... args) {
        if (campanhaRepository.count() > 0) {
            return;
        }

        Campanha campanha = new Campanha(
                "A Maldicao de Vaelthorn",
                "Mestre Igor",
                "Um reino antigo sucumbe lentamente a uma maldicao que desperta " +
                        "criaturas das sombras. Um grupo de aventureiros e convocado " +
                        "pelo conselho real para investigar a fonte do mal antes que " +
                        "a capital seja engolida pela escuridao."
        );

        Personagem guerreiro = new Personagem("Thalrik Punho-de-Ferro", ClasseRPG.GUERREIRO,
                17, 13, 16, 9, 11, 10);
        guerreiro.setNivel(3);
        guerreiro.setDescricao(
                "Um ex-soldado da guarda real, de poucas palavras e lealdade inabalavel. " +
                        "Carrega consigo as cicatrizes de batalhas que preferiria esquecer."
        );
        guerreiro.adicionarHabilidade(new Habilidade(
                "Golpe Sismico",
                "Thalrik crava sua arma no chao com tanta forca que provoca um " +
                        "tremor localizado, derrubando inimigos proximos.",
                Atributo.FORCA,
                "fisico"
        ));

        Personagem maga = new Personagem("Lyriel Vento-Cinza", ClasseRPG.MAGO,
                8, 14, 12, 18, 13, 11);
        maga.setNivel(3);
        maga.setDescricao(
                "Formada na Academia Arcana de Thessar, Lyriel troca seguranca por " +
                        "conhecimento proibido. Sua curiosidade e tao perigosa quanto seus feiticos."
        );
        maga.adicionarHabilidade(new Habilidade(
                "Lanca Glacial",
                "Lyriel condensa a umidade do ar em uma lanca de gelo afiada que " +
                        "perfura e congela o alvo no impacto.",
                Atributo.INTELIGENCIA,
                "gelo"
        ));

        Personagem ladina = new Personagem("Sable Tres-Facas", ClasseRPG.LADINO,
                11, 17, 13, 12, 14, 15);
        ladina.setNivel(3);
        ladina.setDescricao(
                "Cresceu nos becos do distrito portuario e nunca confiou em ninguem " +
                        "alem de si mesma — ate se juntar a este grupo."
        );
        ladina.adicionarHabilidade(new Habilidade(
                "Passo nas Sombras",
                "Sable se funde por um instante com a escuridao ao redor, " +
                        "deslizando despercebida ate as costas do inimigo.",
                Atributo.DESTREZA,
                "sombrio"
        ));

        campanha.adicionarPersonagem(guerreiro);
        campanha.adicionarPersonagem(maga);
        campanha.adicionarPersonagem(ladina);

        Npc cultista = new Npc("Sumo Cultista Mordrek", TipoNpc.VILAO,
                10, 12, 13, 16, 15, 17);
        cultista.setNivelDesafio("4");
        cultista.setPontosVida(58);
        cultista.setDescricao(
                "Lider do culto que invoca a maldicao sobre Vaelthorn. Fala em " +
                        "enigmas e parece sempre um passo a frente dos heroes."
        );

        Npc goblin = new Npc("Batedor Goblin Trit", TipoNpc.MONSTRO,
                8, 15, 10, 8, 8, 6);
        goblin.setNivelDesafio("1/4");
        goblin.setPontosVida(7);
        goblin.setDescricao(
                "Pequeno, rapido e covarde — prefere emboscadas a combate direto, " +
                        "fugindo ao primeiro sinal de desvantagem."
        );

        Npc ferreiro = new Npc("Borin Mao-Pesada", TipoNpc.ALIADO,
                15, 9, 14, 10, 12, 11);
        ferreiro.setNivelDesafio("nao aplicavel");
        ferreiro.setPontosVida(20);
        ferreiro.setDescricao(
                "Ferreiro da vila de Hollow Creek, oferece reparos e equipamentos " +
                        "aos aventureiros em troca de noticias da estrada."
        );

        campanha.adicionarNpc(cultista);
        campanha.adicionarNpc(goblin);
        campanha.adicionarNpc(ferreiro);

        campanhaRepository.save(campanha);
    }
}
