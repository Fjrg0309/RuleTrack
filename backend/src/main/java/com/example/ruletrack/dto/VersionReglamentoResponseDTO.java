package com.example.ruletrack.dto;

import com.example.ruletrack.entity.EstadoVersion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VersionReglamentoResponseDTO {

    private Long id;
    private Long reglamentoId;
    private String reglamentoTitulo;
    private Integer numeroVersion;
    private String contenido;
    private EstadoVersion estado;
    private LocalDateTime fechaCreacion;
    private String creadoPorUsername;
}
