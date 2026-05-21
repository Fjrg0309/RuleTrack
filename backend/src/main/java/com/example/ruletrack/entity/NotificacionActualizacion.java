package com.example.ruletrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Notificación de solicitud de actualización enviada por un organizador
 * al creador de una publicación (reglamento).
 */
@Entity
@Table(name = "notificaciones_actualizacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionActualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reglamento_id", nullable = false)
    private Reglamento reglamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @Builder.Default
    @Column(nullable = false)
    private boolean leida = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
