package com.example.ruletrack.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistorialCambiosResponseDTO {

    private Long id;
    private Long versionReglamentoId;
    private String usuarioUsername;
    private String descripcionCambio;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
    private LocalDateTime fechaCambio;
}
