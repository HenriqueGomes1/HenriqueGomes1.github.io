package com.tcc.rpgmaster.service;

import com.tcc.rpgmaster.model.Campanha;
import com.tcc.rpgmaster.repository.CampanhaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampanhaService {

    private final CampanhaRepository campanhaRepository;

    public CampanhaService(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = campanhaRepository;
    }

    public List<Campanha> listarTodas() {
        return campanhaRepository.findAll();
    }

    public Campanha buscarPorId(Long id) {
        return campanhaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada: " + id));
    }

    public Campanha salvar(Campanha campanha) {
        return campanhaRepository.save(campanha);
    }

    public void excluir(Long id) {
        campanhaRepository.deleteById(id);
    }
}
