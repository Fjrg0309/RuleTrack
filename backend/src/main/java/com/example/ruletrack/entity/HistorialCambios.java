package com.example.ruletrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad de auditoría que registra cada cambio realizado sobre una {@link VersionReglamento}.
 * <p>
 * Cada entrada almacena quién realizó el cambio ({@code realizadoPor}),
 * una descripción de la acción ({@code descripcion}) y, opcionalmente,
 * el campo modificado ({@code campo}) con los valores anterior ({@code valorAnterior})
 * y nuevo ({@code valorNuevo}).
 * Las entradas se crean automáticamente desde {@link com.example.ruletrack.service.HistorialCambiosService}.
 */
@Entity
@Table(name = "historial_cambios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCambios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_reglamento_id", nullable = false)
    private VersionReglamento versionReglamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "descripcion_cambio", columnDefinition = "TEXT", nullable = false)
    private String descripcionCambio;

    @Column(name = "campo_modificado", length = 100)
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @CreationTimestamp
    @Column(name = "fecha_cambio", updatable = false)
    private LocalDateTime fechaCambio;
}
