import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PublicacionesService } from '../../services/publicaciones.service';

@Component({
  selector: 'app-view',
  imports: [],
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

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/']);
      return;
    }

    this.pubService.getById(id).subscribe({
      next: (pub) => {
        this.titulo = pub.titulo;
        this.pubService.getVersiones(id).subscribe({
          next: (versions) => {
            const v = versions.find(ver => ver.estado === 'PUBLICADO') ?? versions[versions.length - 1];
            this.contenido = v?.contenido ?? '';
            this.isLoading = false;
          },
          error: () => {
            this.errorMsg = 'No se pudo cargar el contenido del documento.';
            this.isLoading = false;
          }
        });
      },
      error: () => {
        this.errorMsg = 'Documento no encontrado.';
        this.isLoading = false;
      }
    });
  }

  download(): void {
    const blob = new Blob([this.contenido], { type: 'text/markdown' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `${this.titulo}.md`;
    a.click();
    URL.revokeObjectURL(a.href);
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
