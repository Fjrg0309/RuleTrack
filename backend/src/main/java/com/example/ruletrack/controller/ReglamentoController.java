package com.example.ruletrack.controller;

import com.example.ruletrack.dto.PublicoViewDTO;
import com.example.ruletrack.dto.ReglamentoRequestDTO;
import com.example.ruletrack.dto.ReglamentoResponseDTO;
import com.example.ruletrack.service.ReglamentoService;
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

/**
 * Controlador de reglamentos.
 * Gestiona el ciclo de vida de los reglamentos: creación, lectura, actualización y borrado,
 * con control de visibilidad (PÚBLICO, SOLO_MIEMBROS, PRIVADO).
 */
@RestController
@RequestMapping("/api/reglamentos")
@RequiredArgsConstructor
@Tag(name = "Reglamentos", description = "Gestión de reglamentos con control de visibilidad")
public class ReglamentoController {

    private final ReglamentoService reglamentoService;

    @Operation(summary = "Listar reglamentos públicos",
               description = "Devuelve todos los reglamentos con visibilidad PÚBLICO. No requiere autenticación.")
    @ApiResponse(responseCode = "200", description = "Lista de reglamentos públicos")
    @GetMapping("/publicos")
    public ResponseEntity<List<ReglamentoResponseDTO>> findPublicos() {
        return ResponseEntity.ok(reglamentoService.findPublicos());
    }

    @Operation(summary = "Vista pública de un reglamento",
               description = "Devuelve el contenido de la última versión publicada de un reglamento.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contenido del reglamento"),
        @ApiResponse(responseCode = "404", description = "Reglamento no encontrado o sin acceso")
    })
    @GetMapping("/publico/{id}")
    public ResponseEntity<PublicoViewDTO> getPublicoView(@PathVariable Long id) {
        return ResponseEntity.ok(reglamentoService.getPublicoView(id));
    }

    @Operation(summary = "Listar todos los reglamentos de la organización",
               description = "Devuelve todos los reglamentos de la organización del usuario, sin filtro de visibilidad.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reglamentos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/organizacion")
    public ResponseEntity<List<ReglamentoResponseDTO>> findTodasDeOrganizacion() {
        return ResponseEntity.ok(reglamentoService.findTodasDeOrganizacion());
    }

    @Operation(summary = "Listar reglamentos visibles para el usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lista de reglamentos accesibles")
    @GetMapping("/visibles")
    public ResponseEntity<List<ReglamentoResponseDTO>> findVisibles() {
        return ResponseEntity.ok(reglamentoService.findVisiblesParaUsuarioActual());
    }

    @Operation(summary = "Listar todos los reglamentos (administración)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<ReglamentoResponseDTO>> findAll() {
        return ResponseEntity.ok(reglamentoService.findAll());
    }

    @Operation(summary = "Obtener un reglamento por ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reglamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Reglamento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReglamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reglamentoService.findById(id));
    }

    @Operation(summary = "Crear nuevo reglamento",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reglamento creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Solo ORGANIZADOR puede crear reglamentos")
    })
    @PostMapping
    public ResponseEntity<ReglamentoResponseDTO> create(@Valid @RequestBody ReglamentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reglamentoService.create(request));
    }

    @Operation(summary = "Actualizar un reglamento",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reglamento actualizado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos"),
        @ApiResponse(responseCode = "404", description = "Reglamento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReglamentoResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody ReglamentoRequestDTO request) {
        return ResponseEntity.ok(reglamentoService.update(id, request));
    }

    @Operation(summary = "Eliminar un reglamento",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reglamento eliminado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos"),
        @ApiResponse(responseCode = "404", description = "Reglamento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reglamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

    private final ReglamentoService reglamentoService;

    /** Endpoint público: solo reglamentos PUBLICO */
    @GetMapping("/publicos")
    public ResponseEntity<List<ReglamentoResponseDTO>> findPublicos() {
        return ResponseEntity.ok(reglamentoService.findPublicos());
    }

    /** Endpoint público: vista de un reglamento PUBLICO (sin autenticación) */
    @GetMapping("/publico/{id}")
    public ResponseEntity<PublicoViewDTO> getPublicoView(@PathVariable Long id) {
        return ResponseEntity.ok(reglamentoService.getPublicoView(id));
    }

    /** Endpoint autenticado: todos los reglamentos de la organización del usuario (sin filtro de visibilidad) */
    @GetMapping("/organizacion")
    public ResponseEntity<List<ReglamentoResponseDTO>> findTodasDeOrganizacion() {
        return ResponseEntity.ok(reglamentoService.findTodasDeOrganizacion());
    }

    /** Endpoint autenticado: reglamentos visibles según el usuario actual */
    @GetMapping("/visibles")
    public ResponseEntity<List<ReglamentoResponseDTO>> findVisibles() {
        return ResponseEntity.ok(reglamentoService.findVisiblesParaUsuarioActual());
    }

    @GetMapping
    public ResponseEntity<List<ReglamentoResponseDTO>> findAll() {
        return ResponseEntity.ok(reglamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReglamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reglamentoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReglamentoResponseDTO> create(@Valid @RequestBody ReglamentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reglamentoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReglamentoResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody ReglamentoRequestDTO request) {
        return ResponseEntity.ok(reglamentoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reglamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

