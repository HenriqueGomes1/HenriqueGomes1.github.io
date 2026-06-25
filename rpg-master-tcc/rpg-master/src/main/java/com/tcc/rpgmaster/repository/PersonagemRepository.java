package com.tcc.rpgmaster.repository;

import com.tcc.rpgmaster.model.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

    List<Personagem> findByCampanhaId(Long campanhaId);

    List<Personagem> findByNomeContainingIgnoreCase(String nome);
}
