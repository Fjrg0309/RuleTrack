package com.example.ruletrack.controller;

import com.example.ruletrack.dto.VersionReglamentoRequestDTO;
import com.example.ruletrack.dto.VersionReglamentoResponseDTO;
import com.example.ruletrack.entity.EstadoVersion;
import com.example.ruletrack.service.VersionReglamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VersionReglamentoController {

    private final VersionReglamentoService versionService;

    @GetMapping("/reglamentos/{reglamentoId}/versiones")
    public ResponseEntity<List<VersionReglamentoResponseDTO>> findByReglamento(@PathVariable Long reglamentoId) {
        return ResponseEntity.ok(versionService.findByReglamento(reglamentoId));
    }

    @GetMapping("/reglamentos/{reglamentoId}/versiones/siguiente-etiqueta")
    public ResponseEntity<Map<String, String>> getSiguienteEtiqueta(@PathVariable Long reglamentoId) {
        String siguiente = versionService.getSiguienteVersionPorDefecto(reglamentoId);
        return ResponseEntity.ok(Map.of("versionEtiqueta", siguiente));
    }

    @GetMapping("/versiones/{id}")
    public ResponseEntity<VersionReglamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(versionService.findById(id));
    }

    @PostMapping("/reglamentos/{reglamentoId}/versiones")
    public ResponseEntity<VersionReglamentoResponseDTO> create(@PathVariable Long reglamentoId,
                                                                @Valid @RequestBody VersionReglamentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(versionService.create(reglamentoId, request));
    }

    @PatchMapping("/versiones/{id}/estado")
    public ResponseEntity<VersionReglamentoResponseDTO> cambiarEstado(@PathVariable Long id,
                                                                       @RequestParam EstadoVersion estado) {
        return ResponseEntity.ok(versionService.cambiarEstado(id, estado));
    }

    /** Activa una versión (la pone como PUBLICADO y archiva el resto). */
    @PatchMapping("/versiones/{id}/activar")
    public ResponseEntity<VersionReglamentoResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(versionService.activar(id));
    }
}
