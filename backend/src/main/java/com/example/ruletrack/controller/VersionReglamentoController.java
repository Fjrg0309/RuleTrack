package com.example.ruletrack.controller;

import com.example.ruletrack.dto.VersionReglamentoRequestDTO;
import com.example.ruletrack.dto.VersionReglamentoResponseDTO;
import com.example.ruletrack.entity.EstadoVersion;
import com.example.ruletrack.service.VersionReglamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de versiones de reglamentos.
 * Gestiona el ciclo de vida de las versiones: creación, cambio de estado,
 * activación y consulta. Solo accesible por usuarios con rol ORGANIZADOR.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Versiones", description = "Gestión de versiones de reglamentos (ORGANIZADOR)")
@SecurityRequirement(name = "bearerAuth")
public class VersionReglamentoController {

    private final VersionReglamentoService versionService;

    @Operation(summary = "Listar versiones de un reglamento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de versiones"),
        @ApiResponse(responseCode = "404", description = "Reglamento no encontrado")
    })
    @GetMapping("/reglamentos/{reglamentoId}/versiones")
    public ResponseEntity<List<VersionReglamentoResponseDTO>> findByReglamento(@PathVariable Long reglamentoId) {
        return ResponseEntity.ok(versionService.findByReglamento(reglamentoId));
    }

    @Operation(summary = "Obtener la siguiente etiqueta de versión sugerida",
               description = "Calcula automáticamente la próxima etiqueta (ej. 1.0, 1.1, 2.0).")
    @ApiResponse(responseCode = "200", description = "Etiqueta de la siguiente versión")
    @GetMapping("/reglamentos/{reglamentoId}/versiones/siguiente-etiqueta")
    public ResponseEntity<Map<String, String>> getSiguienteEtiqueta(@PathVariable Long reglamentoId) {
        String siguiente = versionService.getSiguienteVersionPorDefecto(reglamentoId);
        return ResponseEntity.ok(Map.of("versionEtiqueta", siguiente));
    }

    @Operation(summary = "Obtener una versión por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Versión encontrada"),
        @ApiResponse(responseCode = "404", description = "Versión no encontrada")
    })
    @GetMapping("/versiones/{id}")
    public ResponseEntity<VersionReglamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(versionService.findById(id));
    }

    @Operation(summary = "Crear nueva versión de un reglamento")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Versión creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Reglamento no encontrado")
    })
    @PostMapping("/reglamentos/{reglamentoId}/versiones")
    public ResponseEntity<VersionReglamentoResponseDTO> create(@PathVariable Long reglamentoId,
                                                                @Valid @RequestBody VersionReglamentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(versionService.create(reglamentoId, request));
    }

    @Operation(summary = "Cambiar estado de una versión",
               description = "Permite transitar entre estados: BORRADOR, PUBLICADO, ARCHIVADO.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Versión no encontrada")
    })
    @PatchMapping("/versiones/{id}/estado")
    public ResponseEntity<VersionReglamentoResponseDTO> cambiarEstado(@PathVariable Long id,
                                                                       @RequestParam EstadoVersion estado) {
        return ResponseEntity.ok(versionService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Activar una versión",
               description = "Publica la versión indicada y archiva automáticamente las demás del mismo reglamento.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Versión activada"),
        @ApiResponse(responseCode = "404", description = "Versión no encontrada")
    })
    @PatchMapping("/versiones/{id}/activar")
    public ResponseEntity<VersionReglamentoResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(versionService.activar(id));
    }
}
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
