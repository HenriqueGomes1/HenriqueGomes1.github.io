package com.tcc.rpgmaster.controller;

import com.tcc.rpgmaster.model.Campanha;
import com.tcc.rpgmaster.service.CampanhaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller do lado ROXO do sistema: gestao das campanhas do Mestre.
 * A geracao de NPCs via IA fica em {@link NpcController}, acessada
 * a partir da tela de detalhe da campanha.
 */
@Controller
@RequestMapping("/campanhas")
public class CampanhaController {

    private final CampanhaService campanhaService;

    public CampanhaController(CampanhaService campanhaService) {
        this.campanhaService = campanhaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("campanhas", campanhaService.listarTodas());
        return "campanhas/lista";
    }

    @GetMapping("/nova")
    public String formularioNovo(Model model) {
        model.addAttribute("campanha", new Campanha());
        return "campanhas/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        Campanha campanha = campanhaService.buscarPorId(id);
        model.addAttribute("campanha", campanha);
        model.addAttribute("pedidoNpc", "");
        return "campanhas/detalhe";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("campanha") Campanha campanha, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "campanhas/form";
        }
        Campanha salva = campanhaService.salvar(campanha);
        return "redirect:/campanhas/" + salva.getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        campanhaService.excluir(id);
        return "redirect:/campanhas";
    }
}
