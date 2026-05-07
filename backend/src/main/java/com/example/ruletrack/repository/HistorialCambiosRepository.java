package com.example.ruletrack.repository;

import com.example.ruletrack.entity.HistorialCambios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link com.example.ruletrack.entity.HistorialCambios}.
 * <p>
 * Proporciona consultas para recuperar el historial de auditoría ordenado cronológicamente,
 * tanto por versión de reglamento como por usuario que realizó el cambio.
 */
public interface HistorialCambiosRepository extends JpaRepository<HistorialCambios, Long> {

    List<HistorialCambios> findByVersionReglamentoIdOrderByFechaCambioDesc(Long versionId);

    List<HistorialCambios> findByUsuarioIdOrderByFechaCambioDesc(Long usuarioId);
}
