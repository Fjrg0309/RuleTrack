package com.example.ruletrack.service;

import com.example.ruletrack.dto.PublicoViewDTO;
import com.example.ruletrack.dto.ReglamentoRequestDTO;
import com.example.ruletrack.dto.ReglamentoResponseDTO;
import com.example.ruletrack.entity.*;
import com.example.ruletrack.exception.ResourceNotFoundException;
import com.example.ruletrack.repository.ReglamentoRepository;
import com.example.ruletrack.repository.UsuarioRepository;
import com.example.ruletrack.repository.VersionReglamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReglamentoService {

    private final ReglamentoRepository reglamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VersionReglamentoRepository versionRepository;
    private final HistorialCambiosService historialService;

    /** Todos los reglamentos (uso interno / admin) */
    @Transactional(readOnly = true)
    public List<ReglamentoResponseDTO> findAll() {
        return reglamentoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /** Reglamentos públicos (sin autenticación) */
    @Transactional(readOnly = true)
    public List<ReglamentoResponseDTO> findPublicos() {
        return reglamentoRepository.findByVisibilidad(VisibilidadReglamento.PUBLICO)
                .stream().map(this::toDTO).toList();
    }

    /** Reglamentos visibles para el usuario autenticado */
    @Transactional(readOnly = true)
    public List<ReglamentoResponseDTO> findVisiblesParaUsuarioActual() {
        Usuario usuario = getCurrentUser();
        return reglamentoRepository.findVisiblesParaUsuario(
                usuario.getId(),
                usuario.getOrganizacionNombre()
        ).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ReglamentoResponseDTO findById(Long id) {
        return toDTO(getReglamentoOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PublicoViewDTO getPublicoView(Long id) {
        Reglamento r = getReglamentoOrThrow(id);
        Optional<Usuario> currentUser = getCurrentUserOptional();

        boolean canAccess = switch (r.getVisibilidad()) {
            case PUBLICO -> true;
            case SOLO_MIEMBROS -> currentUser.isPresent() && (
                r.getCreadoPor().getOrganizacionNombre().equals(currentUser.get().getOrganizacionNombre())
                || r.getCreadoPor().getId().equals(currentUser.get().getId())
            );
            case PRIVADO -> currentUser.isPresent()
                && r.getCreadoPor().getId().equals(currentUser.get().getId());
        };

        if (!canAccess) {
            throw new ResourceNotFoundException("Reglamento", id);
        }

        String contenido = r.getVersiones().stream()
                .filter(v -> v.getEstado() == EstadoVersion.PUBLICADO)
                .findFirst()
                .map(VersionReglamento::getContenido)
                .orElse("");
        return new PublicoViewDTO(r.getTitulo(), contenido);
    }

    @Transactional
    public ReglamentoResponseDTO create(ReglamentoRequestDTO request) {
        Usuario currentUser = getCurrentUser();

        List<Usuario> permitidos = new ArrayList<>();
        if (request.getVisibilidad() == VisibilidadReglamento.PRIVADO
                && request.getUsuariosPermitidosIds() != null) {
            permitidos = usuarioRepository.findAllById(request.getUsuariosPermitidosIds());
        }

        Reglamento reglamento = Reglamento.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .visibilidad(request.getVisibilidad() != null ? request.getVisibilidad() : VisibilidadReglamento.PUBLICO)
                .creadoPor(currentUser)
                .usuariosPermitidos(permitidos)
                .build();

        Reglamento saved = reglamentoRepository.save(reglamento);

        // Crear primera versión si viene contenido
        if (request.getContenido() != null && !request.getContenido().isBlank()) {
            String etiqueta = (request.getVersionEtiqueta() != null && !request.getVersionEtiqueta().isBlank())
                    ? request.getVersionEtiqueta().trim()
                    : "1.0";

            VersionReglamento v = VersionReglamento.builder()
                    .reglamento(saved)
                    .numeroVersion(1)
                    .versionEtiqueta(etiqueta)
                    .contenido(request.getContenido())
                    .estado(EstadoVersion.PUBLICADO)
                    .creadoPor(currentUser)
                    .build();
            VersionReglamento savedV = versionRepository.save(v);
            historialService.registrar(savedV, currentUser, "Publicación inicial versión " + etiqueta, null, null, null);
        }

        return toDTO(reglamentoRepository.findById(saved.getId()).orElse(saved));
    }

    @Transactional
    public ReglamentoResponseDTO update(Long id, ReglamentoRequestDTO request) {
        Reglamento reglamento = getReglamentoOrThrow(id);
        reglamento.setTitulo(request.getTitulo());
        reglamento.setDescripcion(request.getDescripcion());
        if (request.getVisibilidad() != null) {
            reglamento.setVisibilidad(request.getVisibilidad());
        }
        if (request.getVisibilidad() == VisibilidadReglamento.PRIVADO
                && request.getUsuariosPermitidosIds() != null) {
            reglamento.setUsuariosPermitidos(usuarioRepository.findAllById(request.getUsuariosPermitidosIds()));
        }
        return toDTO(reglamentoRepository.save(reglamento));
    }

    @Transactional
    public void delete(Long id) {
        if (!reglamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reglamento", id);
        }
        reglamentoRepository.deleteById(id);
    }

    private Reglamento getReglamentoOrThrow(Long id) {
        return reglamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reglamento", id));
    }

    /** Todos los reglamentos de la organización del usuario autenticado (sin filtro de visibilidad) */
    @Transactional(readOnly = true)
    public List<ReglamentoResponseDTO> findTodasDeOrganizacion() {
        Usuario usuario = getCurrentUser();
        return reglamentoRepository.findAllByOrganizacionNombre(usuario.getOrganizacionNombre())
                .stream().map(this::toDTO).toList();
    }

    private Optional<Usuario> getCurrentUserOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return usuarioRepository.findByUsername(auth.getName());
    }

    private Usuario getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    private ReglamentoResponseDTO toDTO(Reglamento r) {
        String ultimaVersion = r.getVersiones().stream()
                .max((a, b) -> a.getNumeroVersion().compareTo(b.getNumeroVersion()))
                .map(v -> v.getVersionEtiqueta() != null ? v.getVersionEtiqueta() : String.valueOf(v.getNumeroVersion()) + ".0")
                .orElse("—");

        return ReglamentoResponseDTO.builder()
                .id(r.getId())
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .visibilidad(r.getVisibilidad().name())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .creadoPorUsername(r.getCreadoPor().getUsername())
                .creadoPorNombre(r.getCreadoPor().getNombre() + " " + r.getCreadoPor().getApellidos())
                .organizacionNombre(r.getCreadoPor().getOrganizacionNombre())
                .totalVersiones(r.getVersiones().size())
                .ultimaVersion(ultimaVersion)
                .build();
    }
}

