package com.example.ruletrack.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String username;
    private String nombre;
    private String apellidos;
    private String email;
    private String rol;
    private String organizacionNombre;
    private LocalDate fechaNacimiento;
}

