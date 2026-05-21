import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';
import { ModalComponent } from '../../components/modal/modal.component';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';
import { NotificacionService } from '../../services/notificacion.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-home-organizer',
  imports: [RouterLink, CardComponent, ModalComponent],
  templateUrl: './home-organizer.component.html',
  styleUrl: './home-organizer.component.scss',
})
export class HomeOrganizerComponent implements OnInit {
  protected auth = inject(AuthService);
  private pubService = inject(PublicacionesService);
  private notifService = inject(NotificacionService);
  private toastService = inject(ToastService);

  protected publicaciones = signal<ReglamentoDTO[]>([]);
  protected downloadModalVisible = signal(false);
  protected pendingDownload = signal<ReglamentoDTO | null>(null);

  ngOnInit(): void {
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publicaciones.set(data.slice(0, 7)),
      error: () => {}
    });

    this.loadPendingNotifications();
  }

  private loadPendingNotifications(): void {
    this.notifService.getPendientes().subscribe({
      next: (notifs) => {
        notifs.forEach((notif, index) => {
          setTimeout(() => {
            const link = `/ajustes-publicacion?id=${notif.reglamentoId}`;
            this.toastService.show(
              `📋 ${notif.emisorNombre} solicita actualizar "${notif.reglamentoTitulo}". Toca para ir a los ajustes.`,
              'info',
              10000,
              link
            );
            this.notifService.marcarLeida(notif.id).subscribe();
          }, index * 400);
        });
      },
      error: () => {}
    });
  }

  protected downloadDoc(pub: ReglamentoDTO): void {
    this.pendingDownload.set(pub);
    this.downloadModalVisible.set(true);
  }

  protected onDownloadFormat(format: 'md' | 'pdf'): void {
    const pub = this.pendingDownload();
    if (!pub) return;
    this.downloadModalVisible.set(false);
    this.pendingDownload.set(null);
    if (format === 'md') {
      this.pubService.downloadAsMarkdown(pub);
    } else {
      this.pubService.downloadAsPdf(pub);
    }
  }

  protected onDownloadCancelled(): void {
    this.downloadModalVisible.set(false);
    this.pendingDownload.set(null);
  }
}
