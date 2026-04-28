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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String creadoPorUsername;
    private int totalVersiones;
}
