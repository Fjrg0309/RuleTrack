package com.example.ruletrack.repository;

import com.example.ruletrack.entity.EstadoVersion;
import com.example.ruletrack.entity.VersionReglamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link com.example.ruletrack.entity.VersionReglamento}.
 * <p>
 * Proporciona consultas para recuperar versiones ordenadas, obtener
 * la última versión de un reglamento y filtrar por estado ({@link com.example.ruletrack.entity.EstadoVersion}).
 */
public interface VersionReglamentoRepository extends JpaRepository<VersionReglamento, Long> {

    List<VersionReglamento> findByReglamentoIdOrderByNumeroVersionDesc(Long reglamentoId);

    Optional<VersionReglamento> findTopByReglamentoIdOrderByNumeroVersionDesc(Long reglamentoId);

    List<VersionReglamento> findByReglamentoIdAndEstado(Long reglamentoId, EstadoVersion estado);

    List<VersionReglamento> findByReglamentoIdAndEstadoNot(Long reglamentoId, EstadoVersion estado);
}
