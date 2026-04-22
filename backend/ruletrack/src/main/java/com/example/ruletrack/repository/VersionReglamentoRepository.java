package com.example.ruletrack.repository;

import com.example.ruletrack.entity.EstadoVersion;
import com.example.ruletrack.entity.VersionReglamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VersionReglamentoRepository extends JpaRepository<VersionReglamento, Long> {

    List<VersionReglamento> findByReglamentoIdOrderByNumeroVersionDesc(Long reglamentoId);

    Optional<VersionReglamento> findTopByReglamentoIdOrderByNumeroVersionDesc(Long reglamentoId);

    List<VersionReglamento> findByReglamentoIdAndEstado(Long reglamentoId, EstadoVersion estado);
}
