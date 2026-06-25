package com.tcc.rpgmaster.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.rpgmaster.model.ClasseRPG;
import com.tcc.rpgmaster.model.Personagem;
import com.tcc.rpgmaster.service.dto.MistralChatRequest;
import com.tcc.rpgmaster.service.dto.MistralChatResponse;
import com.tcc.rpgmaster.service.dto.MistralMessage;
import com.tcc.rpgmaster.service.dto.NpcGeradoDTO;
import com.tcc.rpgmaster.service.dto.PersonagemGeradoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Servico responsavel pela integracao com a IA Generativa (Mistral AI).
 *
 * Centraliza toda a comunicacao HTTP com a API de chat completions da
 * Mistral e a conversao da resposta (texto em JSON) para os DTOs do
 * sistema. E usado em dois pontos do dominio:
 *
 *  1. Geracao de descricao + habilidade simplificada ao criar um Personagem.
 *  2. Geracao de NPCs/monstros a partir de um pedido do Mestre em linguagem natural.
 */
@Service
public class MistralAiService {

    private static final Logger log = LoggerFactory.getLogger(MistralAiService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${mistral.api.key:}")
    private String apiKey;

    @Value("${mistral.api.url:https://api.mistral.ai/v1/chat/completions}")
    private String apiUrl;

    @Value("${mistral.api.model:mistral-small-latest}")
    private String modelo;

    public MistralAiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Gera, via IA, uma descricao narrativa e uma habilidade simplificada
     * para o personagem informado, com base na classe e nos atributos dele.
     * Em caso de falha na chamada a IA (sem chave configurada, sem internet,
     * etc.), retorna um resultado padrao para nao quebrar o fluxo do usuario.
     */
    public PersonagemGeradoDTO gerarDescricaoEHabilidade(Personagem personagem) {
        String prompt = montarPromptPersonagem(personagem);
        try {
            String respostaTexto = chamarMistral(promptSistemaPersonagem(), prompt);
            return objectMapper.readValue(limparJson(respostaTexto), PersonagemGeradoDTO.class);
        } catch (Exception e) {
            log.warn("Falha ao gerar conteudo via IA para o personagem '{}': {}", personagem.getNome(), e.getMessage());
            return gerarFallbackPersonagem(personagem);
        }
    }

    /**
     * Gera, via IA, um NPC/monstro completo (atributos, pontos de vida,
     * nivel de desafio e descricao) a partir de um pedido livre do Mestre,
     * ex: "um goblin batedor sorrateiro que ataca pelas sombras".
     */
    public NpcGeradoDTO gerarNpc(String pedidoMestre, String contextoCampanha) {
        String prompt = montarPromptNpc(pedidoMestre, contextoCampanha);
        try {
            String respostaTexto = chamarMistral(promptSistemaNpc(), prompt);
            return objectMapper.readValue(limparJson(respostaTexto), NpcGeradoDTO.class);
        } catch (Exception e) {
            log.warn("Falha ao gerar NPC via IA para o pedido '{}': {}", pedidoMestre, e.getMessage());
            return gerarFallbackNpc(pedidoMestre);
        }
    }

    // ---------------------------------------------------------------
    // Comunicacao HTTP com a API da Mistral
    // ---------------------------------------------------------------

    private String chamarMistral(String mensagemSistema, String mensagemUsuario) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("MISTRAL_API_KEY nao configurada");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        MistralChatRequest body = new MistralChatRequest(
                modelo,
                List.of(
                        new MistralMessage("system", mensagemSistema),
                        new MistralMessage("user", mensagemUsuario)
                ),
                0.85
        );

        HttpEntity<MistralChatRequest> requisicao = new HttpEntity<>(body, headers);
        MistralChatResponse resposta = restTemplate.exchange(
                apiUrl, HttpMethod.POST, requisicao, MistralChatResponse.class).getBody();

        if (resposta == null || resposta.getPrimeiraRespostaTexto() == null) {
            throw new IllegalStateException("Resposta vazia da API da Mistral");
        }
        return resposta.getPrimeiraRespostaTexto();
    }

    /** Remove blocos de markdown (```json ... ```) que o modelo as vezes adiciona. */
    private String limparJson(String texto) {
        String limpo = texto.trim();
        if (limpo.startsWith("```")) {
            limpo = limpo.replaceFirst("^```[a-zA-Z]*", "");
            limpo = limpo.replaceFirst("```$", "");
        }
        return limpo.trim();
    }

    // ---------------------------------------------------------------
    // Prompts
    // ---------------------------------------------------------------

    private String promptSistemaPersonagem() {
        return "Voce e um assistente de criacao de personagens para mesas de RPG (regras D&D 5.5 / 2024). "
                + "Responda SEMPRE e SOMENTE com um JSON valido, sem nenhum texto antes ou depois, no formato: "
                + "{\"descricao\": \"...\", \"habilidadeNome\": \"...\", \"habilidadeDescricao\": \"...\", "
                + "\"atributoChave\": \"FORCA|DESTREZA|CONSTITUICAO|INTELIGENCIA|SABEDORIA|CARISMA\", "
                + "\"tipoEfeito\": \"fogo|gelo|cura|raio|psiquico|fisico|veneno|sagrado|sombrio\"}. "
                + "A descricao deve ter no maximo 3 frases. A habilidade deve ser simples e tematica para a classe.";
    }

    private String montarPromptPersonagem(Personagem personagem) {
        return String.format(
                "Crie uma descricao narrativa breve e uma habilidade simplificada para este personagem: "
                        + "Nome: %s. Classe: %s. Nivel: %d. "
                        + "Forca: %d, Destreza: %d, Constituicao: %d, Inteligencia: %d, Sabedoria: %d, Carisma: %d.",
                personagem.getNome(),
                personagem.getClasse() != null ? personagem.getClasse().getNomeExibicao() : "Aventureiro",
                personagem.getNivel(),
                personagem.getForca(), personagem.getDestreza(), personagem.getConstituicao(),
                personagem.getInteligencia(), personagem.getSabedoria(), personagem.getCarisma()
        );
    }

    private String promptSistemaNpc() {
        return "Voce e um assistente de Mestre de RPG (regras D&D 5.5 / 2024) que cria NPCs e monstros. "
                + "Responda SEMPRE e SOMENTE com um JSON valido, sem nenhum texto antes ou depois, no formato: "
                + "{\"nome\": \"...\", \"descricao\": \"...\", \"tipo\": \"ALIADO|NEUTRO|VILAO|MONSTRO\", "
                + "\"nivelDesafio\": \"1/4|1/2|1|2|3|...\", \"forca\": 10, \"destreza\": 10, \"constituicao\": 10, "
                + "\"inteligencia\": 10, \"sabedoria\": 10, \"carisma\": 10, \"pontosVida\": 10}. "
                + "Os atributos devem ser numeros entre 1 e 20, coerentes com o tipo de criatura pedido. "
                + "A descricao deve ter no maximo 3 frases.";
    }

    private String montarPromptNpc(String pedidoMestre, String contextoCampanha) {
        String contexto = (contextoCampanha == null || contextoCampanha.isBlank())
                ? "" : " Contexto da campanha: " + contextoCampanha + ".";
        return "Crie um NPC ou monstro a partir deste pedido do Mestre: \"" + pedidoMestre + "\"." + contexto;
    }

    // ---------------------------------------------------------------
    // Fallbacks (usados se a chamada a IA falhar)
    // ---------------------------------------------------------------

    private PersonagemGeradoDTO gerarFallbackPersonagem(Personagem personagem) {
        PersonagemGeradoDTO dto = new PersonagemGeradoDTO();
        ClasseRPG classe = personagem.getClasse();
        dto.setDescricao("Um(a) " + (classe != null ? classe.getNomeExibicao().toLowerCase() : "aventureiro(a)")
                + " de nome " + personagem.getNome() + ", forjado(a) em batalhas e pronto(a) para a proxima aventura.");
        dto.setHabilidadeNome("Investida Improvisada");
        dto.setHabilidadeDescricao("Um golpe direto e instintivo contra o inimigo mais proximo.");
        dto.setAtributoChave("FORCA");
        dto.setTipoEfeito("fisico");
        return dto;
    }

    private NpcGeradoDTO gerarFallbackNpc(String pedidoMestre) {
        NpcGeradoDTO dto = new NpcGeradoDTO();
        dto.setNome("Criatura Misteriosa");
        dto.setDescricao("Uma criatura surgida das sombras da campanha, ainda envolta em misterio "
                + "(baseada no pedido: \"" + pedidoMestre + "\").");
        dto.setTipo("NEUTRO");
        dto.setNivelDesafio("1");
        dto.setForca(10);
        dto.setDestreza(10);
        dto.setConstituicao(10);
        dto.setInteligencia(10);
        dto.setSabedoria(10);
        dto.setCarisma(10);
        dto.setPontosVida(10);
        return dto;
    }
}
