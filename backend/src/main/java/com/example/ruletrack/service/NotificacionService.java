package com.example.ruletrack.service;

import com.example.ruletrack.dto.NotificacionResponseDTO;
import com.example.ruletrack.entity.NotificacionActualizacion;
import com.example.ruletrack.entity.Reglamento;
import com.example.ruletrack.entity.Rol;
import com.example.ruletrack.entity.Usuario;
import com.example.ruletrack.exception.ResourceNotFoundException;
import com.example.ruletrack.repository.NotificacionActualizacionRepository;
import com.example.ruletrack.repository.ReglamentoRepository;
import com.example.ruletrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionActualizacionRepository notifRepo;
    private final ReglamentoRepository reglamentoRepo;
    private final UsuarioRepository usuarioRepo;

    /**
     * Envía una notificación al creador de la publicación indicando que necesita actualización.
     * Solo los organizadores que no sean creadores pueden enviarla.
     */
    @Transactional
    public void enviarNotificacion(Long reglamentoId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario emisor = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        if (emisor.getRol() != Rol.ORGANIZADOR) {
            throw new AccessDeniedException("Solo los organizadores pueden enviar notificaciones de actualización.");
        }

        Reglamento reglamento = reglamentoRepo.findById(reglamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reglamento", reglamentoId));

        if (reglamento.getCreadoPor().getId().equals(emisor.getId())) {
            throw new AccessDeniedException("No puedes notificarte a ti mismo.");
        }

        // Solo organizadores de la misma organización pueden notificar al creador
        if (!reglamento.getCreadoPor().getOrganizacionNombre()
                .equals(emisor.getOrganizacionNombre())) {
            throw new AccessDeniedException("Solo los organizadores de la misma organización pueden solicitar actualizaciones.");
        }

        // Evitar notificaciones duplicadas pendientes del mismo emisor para el mismo reglamento
        if (notifRepo.existsByReglamentoIdAndEmisorIdAndLeidaFalse(reglamentoId, emisor.getId())) {
            return;
        }

        NotificacionActualizacion notif = NotificacionActualizacion.builder()
                .reglamento(reglamento)
                .emisor(emisor)
                .receptor(reglamento.getCreadoPor())
                .leida(false)
                .build();
        notifRepo.save(notif);
    }

    /**
     * Devuelve las notificaciones no leídas del usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> getPendientes() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario receptor = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return notifRepo.findByReceptorIdAndLeidaFalse(receptor.getId()).stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Marca una notificación como leída. Solo el receptor puede hacerlo.
     */
    @Transactional
    public void marcarLeida(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NotificacionActualizacion notif = notifRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion", id));
        if (!notif.getReceptor().getUsername().equals(username)) {
            throw new AccessDeniedException("No autorizado para marcar esta notificación.");
        }
        notif.setLeida(true);
        notifRepo.save(notif);
    }

    private NotificacionResponseDTO toDTO(NotificacionActualizacion n) {
        return NotificacionResponseDTO.builder()
                .id(n.getId())
                .reglamentoId(n.getReglamento().getId())
                .reglamentoTitulo(n.getReglamento().getTitulo())
                .emisorUsername(n.getEmisor().getUsername())
                .emisorNombre(n.getEmisor().getNombre() + " " + n.getEmisor().getApellidos())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
