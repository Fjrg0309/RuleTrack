package com.example.ruletrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReglamentoRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String titulo;

    private String descripcion;
}
