package com.example.ruletrack.controller;

import com.example.ruletrack.dto.CorrectionItemDTO;
import com.example.ruletrack.dto.CorrectionRequestDTO;
import com.example.ruletrack.dto.CorrectionResponseDTO;
import com.example.ruletrack.service.LlmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de anÃ¡lisis de correcciones mediante IA.
 * Recibe el contenido de un documento y devuelve sugerencias de correcciÃ³n
 * generadas por el modelo de lenguaje configurado.
 */
@RestController
@RequestMapping("/api/corrections")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Correcciones IA", description = "AnÃ¡lisis y sugerencias de correcciÃ³n mediante LLM")
public class CorrectionController {

    private final LlmService llmService;

    @Operation(summary = "Analizar documento y obtener correcciones",
               description = "EnvÃ­a el contenido de un documento al LLM y devuelve una lista de sugerencias de correcciÃ³n.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "AnÃ¡lisis completado"),
        @ApiResponse(responseCode = "503", description = "Servicio LLM no disponible (API key no configurada o error de autorizaciÃ³n)"),
        @ApiResponse(responseCode = "500", description = "Error interno al procesar el documento")
    })
    @PostMapping("/analyze")
    public ResponseEntity<CorrectionResponseDTO> analyze(@RequestBody CorrectionRequestDTO request) {
        if (!llmService.isApiKeyConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new CorrectionResponseDTO(List.of()));
        }
        try {
            List<CorrectionItemDTO> items = llmService.analizarCorrecciones(request.content());
            return ResponseEntity.ok(new CorrectionResponseDTO(items));
        } catch (Exception e) {
            log.error("Error en /api/corrections/analyze: {}", e.getMessage());
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("401") || msg.contains("Unauthorized") || msg.contains("403") || msg.contains("402")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new CorrectionResponseDTO(List.of()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CorrectionResponseDTO(List.of()));
        }
    }
}
