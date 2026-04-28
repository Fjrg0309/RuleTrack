package com.example.ruletrack.service;

import com.example.ruletrack.dto.HistorialCambiosResponseDTO;
import com.example.ruletrack.entity.HistorialCambios;
import com.example.ruletrack.entity.Usuario;
import com.example.ruletrack.entity.VersionReglamento;
import com.example.ruletrack.repository.HistorialCambiosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialCambiosService {

    private final HistorialCambiosRepository historialRepository;

    public List<HistorialCambiosResponseDTO> findByVersion(Long versionId) {
        return historialRepository.findByVersionReglamentoIdOrderByFechaCambioDesc(versionId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public void registrar(VersionReglamento version, Usuario usuario, String descripcion,
                          String campo, String valorAnterior, String valorNuevo) {
        HistorialCambios historial = HistorialCambios.builder()
                .versionReglamento(version)
                .usuario(usuario)
                .descripcionCambio(descripcion)
                .campoModificado(campo)
                .valorAnterior(valorAnterior)
                .valorNuevo(valorNuevo)
                .build();
        historialRepository.save(historial);
    }

    private HistorialCambiosResponseDTO toDTO(HistorialCambios h) {
        return HistorialCambiosResponseDTO.builder()
                .id(h.getId())
                .versionReglamentoId(h.getVersionReglamento().getId())
                .usuarioUsername(h.getUsuario().getUsername())
                .descripcionCambio(h.getDescripcionCambio())
                .campoModificado(h.getCampoModificado())
                .valorAnterior(h.getValorAnterior())
                .valorNuevo(h.getValorNuevo())
                .fechaCambio(h.getFechaCambio())
                .build();
    }
}
