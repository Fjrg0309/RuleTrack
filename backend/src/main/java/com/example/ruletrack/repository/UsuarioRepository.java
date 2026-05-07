package com.example.ruletrack.repository;

import com.example.ruletrack.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link com.example.ruletrack.entity.Usuario}.
 * <p>
 * Proporciona operaciones CRUD heredadas de {@link org.springframework.data.jpa.repository.JpaRepository}
 * más consultas derivadas para búsqueda por username/email/DNI,
 * comprobación de unicidad y listado de miembros por organización.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByOrganizacionNombre(String organizacionNombre);

    List<Usuario> findByOrganizacionNombre(String organizacionNombre);
}

