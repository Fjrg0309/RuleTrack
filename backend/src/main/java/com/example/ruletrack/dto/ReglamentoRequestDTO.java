package com.example.ruletrack.dto;

import com.example.ruletrack.entity.VisibilidadReglamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ReglamentoRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String titulo;

    private String descripcion;

    private VisibilidadReglamento visibilidad = VisibilidadReglamento.PUBLICO;

    // IDs de usuarios permitidos, solo relevante si visibilidad == PRIVADO
    private List<Long> usuariosPermitidosIds;
}

