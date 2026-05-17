package com.example.ruletrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumenEstructuradoDTO {
    private String tituloReglamento;
    private List<ResumenSeccionDTO> secciones;
}
