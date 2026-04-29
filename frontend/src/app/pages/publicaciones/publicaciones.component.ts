import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

export interface Publication {
  id: number;
  name: string;
  version: string;
  status: 'public' | 'members';
  url: string;
}

@Component({
  selector: 'app-publicaciones',
  templateUrl: './publicaciones.component.html',
  styleUrl: './publicaciones.component.scss',
})
export class PublicacionesComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  protected publications: Publication[] = [
    { id: 1, name: 'Reglas Campeonato FS Andalucía', version: '2.1', status: 'public', url: 'https://FederacionDeCadiz/reglamento.md' },
    { id: 2, name: 'Requisitos de Liga Regional de Voleibol', version: '1.5', status: 'public', url: 'https://FederacionDeCadiz/reglamento.md' },
    { id: 3, name: 'Reglas Provisionales de Campeonato de Ajedrez', version: '1.0', status: 'members', url: 'https://FederacionDeCadiz/reglamento.md' },
    { id: 4, name: 'Acta de partido de la liga regional 13 de Mayo', version: '1.5', status: 'public', url: 'https://FederacionDeCadiz/reglamento.md' },
    { id: 5, name: 'Reglas Liga de Baloncesto Regional', version: '3.4', status: 'public', url: 'https://FederacionDeCadiz/reglamento.md' },
  ];

  protected copiedId: number | null = null;

  protected copyUrl(pub: Publication): void {
    navigator.clipboard.writeText(pub.url).catch(() => {});
    this.copiedId = pub.id;
    setTimeout(() => (this.copiedId = null), 1500);
  }

  protected editPublication(pub: Publication): void {
    this.router.navigate(['/ajustes-publicacion'], { queryParams: { id: pub.id } });
  }
}
