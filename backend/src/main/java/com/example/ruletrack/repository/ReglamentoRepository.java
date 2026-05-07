package com.example.ruletrack.repository;

import com.example.ruletrack.entity.Reglamento;
import com.example.ruletrack.entity.VisibilidadReglamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link com.example.ruletrack.entity.Reglamento}.
 * <p>
 * Incluye consultas JPQL complejas para el filtrado de visibilidad:
 * <ul>
 *   <li>{@link #findVisiblesParaUsuario} – devuelve los reglamentos accesibles
 *       según el ID y organización del usuario autenticado.</li>
 *   <li>{@link #findAllByOrganizacionNombre} – devuelve todos los reglamentos
 *       de una organización independientemente de su visibilidad.</li>
 * </ul>
 */
public interface ReglamentoRepository extends JpaRepository<Reglamento, Long> {

    List<Reglamento> findByCreadoPorId(Long usuarioId);

    List<Reglamento> findByTituloContainingIgnoreCase(String titulo);

    List<Reglamento> findByVisibilidad(VisibilidadReglamento visibilidad);

    /**
     * Devuelve publicaciones visibles para un usuario dado:
     * - PUBLICO: siempre visible
     * - SOLO_MIEMBROS: visible si el usuario pertenece a la misma organización que el creador
     * - PRIVADO: visible solo al creador
     */
    @Query("""
        SELECT DISTINCT r FROM Reglamento r
        LEFT JOIN r.creadoPor creador
        WHERE r.visibilidad = 'PUBLICO'
           OR (r.visibilidad = 'SOLO_MIEMBROS'
               AND creador.organizacionNombre = :orgNombre)
           OR (r.visibilidad = 'PRIVADO' AND creador.id = :userId)
        ORDER BY r.createdAt DESC
        """)
    List<Reglamento> findVisiblesParaUsuario(
            @Param("userId") Long userId,
            @Param("orgNombre") String orgNombre
    );

    @Query("""
        SELECT r FROM Reglamento r
        LEFT JOIN r.creadoPor creador
        WHERE creador.organizacionNombre = :orgNombre
        ORDER BY r.createdAt DESC
        """)
    List<Reglamento> findAllByOrganizacionNombre(@Param("orgNombre") String orgNombre);
}

