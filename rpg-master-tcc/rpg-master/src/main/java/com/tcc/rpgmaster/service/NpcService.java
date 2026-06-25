package com.tcc.rpgmaster.service;

import com.tcc.rpgmaster.model.Campanha;
import com.tcc.rpgmaster.model.Npc;
import com.tcc.rpgmaster.model.TipoNpc;
import com.tcc.rpgmaster.repository.CampanhaRepository;
import com.tcc.rpgmaster.repository.NpcRepository;
import com.tcc.rpgmaster.service.dto.NpcGeradoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NpcService {

    private final NpcRepository npcRepository;
    private final CampanhaRepository campanhaRepository;
    private final MistralAiService mistralAiService;

    public NpcService(NpcRepository npcRepository,
                       CampanhaRepository campanhaRepository,
                       MistralAiService mistralAiService) {
        this.npcRepository = npcRepository;
        this.campanhaRepository = campanhaRepository;
        this.mistralAiService = mistralAiService;
    }

    public List<Npc> listarTodos() {
        return npcRepository.findAll();
    }

    public Npc buscarPorId(Long id) {
        return npcRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NPC nao encontrado: " + id));
    }

    /**
     * Gera um NPC/monstro completo via IA Generativa a partir de um pedido
     * em linguagem natural do Mestre, ja vinculado a campanha informada,
     * e o persiste no banco.
     */
    public Npc gerarEAdicionarNaCampanha(Long campanhaId, String pedidoMestre) {
        Campanha campanha = campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada: " + campanhaId));

        NpcGeradoDTO gerado = mistralAiService.gerarNpc(pedidoMestre, campanha.getDescricao());

        Npc npc = new Npc();
        npc.setNome(gerado.getNome());
        npc.setDescricao(gerado.getDescricao());
        npc.setTipo(parseTipo(gerado.getTipo()));
        npc.setNivelDesafio(gerado.getNivelDesafio());
        npc.setForca(gerado.getForca());
        npc.setDestreza(gerado.getDestreza());
        npc.setConstituicao(gerado.getConstituicao());
        npc.setInteligencia(gerado.getInteligencia());
        npc.setSabedoria(gerado.getSabedoria());
        npc.setCarisma(gerado.getCarisma());
        npc.setPontosVida(gerado.getPontosVida());
        npc.setGeradoPorIa(true);

        campanha.adicionarNpc(npc);
        return npcRepository.save(npc);
    }

    public Npc salvarManual(Npc npc, Long campanhaId) {
        Campanha campanha = campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada: " + campanhaId));
        campanha.adicionarNpc(npc);
        return npcRepository.save(npc);
    }

    public void excluir(Long id) {
        npcRepository.deleteById(id);
    }

    private TipoNpc parseTipo(String valor) {
        if (valor == null) {
            return TipoNpc.NEUTRO;
        }
        try {
            return TipoNpc.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TipoNpc.NEUTRO;
        }
    }
}
