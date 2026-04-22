package com.example.ruletrack.repository;

import com.example.ruletrack.entity.HistorialCambios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialCambiosRepository extends JpaRepository<HistorialCambios, Long> {

    List<HistorialCambios> findByVersionReglamentoIdOrderByFechaCambioDesc(Long versionId);

    List<HistorialCambios> findByUsuarioIdOrderByFechaCambioDesc(Long usuarioId);
}
