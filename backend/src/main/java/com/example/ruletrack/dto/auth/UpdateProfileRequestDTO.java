package com.example.ruletrack.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequestDTO {

    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    private String email;
}
