package com.example.ruletrack.config;

import java.util.Set;

/**
 * Códigos de organización válidos para verificar que un usuario es organizador.
 * Guarda estos códigos en un lugar seguro — son los que los organizadores deben introducir al registrarse.
 *
 * Códigos activos:
 *   ORG-RT-2024-ALPHA-X7K2  → Federación Deportiva de Cádiz
 *   ORG-RT-2024-BETA-M3N8   → Liga Regional de Deportes
 *   ORG-RT-2024-GAMMA-P5Q1  → Club Deportivo Universitario
 *   ORG-RT-2024-DELTA-R9S4  → Asociación Cultural Deportiva
 *   ORG-RT-2024-OMEGA-T6U0  → Comité Técnico Nacional
 *   ORG-RT-2025-SIGMA-K4W9  → Federación Andaluza de Fútbol Sala
 *   ORG-RT-2025-THETA-L2V7  → Real Club de Tenis de la Región
 *   ORG-RT-2025-KAPPA-H8J3  → Asociación de Árbitros Regionales
 */
public final class OrganizacionCodigos {

    private OrganizacionCodigos() {}

    public static final Set<String> CODIGOS_VALIDOS = Set.of(
        "ORG-RT-2024-ALPHA-X7K2",
        "ORG-RT-2024-BETA-M3N8",
        "ORG-RT-2024-GAMMA-P5Q1",
        "ORG-RT-2024-DELTA-R9S4",
        "ORG-RT-2024-OMEGA-T6U0",
        "ORG-RT-2025-SIGMA-K4W9",
        "ORG-RT-2025-THETA-L2V7",
        "ORG-RT-2025-KAPPA-H8J3"
    );

    public static boolean esValido(String codigo) {
        return codigo != null && CODIGOS_VALIDOS.contains(codigo.trim().toUpperCase());
    }
}
