package com.example.ruletrack.controller;

import com.example.ruletrack.dto.SugerenciaIAResponseDTO;
import com.example.ruletrack.service.SugerenciaIAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de sugerencias generadas por IA sobre versiones de reglamentos.
 * Permite generar revisiones, resÃºmenes y detecciÃ³n de incoherencias,
 * asÃ­ como marcar sugerencias como aplicadas.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Sugerencias IA", description = "GeneraciÃ³n y gestiÃ³n de sugerencias de IA sobre versiones de reglamentos")
@SecurityRequirement(name = "bearerAuth")
public class SugerenciaIAController {

    private final SugerenciaIAService sugerenciaService;

    @Operation(summary = "Listar sugerencias de una versiÃ³n")
    @ApiResponse(responseCode = "200", description = "Lista de sugerencias almacenadas")
    @GetMapping("/versiones/{versionId}/sugerencias")
    public ResponseEntity<List<SugerenciaIAResponseDTO>> findByVersion(@PathVariable Long versionId) {
        return ResponseEntity.ok(sugerenciaService.findByVersion(versionId));
    }

    @Operation(summary = "Generar revisiÃ³n de calidad",
               description = "Llama al LLM para obtener una revisiÃ³n detallada del contenido del reglamento.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sugerencia de revisiÃ³n generada"),
        @ApiResponse(responseCode = "404", description = "VersiÃ³n no encontrada")
    })
    @PostMapping("/versiones/{versionId}/sugerencias/revision")
    public ResponseEntity<SugerenciaIAResponseDTO> generarRevision(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugerenciaService.generarRevision(versionId));
    }

    @Operation(summary = "Generar resumen del reglamento",
               description = "Genera un resumen ejecutivo del contenido de la versiÃ³n mediante IA.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Resumen generado"),
        @ApiResponse(responseCode = "404", description = "VersiÃ³n no encontrada")
    })
    @PostMapping("/versiones/{versionId}/sugerencias/resumen")
    public ResponseEntity<SugerenciaIAResponseDTO> generarResumen(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugerenciaService.generarResumen(versionId));
    }

    @Operation(summary = "Detectar incoherencias en el reglamento",
               description = "Analiza el documento con IA para identificar posibles contradicciones o ambiguÃ­edades.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Incoherencias detectadas"),
        @ApiResponse(responseCode = "404", description = "VersiÃ³n no encontrada")
    })
    @PostMapping("/versiones/{versionId}/sugerencias/incoherencias")
    public ResponseEntity<SugerenciaIAResponseDTO> detectarIncoherencias(@PathVariable Long versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugerenciaService.detectarIncoherencias(versionId));
    }

    @Operation(summary = "Marcar sugerencia como aplicada o descartada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado de sugerencia actualizado"),
        @ApiResponse(responseCode = "404", description = "Sugerencia no encontrada")
    })
    @PatchMapping("/sugerencias/{id}/aplicada")
    public ResponseEntity<SugerenciaIAResponseDTO> marcarAplicada(@PathVariable Long id,
                                                                    @RequestParam boolean aplicada) {
        return ResponseEntity.ok(sugerenciaService.marcarAplicada(id, aplicada));
    }
}
