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

    @Transactional
    public VersionReglamentoResponseDTO create(Long reglamentoId, VersionReglamentoRequestDTO request) {
        Reglamento reglamento = reglamentoRepository.findById(reglamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reglamento", reglamentoId));

        int nextNumero = versionRepository
                .findTopByReglamentoIdOrderByNumeroVersionDesc(reglamentoId)
                .map(v -> v.getNumeroVersion() + 1)
                .orElse(1);

        Usuario currentUser = getCurrentUser();

        VersionReglamento version = VersionReglamento.builder()
                .reglamento(reglamento)
                .numeroVersion(nextNumero)
                .contenido(request.getContenido())
                .creadoPor(currentUser)
                .build();

        VersionReglamento saved = versionRepository.save(version);
        historialService.registrar(saved, currentUser, "Creación de versión " + nextNumero, null, null, null);
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
                .contenido(v.getContenido())
                .estado(v.getEstado())
                .fechaCreacion(v.getFechaCreacion())
                .creadoPorUsername(v.getCreadoPor().getUsername())
                .build();
    }
}
