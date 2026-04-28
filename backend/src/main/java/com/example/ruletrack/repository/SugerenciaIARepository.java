package com.example.ruletrack.repository;

import com.example.ruletrack.entity.SugerenciaIA;
import com.example.ruletrack.entity.TipoSugerencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SugerenciaIARepository extends JpaRepository<SugerenciaIA, Long> {

    List<SugerenciaIA> findByVersionReglamentoId(Long versionId);

    List<SugerenciaIA> findByVersionReglamentoIdAndTipo(Long versionId, TipoSugerencia tipo);

    List<SugerenciaIA> findByVersionReglamentoIdAndAplicada(Long versionId, Boolean aplicada);
}
