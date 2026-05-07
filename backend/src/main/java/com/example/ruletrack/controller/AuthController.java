package com.example.ruletrack.controller;

import com.example.ruletrack.dto.MiembroDTO;
import com.example.ruletrack.dto.OrganizacionInfoDTO;
import com.example.ruletrack.dto.auth.AuthResponseDTO;
import com.example.ruletrack.dto.auth.LoginRequestDTO;
import com.example.ruletrack.dto.auth.RegisterRequestDTO;
import com.example.ruletrack.dto.auth.UpdateProfileRequestDTO;
import com.example.ruletrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación y gestión del perfil de usuario.
 * Expone endpoints públicos de registro/login y endpoints autenticados de perfil.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y gestión de perfil")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea un usuario y devuelve un token JWT. Permite crear o unirse a una organización.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario/email/DNI ya existente")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT válido 24 h.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticación correcta"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Comprobar si existe una organización")
    @ApiResponse(responseCode = "200", description = "Resultado de la búsqueda")
    @GetMapping("/organizacion/existe")
    public ResponseEntity<Map<String, Boolean>> checkOrganizacion(@RequestParam String nombre) {
        return ResponseEntity.ok(Map.of("existe", authService.existeOrganizacion(nombre)));
    }

    @Operation(summary = "Obtener información pública de una organización")
    @ApiResponse(responseCode = "200", description = "Datos de la organización")
    @GetMapping("/organizacion/info")
    public ResponseEntity<OrganizacionInfoDTO> getOrganizacionInfo(@RequestParam String nombre) {
        return ResponseEntity.ok(authService.getOrganizacionInfo(nombre));
    }

    @Operation(summary = "Listar miembros de la organización del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de miembros"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping("/organizacion/miembros")
    public ResponseEntity<java.util.List<MiembroDTO>> getMiembros(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getMiembros(userDetails.getUsername()));
    }

    @Operation(summary = "Actualizar perfil del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil actualizado y nuevo token devuelto"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @PutMapping("/me")
    public ResponseEntity<AuthResponseDTO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        return ResponseEntity.ok(authService.updateProfile(userDetails.getUsername(), request));
    }

    @Operation(summary = "Obtener datos del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos del usuario actual"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getMe(userDetails.getUsername()));
    }
}

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/organizacion/existe")
    public ResponseEntity<Map<String, Boolean>> checkOrganizacion(@RequestParam String nombre) {
        return ResponseEntity.ok(Map.of("existe", authService.existeOrganizacion(nombre)));
    }

    @GetMapping("/organizacion/info")
    public ResponseEntity<OrganizacionInfoDTO> getOrganizacionInfo(@RequestParam String nombre) {
        return ResponseEntity.ok(authService.getOrganizacionInfo(nombre));
    }

    @GetMapping("/organizacion/miembros")
    public ResponseEntity<java.util.List<MiembroDTO>> getMiembros(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getMiembros(userDetails.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponseDTO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        return ResponseEntity.ok(authService.updateProfile(userDetails.getUsername(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getMe(userDetails.getUsername()));
    }
}
