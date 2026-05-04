package com.example.ruletrack.service;

import com.example.ruletrack.dto.VersionReglamentoRequestDTO;
import com.example.ruletrack.dto.VersionReglamentoResponseDTO;
import com.example.ruletrack.entity.*;
import com.example.ruletrack.exception.ResourceNotFoundException;
import com.example.ruletrack.repository.ReglamentoRepository;
import com.example.ruletrack.repository.UsuarioRepository;
import com.example.ruletrack.repository.VersionReglamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VersionReglamentoService {

    private final VersionReglamentoRepository versionRepository;
    private final ReglamentoRepository reglamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialCambiosService historialService;

    public List<VersionReglamentoResponseDTO> findByReglamento(Long reglamentoId) {
        return versionRepository.findByReglamentoIdOrderByNumeroVersionDesc(reglamentoId)
                .stream().map(this::toDTO).toList();
    }

    public VersionReglamentoResponseDTO findById(Long id) {
        return toDTO(getVersionOrThrow(id));
    }

    /** Calcula la siguiente etiqueta de versión por defecto (+0.1 sobre la mayor existente). */
    public String getSiguienteVersionPorDefecto(Long reglamentoId) {
        return versionRepository.findByReglamentoIdOrderByNumeroVersionDesc(reglamentoId)
                .stream()
                .map(v -> v.getVersionEtiqueta() != null ? v.getVersionEtiqueta() : v.getNumeroVersion() + ".0")
                .map(label -> {
                    try { return Double.parseDouble(label); } catch (NumberFormatException e) { return 1.0; }
                })
                .max(java.util.Comparator.naturalOrder())
                .map(max -> String.format("%.1f", Math.round((max + 0.1) * 10.0) / 10.0))
                .orElse("1.0");
    }

    @Transactional
    public VersionReglamentoResponseDTO create(Long reglamentoId, VersionReglamentoRequestDTO request) {
        Reglamento reglamento = reglamentoRepository.findById(reglamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reglamento", reglamentoId));

        // Determinar etiqueta de versión
        String etiqueta = (request.getVersionEtiqueta() != null && !request.getVersionEtiqueta().isBlank())
                ? request.getVersionEtiqueta().trim()
                : getSiguienteVersionPorDefecto(reglamentoId);

        // Validar que no existe ya esa etiqueta en este reglamento
        boolean duplicada = versionRepository.findByReglamentoIdOrderByNumeroVersionDesc(reglamentoId)
                .stream()
                .anyMatch(v -> etiqueta.equals(v.getVersionEtiqueta()));
        if (duplicada) {
            throw new IllegalArgumentException("Ya existe una versión con la etiqueta '" + etiqueta + "' en este reglamento.");
        }

        int nextNumero = versionRepository
                .findTopByReglamentoIdOrderByNumeroVersionDesc(reglamentoId)
                .map(v -> v.getNumeroVersion() + 1)
                .orElse(1);

        Usuario currentUser = getCurrentUser();

        VersionReglamento version = VersionReglamento.builder()
                .reglamento(reglamento)
                .numeroVersion(nextNumero)
                .versionEtiqueta(etiqueta)
                .contenido(request.getContenido())
                .estado(EstadoVersion.PUBLICADO)
                .creadoPor(currentUser)
                .build();

        // Al crear nueva versión publicada, archivar las anteriores publicadas
        versionRepository.findByReglamentoIdAndEstado(reglamentoId, EstadoVersion.PUBLICADO)
                .forEach(v -> {
                    v.setEstado(EstadoVersion.ARCHIVADO);
                    versionRepository.save(v);
                });

        VersionReglamento saved = versionRepository.save(version);
        historialService.registrar(saved, currentUser, "Creación de versión " + etiqueta, null, null, null);
        return toDTO(saved);
    }

    @Transactional
    public VersionReglamentoResponseDTO cambiarEstado(Long id, EstadoVersion nuevoEstado) {
        VersionReglamento version = getVersionOrThrow(id);
        String estadoAnterior = version.getEstado().name();
        version.setEstado(nuevoEstado);
        VersionReglamento saved = versionRepository.save(version);
        historialService.registrar(saved, getCurrentUser(), "Cambio de estado", "estado", estadoAnterior, nuevoEstado.name());
        return toDTO(saved);
    }

    /** Activa una versión (PUBLICADO) y archiva el resto del mismo reglamento. */
    @Transactional
    public VersionReglamentoResponseDTO activar(Long id) {
        VersionReglamento version = getVersionOrThrow(id);
        Long reglamentoId = version.getReglamento().getId();
        Usuario currentUser = getCurrentUser();

        versionRepository.findByReglamentoIdAndEstado(reglamentoId, EstadoVersion.PUBLICADO)
                .forEach(v -> {
                    v.setEstado(EstadoVersion.ARCHIVADO);
                    versionRepository.save(v);
                });

        version.setEstado(EstadoVersion.PUBLICADO);
        VersionReglamento saved = versionRepository.save(version);
        historialService.registrar(saved, currentUser, "Versión activada: " + version.getVersionEtiqueta(), "estado", EstadoVersion.ARCHIVADO.name(), EstadoVersion.PUBLICADO.name());
        return toDTO(saved);
    }

    private VersionReglamento getVersionOrThrow(Long id) {
        return versionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VersionReglamento", id));
    }

    private Usuario getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    private VersionReglamentoResponseDTO toDTO(VersionReglamento v) {
        return VersionReglamentoResponseDTO.builder()
                .id(v.getId())
                .reglamentoId(v.getReglamento().getId())
                .reglamentoTitulo(v.getReglamento().getTitulo())
                .numeroVersion(v.getNumeroVersion())
                .versionEtiqueta(v.getVersionEtiqueta())
                .contenido(v.getContenido())
                .estado(v.getEstado())
                .fechaCreacion(v.getFechaCreacion())
                .creadoPorUsername(v.getCreadoPor().getUsername())
                .build();
    }
}
