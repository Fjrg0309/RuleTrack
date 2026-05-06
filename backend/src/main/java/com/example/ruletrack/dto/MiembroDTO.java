package com.example.ruletrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiembroDTO {
    private String username;
    private String nombre;
    private String apellidos;
    private String rol;
}
