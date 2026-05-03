package com.example.ruletrack.dto.auth;

import com.example.ruletrack.entity.Rol;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 120)
    private String apellidos;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "El DNI debe tener 8 números y 1 letra")
    private String dni;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    // Nombre de la organización (obligatorio para ambos tipos)
    @NotBlank(message = "El nombre de la organización es obligatorio")
    @Size(max = 200)
    private String organizacionNombre;

    // true = crear nueva organización, false = unirse a existente
    private boolean crearOrganizacion;
}

