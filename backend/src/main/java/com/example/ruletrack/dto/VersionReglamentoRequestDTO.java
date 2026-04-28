package com.example.ruletrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VersionReglamentoRequestDTO {

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;
}
