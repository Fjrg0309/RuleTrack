package com.example.ruletrack.service;

import com.example.ruletrack.dto.CorrectionItemDTO;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
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
    private final boolean apiKeyConfigured;

    public LlmService(
            @Value("${app.llm.api-url}") String apiUrl,
            @Value("${app.llm.api-key}") String apiKey,
            @Value("${app.llm.model}") String model) {
        this.model = model;
        this.apiKeyConfigured = apiKey != null && !apiKey.isBlank();
        if (!this.apiKeyConfigured) {
            log.warn("LLM API key is not configured (app.llm.api-key). Corrections endpoint will be unavailable.");
        }
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public boolean isApiKeyConfigured() {
        return apiKeyConfigured;
    }

    public List<CorrectionItemDTO> analizarCorrecciones(String contenido) {
        String systemPrompt = """
                Eres un corrector profesional de documentos oficiales en español. \
                Tu única función es devolver un array JSON de correcciones. \
                SIEMPRE encuentras correcciones: cualquier documento tiene margen de mejora. \
                NUNCA devuelves un array vacío salvo que el documento sea literalmente perfecto. \
                NUNCA envuelvas la respuesta en bloques markdown ni añadas texto fuera del array JSON. \
                Tu respuesta DEBE comenzar con [ y terminar con ].
                """;

        String userPrompt = """
                Analiza el siguiente documento oficial y devuelve TODAS las correcciones posibles. \
                Sé EXHAUSTIVO y ESTRICTO. Busca mínimo 5-10 correcciones por documento. \
                Categorías obligatorias a revisar:
                1. ORTOGRAFÍA: tildes, mayúsculas, signos de puntuación, errores tipográficos.
                2. GRAMÁTICA: concordancia de género/número, verbos incorrectos, pronombres.
                3. FORMALIDAD: sustituye expresiones coloquiales por lenguaje formal y técnico. \
                   Ej: "hacer"→"ejecutar/realizar", "poner"→"establecer/disponer", "hay que"→"deberá", \
                   "cosa"→sustantivo técnico, frases telegráficas→frases completas.
                4. ESTILO Y PRECISIÓN: reescribe frases ambiguas con mayor rigor.
                5. COHERENCIA: referencias inconsistentes, numeración errónea, contradicciones.
                6. VERBOSIDAD: resume párrafos redundantes conservando el significado completo.
                7. CLARIDAD: reformula normas confusas de forma inequívoca.

                Para CADA corrección el campo "explanation" DEBE incluir la categoría y justificación. \
                Ejemplo: "FORMALIDAD – 'hay que poner' es coloquial; se sustituye por 'deberá establecerse'."

                Devuelve ÚNICAMENTE el array JSON (sin markdown, sin texto adicional):
                [
                  {
                    "id": "c1",
                    "original": "texto exacto del documento",
                    "suggestion": "texto corregido",
                    "explanation": "CATEGORÍA – justificación detallada"
                  }
                ]

                Documento:
                %s
                """.formatted(contenido);

        String response = completar(systemPrompt, userPrompt);
        log.debug("Respuesta raw del LLM: {}", response);

        try {
            // Extract JSON array robustly: find first '[' and last ']'
            int start = response.indexOf('[');
            int end = response.lastIndexOf(']');
            if (start < 0 || end <= start) {
                log.error("No se encontró array JSON en la respuesta del LLM: {}", response);
                return List.of();
            }
            String json = response.substring(start, end + 1);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<CorrectionItemDTO>>() {});
        } catch (Exception e) {
            log.error("Error al parsear correcciones del LLM: {}", e.getMessage());
            return List.of();
        }
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
        return completar("You are a helpful assistant.", userPrompt);
    }

    private String completar(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );
        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 4096);

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) throw new RuntimeException("El modelo no devolvió respuesta");

            List<?> choices = (List<?>) response.get("choices");
            if (choices == null || choices.isEmpty()) throw new RuntimeException("El modelo devolvió choices vacío");

            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            return (String) message.get("content");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al llamar al LLM: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el modelo de lenguaje: " + e.getMessage(), e);
        }
    }
}
