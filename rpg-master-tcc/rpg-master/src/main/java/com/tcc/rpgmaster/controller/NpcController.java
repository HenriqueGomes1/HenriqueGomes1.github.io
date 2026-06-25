package com.tcc.rpgmaster.controller;

import com.tcc.rpgmaster.service.NpcService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller do lado ROXO responsavel pela geracao de NPCs/monstros via
 * IA Generativa, a partir de um pedido em linguagem natural do Mestre.
 */
@Controller
@RequestMapping("/campanhas/{campanhaId}/npcs")
public class NpcController {

    private final NpcService npcService;

    public NpcController(NpcService npcService) {
        this.npcService = npcService;
    }

    @PostMapping("/gerar")
    public String gerarComIa(@PathVariable Long campanhaId,
                              @RequestParam("pedidoMestre") String pedidoMestre) {
        npcService.gerarEAdicionarNaCampanha(campanhaId, pedidoMestre);
        return "redirect:/campanhas/" + campanhaId;
    }

    @PostMapping("/{npcId}/excluir")
    public String excluir(@PathVariable Long campanhaId, @PathVariable Long npcId) {
        npcService.excluir(npcId);
        return "redirect:/campanhas/" + campanhaId;
    }
}
