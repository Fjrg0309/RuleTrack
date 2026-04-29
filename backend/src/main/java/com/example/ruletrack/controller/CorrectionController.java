package com.example.ruletrack.controller;

import com.example.ruletrack.dto.CorrectionItemDTO;
import com.example.ruletrack.dto.CorrectionRequestDTO;
import com.example.ruletrack.dto.CorrectionResponseDTO;
import com.example.ruletrack.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corrections")
@RequiredArgsConstructor
@Slf4j
public class CorrectionController {

    private final LlmService llmService;

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
