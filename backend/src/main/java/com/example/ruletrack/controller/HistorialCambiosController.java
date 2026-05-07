package com.example.ruletrack.controller;

import com.example.ruletrack.dto.HistorialCambiosResponseDTO;
import com.example.ruletrack.service.HistorialCambiosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador del historial de cambios de una versión de reglamento.
 * Permite consultar todos los cambios registrados automáticamente sobre una versión.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Historial de Cambios", description = "Auditoría de modificaciones sobre versiones de reglamentos")
@SecurityRequirement(name = "bearerAuth")
public class HistorialCambiosController {

    private final HistorialCambiosService historialService;

    @Operation(summary = "Listar historial de cambios de una versión",
               description = "Devuelve todos los cambios registrados sobre la versión indicada, ordenados cronológicamente.")
    @ApiResponse(responseCode = "200", description = "Lista de cambios")
    @GetMapping("/versiones/{versionId}/historial")
    public ResponseEntity<List<HistorialCambiosResponseDTO>> findByVersion(@PathVariable Long versionId) {
        return ResponseEntity.ok(historialService.findByVersion(versionId));
    }
}
