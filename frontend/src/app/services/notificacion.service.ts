import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface NotificacionDTO {
  id: number;
  reglamentoId: number;
  reglamentoTitulo: string;
  emisorUsername: string;
  emisorNombre: string;
  createdAt: string;
}

/** Servicio para gestionar notificaciones de solicitud de actualización entre organizadores. */
@Injectable({ providedIn: 'root' })
export class NotificacionService {
  private http = inject(HttpClient);

  /** Envía una notificación al creador de la publicación. */
  enviarNotificacion(reglamentoId: number): Observable<void> {
    return this.http.post<void>(`/api/notificaciones/reglamento/${reglamentoId}`, {});
  }

  /** Obtiene las notificaciones pendientes (no leídas) del usuario autenticado. */
  getPendientes(): Observable<NotificacionDTO[]> {
    return this.http.get<NotificacionDTO[]>('/api/notificaciones/pendientes');
  }

  /** Marca una notificación como leída. */
  marcarLeida(id: number): Observable<void> {
    return this.http.patch<void>(`/api/notificaciones/${id}/leida`, {});
  }
}
