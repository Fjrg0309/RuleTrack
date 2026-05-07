package com.example.ruletrack.repository;

import com.example.ruletrack.entity.SugerenciaIA;
import com.example.ruletrack.entity.TipoSugerencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link com.example.ruletrack.entity.SugerenciaIA}.
 * <p>
 * Permite recuperar sugerencias filtradas por versión de reglamento,
 * tipo de sugerencia ({@link com.example.ruletrack.entity.TipoSugerencia})
 * y estado de aplicación ({@code aplicada}).
 */
public interface SugerenciaIARepository extends JpaRepository<SugerenciaIA, Long> {

    List<SugerenciaIA> findByVersionReglamentoId(Long versionId);

    List<SugerenciaIA> findByVersionReglamentoIdAndTipo(Long versionId, TipoSugerencia tipo);

    List<SugerenciaIA> findByVersionReglamentoIdAndAplicada(Long versionId, Boolean aplicada);
}
