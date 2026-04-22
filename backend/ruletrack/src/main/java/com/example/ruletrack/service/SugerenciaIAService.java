package com.example.ruletrack.service;

import com.example.ruletrack.dto.SugerenciaIAResponseDTO;
import com.example.ruletrack.entity.SugerenciaIA;
import com.example.ruletrack.entity.TipoSugerencia;
import com.example.ruletrack.entity.VersionReglamento;
import com.example.ruletrack.exception.ResourceNotFoundException;
import com.example.ruletrack.repository.SugerenciaIARepository;
import com.example.ruletrack.repository.VersionReglamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SugerenciaIAService {

    private final SugerenciaIARepository sugerenciaRepository;
    private final VersionReglamentoRepository versionRepository;
    private final LlmService llmService;

    public List<SugerenciaIAResponseDTO> findByVersion(Long versionId) {
        return sugerenciaRepository.findByVersionReglamentoId(versionId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public SugerenciaIAResponseDTO generarRevision(Long versionId) {
        return generar(versionId, TipoSugerencia.REVISION);
    }

    @Transactional
    public SugerenciaIAResponseDTO generarResumen(Long versionId) {
        return generar(versionId, TipoSugerencia.RESUMEN);
    }

    @Transactional
    public SugerenciaIAResponseDTO detectarIncoherencias(Long versionId) {
        return generar(versionId, TipoSugerencia.INCOHERENCIA);
    }

    @Transactional
    public SugerenciaIAResponseDTO marcarAplicada(Long sugerenciaId, boolean aplicada) {
        SugerenciaIA sugerencia = sugerenciaRepository.findById(sugerenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("SugerenciaIA", sugerenciaId));
        sugerencia.setAplicada(aplicada);
        return toDTO(sugerenciaRepository.save(sugerencia));
    }

    private SugerenciaIAResponseDTO generar(Long versionId, TipoSugerencia tipo) {
        VersionReglamento version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("VersionReglamento", versionId));

        String contenidoLlm = switch (tipo) {
            case REVISION     -> llmService.revisarTexto(version.getContenido());
            case RESUMEN      -> llmService.generarResumen(version.getContenido());
            case INCOHERENCIA -> llmService.detectarIncoherencias(version.getContenido());
        };

        SugerenciaIA sugerencia = SugerenciaIA.builder()
                .versionReglamento(version)
                .tipo(tipo)
                .contenido(contenidoLlm)
                .build();

        return toDTO(sugerenciaRepository.save(sugerencia));
    }

    private SugerenciaIAResponseDTO toDTO(SugerenciaIA s) {
        return SugerenciaIAResponseDTO.builder()
                .id(s.getId())
                .versionReglamentoId(s.getVersionReglamento().getId())
                .tipo(s.getTipo())
                .contenido(s.getContenido())
                .fechaGeneracion(s.getFechaGeneracion())
                .aplicada(s.getAplicada())
                .build();
    }
}
