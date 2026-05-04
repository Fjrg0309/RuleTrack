package com.example.ruletrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VersionReglamentoRequestDTO {

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    /** Etiqueta de versión opcional. Si no se envía, el servicio calcula la siguiente (+0.1). */
    private String versionEtiqueta;
}
