import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';

@Component({
  selector: 'app-publicaciones',
  templateUrl: './publicaciones.component.html',
  styleUrl: './publicaciones.component.scss',
})
export class PublicacionesComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);

  protected publications = signal<ReglamentoDTO[]>([]);
  protected copiedId: number | null = null;

  ngOnInit(): void {
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publications.set(data),
      error: () => {}
    });
  }

  protected copyUrl(pub: ReglamentoDTO): void {
    const url = `${window.location.origin}/api/reglamentos/${pub.id}/descargar`;
    navigator.clipboard.writeText(url).catch(() => {});
    this.copiedId = pub.id;
    setTimeout(() => (this.copiedId = null), 1500);
  }

  protected editPublication(pub: ReglamentoDTO): void {
    this.router.navigate(['/ajustes-publicacion'], { queryParams: { id: pub.id } });
  }
}
