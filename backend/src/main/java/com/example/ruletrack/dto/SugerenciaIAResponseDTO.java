package com.example.ruletrack.dto;

import com.example.ruletrack.entity.TipoSugerencia;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SugerenciaIAResponseDTO {

    private Long id;
    private Long versionReglamentoId;
    private TipoSugerencia tipo;
    private String contenido;
    private LocalDateTime fechaGeneracion;
    private Boolean aplicada;
}
