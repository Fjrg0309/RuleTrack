package com.example.ruletrack.repository;

import com.example.ruletrack.entity.Reglamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglamentoRepository extends JpaRepository<Reglamento, Long> {

    List<Reglamento> findByCreadoPorId(Long usuarioId);

    List<Reglamento> findByTituloContainingIgnoreCase(String titulo);
}
