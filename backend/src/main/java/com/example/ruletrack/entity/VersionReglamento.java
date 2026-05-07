package com.example.ruletrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una versión concreta de un {@link Reglamento}.
 * <p>
 * Cada versión tiene un número secuencial ({@code numeroVersion}), una etiqueta legible
 * ({@code versionEtiqueta}, p. ej. "1.0", "2.3") y un estado que determina su ciclo de vida:
 * <ul>
 *   <li>{@link EstadoVersion#BORRADOR} – en redacción, no publicado.</li>
 *   <li>{@link EstadoVersion#PUBLICADO} – activo y visible para los usuarios con acceso.</li>
 *   <li>{@link EstadoVersion#ARCHIVADO} – reemplazado por una versión posterior.</li>
 * </ul>
 * El contenido ({@code contenido}) se almacena en formato Markdown.
 */
@Entity
@Table(name = "versiones_reglamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionReglamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reglamento_id", nullable = false)
    private Reglamento reglamento;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    /** Etiqueta semántica de versión: "1.0", "1.1", "2.0"… */
    @Column(name = "version_etiqueta", length = 20)
    private String versionEtiqueta;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoVersion estado = EstadoVersion.BORRADOR;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @OneToMany(mappedBy = "versionReglamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SugerenciaIA> sugerencias = new ArrayList<>();

    @OneToMany(mappedBy = "versionReglamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistorialCambios> historial = new ArrayList<>();
}
