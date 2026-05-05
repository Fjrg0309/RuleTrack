package com.example.ruletrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacionInfoDTO {
    private String nombre;
    private int anioFundacion;
    private long numOrganizadores;
    private long numMiembros;
}
