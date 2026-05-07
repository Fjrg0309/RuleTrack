package com.example.ruletrack.service;

import com.example.ruletrack.dto.ReglamentoRequestDTO;
import com.example.ruletrack.dto.ReglamentoResponseDTO;
import com.example.ruletrack.entity.*;
import com.example.ruletrack.exception.ResourceNotFoundException;
import com.example.ruletrack.repository.ReglamentoRepository;
import com.example.ruletrack.repository.UsuarioRepository;
import com.example.ruletrack.repository.VersionReglamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de reglamentos.
 * Cubre consulta pública, búsqueda por ID, creación y eliminación,
 * con validación de visibilidad y control de acceso.
 */
@ExtendWith(MockitoExtension.class)
class ReglamentoServiceTest {

    @Mock
    private ReglamentoRepository reglamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VersionReglamentoRepository versionRepository;

    @Mock
    private HistorialCambiosService historialService;

    @InjectMocks
    private ReglamentoService reglamentoService;

    private Usuario organizador;

    @BeforeEach
    void setUp() {
        organizador = Usuario.builder()
                .id(10L)
                .username("admin")
                .nombre("Admin")
                .apellidos("Org")
                .rol(Rol.ORGANIZADOR)
                .organizacionNombre("ClubDeportivo")
                .build();
    }

    /** Configura el SecurityContext con el usuario dado. */
    private void mockSecurityContext(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private Reglamento buildReglamento(Long id, String titulo, VisibilidadReglamento vis) {
        return Reglamento.builder()
                .id(id)
                .titulo(titulo)
                .descripcion("Descripción de prueba")
                .visibilidad(vis)
                .creadoPor(organizador)
                .createdAt(LocalDateTime.now())
                .versiones(new ArrayList<>())
                .usuariosPermitidos(new ArrayList<>())
                .build();
    }

    // ─────────────────── findPublicos() ───────────────────

    @Test
    @DisplayName("findPublicos – devuelve solo los reglamentos con visibilidad PUBLICO")
    void findPublicos_returnsOnlyPublicReglamentos() {
        Reglamento pub1 = buildReglamento(1L, "Reglamento Fútbol", VisibilidadReglamento.PUBLICO);
        Reglamento pub2 = buildReglamento(2L, "Reglamento Tenis", VisibilidadReglamento.PUBLICO);

        when(reglamentoRepository.findByVisibilidad(VisibilidadReglamento.PUBLICO))
                .thenReturn(List.of(pub1, pub2));

        List<ReglamentoResponseDTO> result = reglamentoService.findPublicos();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("titulo")
                .containsExactlyInAnyOrder("Reglamento Fútbol", "Reglamento Tenis");
    }

    @Test
    @DisplayName("findPublicos – devuelve lista vacía si no hay reglamentos públicos")
    void findPublicos_noResults_returnsEmpty() {
        when(reglamentoRepository.findByVisibilidad(VisibilidadReglamento.PUBLICO))
                .thenReturn(List.of());

        List<ReglamentoResponseDTO> result = reglamentoService.findPublicos();

        assertThat(result).isEmpty();
    }

    // ─────────────────── findById() ───────────────────

    @Test
    @DisplayName("findById – devuelve el reglamento cuando existe")
    void findById_found_returnsDTO() {
        Reglamento reg = buildReglamento(5L, "Reglamento Natación", VisibilidadReglamento.PUBLICO);
        when(reglamentoRepository.findById(5L)).thenReturn(Optional.of(reg));

        ReglamentoResponseDTO result = reglamentoService.findById(5L);

        assertThat(result).isNotNull();
        assertThat(result.getTitulo()).isEqualTo("Reglamento Natación");
    }

    @Test
    @DisplayName("findById – lanza ResourceNotFoundException si el reglamento no existe")
    void findById_notFound_throwsResourceNotFoundException() {
        when(reglamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reglamentoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─────────────────── findAll() ───────────────────

    @Test
    @DisplayName("findAll – devuelve todos los reglamentos del repositorio")
    void findAll_returnsAll() {
        Reglamento pub  = buildReglamento(1L, "Fútbol",   VisibilidadReglamento.PUBLICO);
        Reglamento priv = buildReglamento(2L, "Privado",  VisibilidadReglamento.PRIVADO);

        when(reglamentoRepository.findAll()).thenReturn(List.of(pub, priv));

        List<ReglamentoResponseDTO> result = reglamentoService.findAll();

        assertThat(result).hasSize(2);
    }

    // ─────────────────── delete() ───────────────────

    @Test
    @DisplayName("delete – elimina correctamente un reglamento existente")
    void delete_existingId_callsDeleteById() {
        when(reglamentoRepository.existsById(3L)).thenReturn(true);

        reglamentoService.delete(3L);

        verify(reglamentoRepository).deleteById(3L);
    }

    @Test
    @DisplayName("delete – lanza ResourceNotFoundException si el reglamento no existe")
    void delete_notFound_throwsResourceNotFoundException() {
        when(reglamentoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> reglamentoService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reglamentoRepository, never()).deleteById(any());
    }

    // ─────────────────── create() ───────────────────

    @Test
    @DisplayName("create – crea reglamento sin versión cuando no hay contenido")
    void create_noContent_createsOnlyReglamento() {
        mockSecurityContext("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(organizador));

        ReglamentoRequestDTO request = new ReglamentoRequestDTO();
        request.setTitulo("Nuevo Reglamento");
        request.setVisibilidad(VisibilidadReglamento.PUBLICO);

        Reglamento saved = buildReglamento(7L, "Nuevo Reglamento", VisibilidadReglamento.PUBLICO);
        when(reglamentoRepository.save(any(Reglamento.class))).thenReturn(saved);
        when(reglamentoRepository.findById(7L)).thenReturn(Optional.of(saved));

        ReglamentoResponseDTO result = reglamentoService.create(request);

        assertThat(result.getTitulo()).isEqualTo("Nuevo Reglamento");
        verify(versionRepository, never()).save(any());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("create – crea reglamento con versión inicial cuando hay contenido")
    void create_withContent_createsReglamentoAndVersion() {
        mockSecurityContext("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(organizador));

        ReglamentoRequestDTO request = new ReglamentoRequestDTO();
        request.setTitulo("Reglamento con Versión");
        request.setVisibilidad(VisibilidadReglamento.PUBLICO);
        request.setContenido("# Artículo 1\nContenido inicial");
        request.setVersionEtiqueta("1.0");

        Reglamento saved = buildReglamento(8L, "Reglamento con Versión", VisibilidadReglamento.PUBLICO);
        when(reglamentoRepository.save(any(Reglamento.class))).thenReturn(saved);
        when(reglamentoRepository.findById(8L)).thenReturn(Optional.of(saved));

        VersionReglamento version = VersionReglamento.builder()
                .id(1L)
                .reglamento(saved)
                .numeroVersion(1)
                .versionEtiqueta("1.0")
                .contenido(request.getContenido())
                .estado(EstadoVersion.PUBLICADO)
                .creadoPor(organizador)
                .build();
        when(versionRepository.save(any(VersionReglamento.class))).thenReturn(version);

        reglamentoService.create(request);

        verify(versionRepository).save(any(VersionReglamento.class));
        verify(historialService).registrar(any(), any(), anyString(), isNull(), isNull(), isNull());

        SecurityContextHolder.clearContext();
    }
}
