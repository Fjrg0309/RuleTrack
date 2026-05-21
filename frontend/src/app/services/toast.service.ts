import { Injectable, signal } from '@angular/core';

/** Representa un mensaje de notificación temporal en la interfaz. */
export interface Toast {
  id: number;
  message: string;
  type: 'error' | 'warning' | 'info';
  /** Ruta opcional de navegación al hacer clic en el toast. */
  link?: string;
}

/** Servicio global para mostrar notificaciones toast al usuario. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private counter = 0;

  /**
   * Muestra un mensaje de toast durante un tiempo determinado.
   * @param message Texto a mostrar al usuario.
   * @param type Tipo de mensaje: 'error', 'warning' o 'info'.
   * @param duration Tiempo en ms antes de que el toast desaparezca (por defecto 3500).
   * @param link Ruta de navegación opcional al hacer clic en el toast.
   */
  show(message: string, type: Toast['type'] = 'error', duration = 3500, link?: string): void {
    const id = ++this.counter;
    this.toasts.update(list => [...list, { id, message, type, link }]);
    setTimeout(() => this.dismiss(id), duration);
  }

  /**
   * Elimina un toast de la lista por su ID.
   * @param id Identificador único del toast a eliminar.
   */
  dismiss(id: number): void {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }
}
