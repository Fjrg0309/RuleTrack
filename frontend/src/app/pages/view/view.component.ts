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
