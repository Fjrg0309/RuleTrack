package com.example.ruletrack.controller;

import com.example.ruletrack.dto.CorrectionItemDTO;
import com.example.ruletrack.dto.CorrectionRequestDTO;
import com.example.ruletrack.dto.CorrectionResponseDTO;
import com.example.ruletrack.service.LlmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corrections")
@RequiredArgsConstructor
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CorrectionResponseDTO(List.of()));
        }
    }
}
