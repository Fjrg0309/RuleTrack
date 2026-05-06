import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';
import { ModalComponent } from '../../components/modal/modal.component';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';

@Component({
  selector: 'app-home-organizer',
  imports: [RouterLink, CardComponent, ModalComponent],
  templateUrl: './home-organizer.component.html',
  styleUrl: './home-organizer.component.scss',
})
export class HomeOrganizerComponent implements OnInit {
  protected auth = inject(AuthService);
  private pubService = inject(PublicacionesService);

  protected publicaciones = signal<ReglamentoDTO[]>([]);
  protected downloadModalVisible = signal(false);
  protected pendingDownload = signal<ReglamentoDTO | null>(null);

  ngOnInit(): void {
    this.pubService.getTodasDeOrganizacion().subscribe({
      next: (data) => this.publicaciones.set(data.slice(0, 7)),
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
