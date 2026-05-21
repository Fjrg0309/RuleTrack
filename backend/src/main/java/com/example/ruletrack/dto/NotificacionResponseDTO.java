package com.example.ruletrack.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificacionResponseDTO {
    private Long id;
    private Long reglamentoId;
    private String reglamentoTitulo;
    private String emisorUsername;
    private String emisorNombre;
    private LocalDateTime createdAt;
}
