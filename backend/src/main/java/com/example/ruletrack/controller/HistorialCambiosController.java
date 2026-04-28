package com.example.ruletrack.controller;

import com.example.ruletrack.dto.HistorialCambiosResponseDTO;
import com.example.ruletrack.service.HistorialCambiosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HistorialCambiosController {

    private final HistorialCambiosService historialService;

    @GetMapping("/versiones/{versionId}/historial")
    public ResponseEntity<List<HistorialCambiosResponseDTO>> findByVersion(@PathVariable Long versionId) {
        return ResponseEntity.ok(historialService.findByVersion(versionId));
    }
}
