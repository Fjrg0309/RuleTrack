package com.example.ruletrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionPublicaDTO {
    private Long id;
    private Integer numeroVersion;
    private String versionEtiqueta;
    private String contenido;
    private LocalDateTime fechaCreacion;
}
