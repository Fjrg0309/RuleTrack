package com.example.ruletrack.service;

import com.example.ruletrack.dto.auth.AuthResponseDTO;
import com.example.ruletrack.dto.auth.LoginRequestDTO;
import com.example.ruletrack.dto.auth.RegisterRequestDTO;
import com.example.ruletrack.dto.auth.UpdateProfileRequestDTO;
import com.example.ruletrack.entity.Rol;
import com.example.ruletrack.entity.Usuario;
import com.example.ruletrack.repository.UsuarioRepository;
import com.example.ruletrack.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de autenticación.
 * Verifica registro, login, actualización de perfil y consulta de miembros,
 * usando mocks para aislar el servicio de sus dependencias.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO validRegisterRequest;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequestDTO();
        validRegisterRequest.setUsername("juanito");
        validRegisterRequest.setNombre("Juan");
        validRegisterRequest.setApellidos("García");
        validRegisterRequest.setFechaNacimiento(LocalDate.of(1995, 5, 20));
        validRegisterRequest.setEmail("juan@ejemplo.com");
        validRegisterRequest.setDni("12345678A");
        validRegisterRequest.setPassword("clave1234");
        validRegisterRequest.setRol(Rol.USUARIO);
        validRegisterRequest.setOrganizacionNombre("ClubDeportivo");
        validRegisterRequest.setCrearOrganizacion(false);
    }

    // ─────────────────── register() ───────────────────

    @Test
    @DisplayName("register – éxito: usuario creado y token devuelto")
    void register_success() {
        when(usuarioRepository.existsByUsername("juanito")).thenReturn(false);
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(false);
        when(usuarioRepository.existsByDni("12345678A")).thenReturn(false);
        when(usuarioRepository.existsByOrganizacionNombre("ClubDeportivo")).thenReturn(true); // unirse
        when(passwordEncoder.encode("clave1234")).thenReturn("$hashed");

        Usuario saved = Usuario.builder()
                .id(1L)
                .username("juanito")
                .nombre("Juan")
                .apellidos("García")
                .fechaNacimiento(LocalDate.of(1995, 5, 20))
                .email("juan@ejemplo.com")
                .dni("12345678A")
                .password("$hashed")
                .rol(Rol.USUARIO)
                .organizacionNombre("ClubDeportivo")
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(saved);
        when(tokenProvider.generateToken("juanito")).thenReturn("jwt-token");

        AuthResponseDTO result = authService.register(validRegisterRequest);

        assertThat(result.getUsername()).isEqualTo("juanito");
        assertThat(result.getToken()).isEqualTo("jwt-token");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("register – falla si el username ya existe")
    void register_duplicateUsername_throwsIllegalArgument() {
        when(usuarioRepository.existsByUsername("juanito")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre de usuario");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("register – falla si el email ya está registrado")
    void register_duplicateEmail_throwsIllegalArgument() {
        when(usuarioRepository.existsByUsername("juanito")).thenReturn(false);
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("register – falla si el DNI ya está registrado")
    void register_duplicateDni_throwsIllegalArgument() {
        when(usuarioRepository.existsByUsername("juanito")).thenReturn(false);
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(false);
        when(usuarioRepository.existsByDni("12345678A")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DNI");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("register – falla si se intenta unir a una organización inexistente")
    void register_joinNonExistentOrg_throwsIllegalArgument() {
        when(usuarioRepository.existsByUsername("juanito")).thenReturn(false);
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(false);
        when(usuarioRepository.existsByDni("12345678A")).thenReturn(false);
        when(usuarioRepository.existsByOrganizacionNombre("ClubDeportivo")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe");
    }

    @Test
    @DisplayName("register – falla si un ORGANIZADOR es menor de 18 años")
    void register_organizadorMenorDeEdad_throwsIllegalArgument() {
        validRegisterRequest.setRol(Rol.ORGANIZADOR);
        validRegisterRequest.setFechaNacimiento(LocalDate.now().minusYears(17));

        when(usuarioRepository.existsByUsername("juanito")).thenReturn(false);
        when(usuarioRepository.existsByEmail("juan@ejemplo.com")).thenReturn(false);
        when(usuarioRepository.existsByDni("12345678A")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(validRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("18");
    }

    // ─────────────────── login() ───────────────────

    @Test
    @DisplayName("login – éxito: token devuelto correctamente")
    void login_success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("juanito");
        loginRequest.setPassword("clave1234");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("juanito");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        Usuario usuario = Usuario.builder()
                .id(1L)
                .username("juanito")
                .nombre("Juan")
                .apellidos("García")
                .email("juan@ejemplo.com")
                .rol(Rol.USUARIO)
                .organizacionNombre("ClubDeportivo")
                .build();
        when(usuarioRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
        when(tokenProvider.generateToken(auth)).thenReturn("jwt-login-token");

        AuthResponseDTO result = authService.login(loginRequest);

        assertThat(result.getToken()).isEqualTo("jwt-login-token");
        assertThat(result.getUsername()).isEqualTo("juanito");
    }

    @Test
    @DisplayName("login – falla con credenciales incorrectas")
    void login_badCredentials_throwsBadCredentials() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("juanito");
        loginRequest.setPassword("mala");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ─────────────────── updateProfile() ───────────────────

    @Test
    @DisplayName("updateProfile – actualiza nombre y email correctamente")
    void updateProfile_success() {
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO();
        request.setNombre("Juan Actualizado");
        request.setEmail("nuevo@ejemplo.com");

        Usuario usuario = Usuario.builder()
                .id(1L)
                .username("juanito")
                .nombre("Juan")
                .apellidos("García")
                .email("juan@ejemplo.com")
                .rol(Rol.USUARIO)
                .organizacionNombre("ClubDeportivo")
                .build();

        when(usuarioRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("nuevo@ejemplo.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateToken("juanito")).thenReturn("new-token");

        AuthResponseDTO result = authService.updateProfile("juanito", request);

        assertThat(result.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(result.getEmail()).isEqualTo("nuevo@ejemplo.com");
    }

    @Test
    @DisplayName("updateProfile – falla si el nuevo email ya está en uso")
    void updateProfile_emailAlreadyUsed_throwsIllegalArgument() {
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO();
        request.setNombre("Juan");
        request.setEmail("ocupado@ejemplo.com");

        Usuario usuario = Usuario.builder()
                .username("juanito")
                .email("juan@ejemplo.com")
                .build();

        when(usuarioRepository.findByUsername("juanito")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("ocupado@ejemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.updateProfile("juanito", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    // ─────────────────── getMiembros() ───────────────────

    @Test
    @DisplayName("getMiembros – devuelve la lista de miembros de la organización")
    void getMiembros_success() {
        Usuario organizador = Usuario.builder()
                .username("juanito")
                .nombre("Juan")
                .apellidos("García")
                .rol(Rol.ORGANIZADOR)
                .organizacionNombre("ClubDeportivo")
                .build();

        Usuario miembro = Usuario.builder()
                .username("maria")
                .nombre("María")
                .apellidos("López")
                .rol(Rol.USUARIO)
                .organizacionNombre("ClubDeportivo")
                .build();

        when(usuarioRepository.findByUsername("juanito")).thenReturn(Optional.of(organizador));
        when(usuarioRepository.findByOrganizacionNombre("ClubDeportivo"))
                .thenReturn(List.of(organizador, miembro));

        var miembros = authService.getMiembros("juanito");

        assertThat(miembros).hasSize(2);
        assertThat(miembros).extracting("username").contains("juanito", "maria");
    }

    @Test
    @DisplayName("getMiembros – devuelve lista vacía si el usuario no tiene organización")
    void getMiembros_noOrg_returnsEmpty() {
        Usuario usuario = Usuario.builder()
                .username("anonimo")
                .organizacionNombre(null)
                .build();

        when(usuarioRepository.findByUsername("anonimo")).thenReturn(Optional.of(usuario));

        var result = authService.getMiembros("anonimo");

        assertThat(result).isEmpty();
    }
}
