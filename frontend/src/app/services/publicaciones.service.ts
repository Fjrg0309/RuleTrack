import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface ReglamentoDTO {
  id: number;
  titulo: string;
  visibilidad: 'PUBLICO' | 'SOLO_MIEMBROS' | 'PRIVADO';
  ultimaVersion: string;
  organizacionNombre: string;
  creadoPorNombre: string;
  descripcion?: string;
}

@Injectable({ providedIn: 'root' })
export class PublicacionesService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  getVisibles(): Observable<ReglamentoDTO[]> {
    if (this.auth.currentUser()) {
      return this.http.get<ReglamentoDTO[]>('/api/reglamentos/visibles');
    }
    return this.http.get<ReglamentoDTO[]>('/api/reglamentos/publicos');
  }

  getPublicos(): Observable<ReglamentoDTO[]> {
    return this.http.get<ReglamentoDTO[]>('/api/reglamentos/publicos');
  }
}
