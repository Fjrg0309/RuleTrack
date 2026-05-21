package com.example.ruletrack.controller;

import com.example.ruletrack.dto.NotificacionResponseDTO;
import com.example.ruletrack.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar las notificaciones de solicitud de actualización
 * entre organizadores de una misma organización.
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Solicitudes de actualización entre organizadores")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Enviar notificación de actualización al creador de una publicación",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/reglamento/{id}")
    public ResponseEntity<Void> enviar(@PathVariable Long id) {
        notificacionService.enviarNotificacion(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtener notificaciones pendientes del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionResponseDTO>> getPendientes() {
        return ResponseEntity.ok(notificacionService.getPendientes());
    }

    @Operation(summary = "Marcar una notificación como leída",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }
}
