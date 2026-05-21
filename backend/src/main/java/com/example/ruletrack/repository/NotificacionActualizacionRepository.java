package com.example.ruletrack.repository;

import com.example.ruletrack.entity.NotificacionActualizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionActualizacionRepository extends JpaRepository<NotificacionActualizacion, Long> {

    List<NotificacionActualizacion> findByReceptorIdAndLeidaFalse(Long receptorId);

    boolean existsByReglamentoIdAndEmisorIdAndLeidaFalse(Long reglamentoId, Long emisorId);
}
