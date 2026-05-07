package com.example.ruletrack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global de OpenAPI 3 / Swagger UI para la API de RuleTrack.
 * <p>
 * Accesible en tiempo de ejecución en:
 * <ul>
 *   <li>Swagger UI: {@code /swagger-ui.html}</li>
 *   <li>Spec JSON: {@code /v3/api-docs}</li>
 *   <li>Spec YAML: {@code /v3/api-docs.yaml}</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI ruleTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RuleTrack API")
                        .description("""
                                API REST del sistema de gestión y versionado de reglamentos de organizaciones.
                                Permite registrar usuarios, gestionar reglamentos con control de visibilidad,
                                versionar documentos, y obtener correcciones y sugerencias mediante IA.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RuleTrack Team")
                                .email("admin@ruletrack.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Introduce el token JWT obtenido en /api/auth/login")));
    }
}
