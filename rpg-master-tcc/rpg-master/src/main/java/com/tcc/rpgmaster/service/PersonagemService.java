package com.tcc.rpgmaster.service;

import com.tcc.rpgmaster.model.Atributo;
import com.tcc.rpgmaster.model.Campanha;
import com.tcc.rpgmaster.model.Habilidade;
import com.tcc.rpgmaster.model.Personagem;
import com.tcc.rpgmaster.repository.CampanhaRepository;
import com.tcc.rpgmaster.repository.PersonagemRepository;
import com.tcc.rpgmaster.service.dto.PersonagemGeradoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonagemService {

    private final PersonagemRepository personagemRepository;
    private final CampanhaRepository campanhaRepository;
    private final MistralAiService mistralAiService;

    public PersonagemService(PersonagemRepository personagemRepository,
                              CampanhaRepository campanhaRepository,
                              MistralAiService mistralAiService) {
        this.personagemRepository = personagemRepository;
        this.campanhaRepository = campanhaRepository;
        this.mistralAiService = mistralAiService;
    }

    public List<Personagem> listarTodos() {
        return personagemRepository.findAll();
    }

    public Personagem buscarPorId(Long id) {
        return personagemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Personagem nao encontrado: " + id));
    }

    /**
     * Salva o personagem e, em seguida, utiliza a IA Generativa para criar
     * automaticamente a descricao e a primeira habilidade simplificada,
     * baseadas na classe e nos atributos definidos no formulario.
     */
    public Personagem salvarComGeracaoDeIa(Personagem personagem, Long campanhaId) {
        if (campanhaId != null) {
            Campanha campanha = campanhaRepository.findById(campanhaId)
                    .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada: " + campanhaId));
            personagem.setCampanha(campanha);
        }

        PersonagemGeradoDTO gerado = mistralAiService.gerarDescricaoEHabilidade(personagem);
        personagem.setDescricao(gerado.getDescricao());

        Personagem salvo = personagemRepository.save(personagem);

        Atributo atributoChave = parseAtributo(gerado.getAtributoChave());
        Habilidade habilidade = new Habilidade(
                gerado.getHabilidadeNome(),
                gerado.getHabilidadeDescricao(),
                atributoChave,
                gerado.getTipoEfeito()
        );
        salvo.adicionarHabilidade(habilidade);

        return personagemRepository.save(salvo);
    }

    public void excluir(Long id) {
        personagemRepository.deleteById(id);
    }

    private Atributo parseAtributo(String valor) {
        if (valor == null) {
            return Atributo.FORCA;
        }
        try {
            return Atributo.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Atributo.FORCA;
        }
    }
}
