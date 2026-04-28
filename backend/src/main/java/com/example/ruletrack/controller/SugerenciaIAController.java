package com.example.ruletrack.controller;

import com.example.ruletrack.dto.SugerenciaIAResponseDTO;
import com.example.ruletrack.service.SugerenciaIAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SugerenciaIAController {

    private final SugerenciaIAService sugerenciaService;

    @GetMapping("/versiones/{versionId}/sugerencias")
    public ResponseEntity<List<SugerenciaIAResponseDTO>> findByVersion(@PathVariable Long versionId) {
        return ResponseEntity.ok(sugerenciaService.findByVersion(versionId));
    }

    @PostMapping("/versiones/{versionId}/sugerencias/revision")
    public ResponseEntity<SugerenciaIAResponseDTO> generarRevision(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugerenciaService.generarRevision(versionId));
    }

    @PostMapping("/versiones/{versionId}/sugerencias/resumen")
    public ResponseEntity<SugerenciaIAResponseDTO> generarResumen(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugerenciaService.generarResumen(versionId));
    }

    @PostMapping("/versiones/{versionId}/sugerencias/incoherencias")
    public ResponseEntity<SugerenciaIAResponseDTO> detectarIncoherencias(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugerenciaService.detectarIncoherencias(versionId));
    }

    @PatchMapping("/sugerencias/{id}/aplicada")
    public ResponseEntity<SugerenciaIAResponseDTO> marcarAplicada(@PathVariable Long id,
                                                                    @RequestParam boolean aplicada) {
        return ResponseEntity.ok(sugerenciaService.marcarAplicada(id, aplicada));
    }
}
