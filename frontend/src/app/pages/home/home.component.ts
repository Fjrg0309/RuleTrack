import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';
import { ModalComponent } from '../../components/modal/modal.component';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink, CardComponent, ModalComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);
  private toastService = inject(ToastService);

  protected publicaciones = signal<ReglamentoDTO[]>([]);
  protected downloadModalVisible = signal(false);
  protected pendingDownload = signal<ReglamentoDTO | null>(null);

  ngOnInit(): void {
    if (this.auth.currentUser()?.rol === 'ORGANIZADOR') {
      this.router.navigate(['/organizer'], { replaceUrl: true });
    }
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publicaciones.set(data.slice(0, 7)),
      error: () => {}
    });
  }

  protected showPermissionToast(message: string): void {
    this.toastService.show(message, 'warning');
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
