import { Component, Input } from '@angular/core';
import { ReglamentoDTO } from '../../services/publicaciones.service';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrl: './card.component.scss',
})
export class CardComponent {
  @Input() pub?: ReglamentoDTO;

  get titulo(): string { return this.pub?.titulo ?? 'Sin título'; }
  get version(): string { return this.pub?.ultimaVersion ?? '—'; }
  get organizacion(): string { return this.pub?.organizacionNombre ?? ''; }
  get visibilidad(): string {
    switch (this.pub?.visibilidad) {
      case 'PUBLICO': return 'Público';
      case 'SOLO_MIEMBROS': return 'Solo miembros';
      case 'PRIVADO': return 'Privado';
      default: return '';
    }
  }
}
