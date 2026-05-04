package com.example.ruletrack.service;

import com.example.ruletrack.dto.auth.AuthResponseDTO;
import com.example.ruletrack.dto.auth.LoginRequestDTO;
import com.example.ruletrack.dto.auth.RegisterRequestDTO;
import com.example.ruletrack.entity.Rol;
import com.example.ruletrack.entity.Usuario;
import com.example.ruletrack.repository.UsuarioRepository;
import com.example.ruletrack.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado");
        }

        if (request.getRol() == Rol.ORGANIZADOR) {
            LocalDate hace18 = LocalDate.now().minusYears(18);
            if (request.getFechaNacimiento().isAfter(hace18)) {
                throw new IllegalArgumentException("Los organizadores deben ser mayores de 18 años");
            }
        }

        String orgNombre = request.getOrganizacionNombre().trim();
        if (request.isCrearOrganizacion()) {
            // Modo CREAR: la organización NO debe existir ya
            if (usuarioRepository.existsByOrganizacionNombre(orgNombre)) {
                throw new IllegalArgumentException("Ya existe una organización con ese nombre. Únete a ella en lugar de crearla.");
            }
        } else {
            // Modo UNIRSE: la organización DEBE existir
            if (!usuarioRepository.existsByOrganizacionNombre(orgNombre)) {
                throw new IllegalArgumentException("No existe ninguna organización con ese nombre.");
            }
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .fechaNacimiento(request.getFechaNacimiento())
                .email(request.getEmail())
                .dni(request.getDni().toUpperCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .organizacionNombre(request.getOrganizacionNombre())
                .build();

        usuarioRepository.save(usuario);

        String token = tokenProvider.generateToken(usuario.getUsername());
        return toResponse(token, usuario);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(auth);
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();

        return toResponse(token, usuario);
    }

    public boolean existeOrganizacion(String nombre) {
        return usuarioRepository.existsByOrganizacionNombre(nombre.trim());
    }

    private AuthResponseDTO toResponse(String token, Usuario usuario) {
        return AuthResponseDTO.builder()
                .token(token)
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .organizacionNombre(usuario.getOrganizacionNombre())
                .build();
    }
}

