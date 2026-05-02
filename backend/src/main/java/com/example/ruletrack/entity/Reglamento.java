package com.example.ruletrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reglamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reglamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VisibilidadReglamento visibilidad = VisibilidadReglamento.PUBLICO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    // Usuarios explícitamente autorizados (solo para PRIVADO)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reglamento_usuarios_permitidos",
        joinColumns = @JoinColumn(name = "reglamento_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @Builder.Default
    private List<Usuario> usuariosPermitidos = new ArrayList<>();

    @OneToMany(mappedBy = "reglamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VersionReglamento> versiones = new ArrayList<>();
}
