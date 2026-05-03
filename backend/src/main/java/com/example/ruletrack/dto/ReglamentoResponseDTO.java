package com.example.ruletrack.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReglamentoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String visibilidad;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String creadoPorUsername;
    private String creadoPorNombre;
    private String organizacionNombre;
    private int totalVersiones;
    private String ultimaVersion;
}

