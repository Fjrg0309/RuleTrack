package com.example.ruletrack.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LlmService {

    private final RestClient restClient;
    private final String model;

    public LlmService(
            @Value("${app.llm.api-url}") String apiUrl,
            @Value("${app.llm.api-key}") String apiKey,
            @Value("${app.llm.model}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String revisarTexto(String contenido) {
        String prompt = """
                Eres un experto en revisión de reglamentos. Revisa el siguiente texto de un reglamento \
                e identifica errores ortográficos, gramaticales o de redacción. \
                Devuelve un listado de correcciones sugeridas.
                
                Texto:
                %s
                """.formatted(contenido);
        return completar(prompt);
    }

    public String generarResumen(String contenido) {
        String prompt = """
                Eres un experto en síntesis de documentos legales. \
                Genera un resumen claro y conciso del siguiente reglamento, \
                destacando los puntos principales.
                
                Reglamento:
                %s
                """.formatted(contenido);
        return completar(prompt);
    }

    public String detectarIncoherencias(String contenido) {
        String prompt = """
                Eres un experto en análisis de reglamentos. \
                Analiza el siguiente texto y detecta posibles incoherencias, contradicciones \
                o artículos que entren en conflicto entre sí. Explica cada incoherencia encontrada.
                
                Reglamento:
                %s
                """.formatted(contenido);
        return completar(prompt);
    }

    private String completar(String userPrompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.3
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return "Sin respuesta del modelo";

            List<?> choices = (List<?>) response.get("choices");
            if (choices == null || choices.isEmpty()) return "Sin respuesta del modelo";

            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            log.error("Error al llamar al LLM: {}", e.getMessage());
            return "Error al procesar la solicitud con el modelo de lenguaje";
        }
    }
}
