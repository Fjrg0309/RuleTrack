package com.example.ruletrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema.
 * <p>
 * Cada usuario pertenece a exactamente una organización ({@code organizacionNombre})
 * y tiene un rol que determina sus privilegios:
 * <ul>
 *   <li>{@link Rol#ORGANIZADOR} – puede crear y gestionar reglamentos.</li>
 *   <li>{@link Rol#USUARIO} – solo puede consultar reglamentos accesibles.</li>
 * </ul>
 * Los campos {@code username}, {@code email} y {@code dni} son únicos en la base de datos.
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(nullable = false)
    private String password;

    @Column(name = "organizacion_nombre", length = 200)
    private String organizacionNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "creadoPor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reglamento> reglamentos = new ArrayList<>();
}
