package com.example.ruletrack.controller;

import com.example.ruletrack.dto.PublicoViewDTO;
import com.example.ruletrack.dto.ReglamentoRequestDTO;
import com.example.ruletrack.dto.ReglamentoResponseDTO;
import com.example.ruletrack.service.ReglamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reglamentos")
@RequiredArgsConstructor
public class ReglamentoController {

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

