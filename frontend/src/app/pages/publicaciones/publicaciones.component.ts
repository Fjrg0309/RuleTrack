import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';
import { NotificacionService } from '../../services/notificacion.service';
import { ToastService } from '../../services/toast.service';

/**
 * Componente de listado de publicaciones.
 * Muestra los reglamentos accesibles para el usuario con paginación cliente,
 * opciones de copiar URL y, para los organizadores, acceso a los ajustes de cada publicación.
 * Solo el creador de una publicación puede acceder a sus ajustes; los demás organizadores
 * pueden notificar al creador para solicitar una actualización.
 */
@Component({
  selector: 'app-publicaciones',
  templateUrl: './publicaciones.component.html',
  styleUrl: './publicaciones.component.scss',
})
export class PublicacionesComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);
  private notifService = inject(NotificacionService);
  private toastService = inject(ToastService);

  protected publications = signal<ReglamentoDTO[]>([]);
  protected copiedId: number | null = null;
  protected notifiedId: number | null = null;

  protected readonly pageSize = 8;
  protected currentPage = signal(0);

  protected totalPages = computed(() =>
    Math.ceil(this.publications().length / this.pageSize)
  );

  protected pagedPublications = computed(() => {
    const start = this.currentPage() * this.pageSize;
    return this.publications().slice(start, start + this.pageSize);
  });

  ngOnInit(): void {
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publications.set(data),
      error: () => {}
    });
  }

  protected prevPage(): void {
    if (this.currentPage() > 0) this.currentPage.set(this.currentPage() - 1);
  }

  protected nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) this.currentPage.set(this.currentPage() + 1);
  }

  protected copyUrl(pub: ReglamentoDTO): void {
    const url = `${window.location.origin}/view/${pub.id}`;
    navigator.clipboard.writeText(url).catch(() => {});
    this.copiedId = pub.id;
    setTimeout(() => (this.copiedId = null), 1500);
  }

  protected getUrl(pub: ReglamentoDTO): string {
    return `${window.location.origin}/view/${pub.id}`;
  }

  protected editPublication(pub: ReglamentoDTO): void {
    this.router.navigate(['/ajustes-publicacion'], { queryParams: { id: pub.id } });
  }

  /** Devuelve true si la publicación fue creada por el usuario autenticado. */
  protected isOwner(pub: ReglamentoDTO): boolean {
    return pub.creadoPorUsername === this.auth.currentUser()?.username;
  }

  /** Devuelve true si el organizador autenticado pertenece a la misma organización que el creador. */
  protected isSameOrg(pub: ReglamentoDTO): boolean {
    return pub.organizacionNombre === this.auth.currentUser()?.organizacionNombre;
  }

  /** Notifica al creador de la publicación que se necesita una actualización. */
  protected notifyCreator(pub: ReglamentoDTO): void {
    this.notifService.enviarNotificacion(pub.id).subscribe({
      next: () => {
        this.notifiedId = pub.id;
        this.toastService.show(
          `Se ha notificado a ${pub.creadoPorNombre} para actualizar "${pub.titulo}".`,
          'info',
          3500
        );
        setTimeout(() => (this.notifiedId = null), 3000);
      },
      error: () => {
        this.toastService.show('No se pudo enviar la notificación.', 'error', 3500);
      }
    });
  }
}
