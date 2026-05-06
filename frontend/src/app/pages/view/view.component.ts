import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalComponent } from '../../components/modal/modal.component';
import { PublicacionesService } from '../../services/publicaciones.service';

@Component({
  selector: 'app-view',
  imports: [ModalComponent],
  templateUrl: './view.component.html',
  styleUrl: './view.component.scss',
})
export class ViewComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);

  titulo = '';
  contenido = '';
  isLoading = true;
  errorMsg = '';
  downloadModalVisible = false;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/']);
      return;
    }

    this.pubService.getPublicoView(id).subscribe({
      next: (data) => {
        this.titulo = data.titulo;
        this.contenido = data.contenido;
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Documento no encontrado o no disponible públicamente.';
        this.isLoading = false;
      }
    });
  }

  openDownloadModal(): void {
    this.downloadModalVisible = true;
  }

  onDownloadFormat(format: 'md' | 'pdf'): void {
    this.downloadModalVisible = false;
    if (format === 'md') {
      const blob = new Blob([this.contenido], { type: 'text/markdown' });
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `${this.titulo}.md`;
      a.click();
      URL.revokeObjectURL(a.href);
    } else {
      PublicacionesService.printAsPdf(this.titulo, this.contenido);
    }
  }

  onDownloadCancelled(): void {
    this.downloadModalVisible = false;
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
