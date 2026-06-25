package com.tcc.rpgmaster.controller;

import com.tcc.rpgmaster.model.ClasseRPG;
import com.tcc.rpgmaster.model.Personagem;
import com.tcc.rpgmaster.repository.CampanhaRepository;
import com.tcc.rpgmaster.service.PersonagemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller do lado VERDE do sistema: gestao das fichas de personagem
 * dos jogadores, com geracao de descricao/habilidade via IA Generativa.
 */
@Controller
@RequestMapping("/personagens")
public class PersonagemController {

    private final PersonagemService personagemService;
    private final CampanhaRepository campanhaRepository;

    public PersonagemController(PersonagemService personagemService, CampanhaRepository campanhaRepository) {
        this.personagemService = personagemService;
        this.campanhaRepository = campanhaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("personagens", personagemService.listarTodos());
        return "personagens/lista";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("personagem", new Personagem());
        model.addAttribute("classes", ClasseRPG.values());
        model.addAttribute("campanhas", campanhaRepository.findAll());
        return "personagens/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("personagem", personagemService.buscarPorId(id));
        return "personagens/detalhe";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("personagem") Personagem personagem,
                          BindingResult bindingResult,
                          @RequestParam(value = "campanhaId", required = false) Long campanhaId,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("classes", ClasseRPG.values());
            model.addAttribute("campanhas", campanhaRepository.findAll());
            return "personagens/form";
        }
        Personagem salvo = personagemService.salvarComGeracaoDeIa(personagem, campanhaId);
        return "redirect:/personagens/" + salvo.getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        personagemService.excluir(id);
        return "redirect:/personagens";
    }
}
