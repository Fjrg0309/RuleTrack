package com.example.ruletrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sugerencias_ia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_reglamento_id", nullable = false)
    private VersionReglamento versionReglamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSugerencia tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @CreationTimestamp
    @Column(name = "fecha_generacion", updatable = false)
    private LocalDateTime fechaGeneracion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aplicada = false;
}
