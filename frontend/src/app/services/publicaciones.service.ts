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

export interface ReglamentoCreateDTO {
  titulo: string;
  descripcion?: string;
  visibilidad: 'PUBLICO' | 'SOLO_MIEMBROS' | 'PRIVADO';
  contenido: string;
  versionEtiqueta?: string;
}

export interface VersionDTO {
  id: number;
  reglamentoId: number;
  reglamentoTitulo: string;
  numeroVersion: number;
  versionEtiqueta: string;
  contenido: string;
  estado: 'BORRADOR' | 'PUBLICADO' | 'ARCHIVADO';
  fechaCreacion: string;
  creadoPorUsername: string;
}

export interface VersionCreateDTO {
  contenido: string;
  versionEtiqueta?: string;
}

export interface PublicoViewDTO {
  titulo: string;
  contenido: string;
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

  getById(id: number): Observable<ReglamentoDTO> {
    return this.http.get<ReglamentoDTO>(`/api/reglamentos/${id}`);
  }

  getPublicoView(id: number): Observable<PublicoViewDTO> {
    return this.http.get<PublicoViewDTO>(`/api/reglamentos/publico/${id}`);
  }

  create(data: ReglamentoCreateDTO): Observable<ReglamentoDTO> {
    return this.http.post<ReglamentoDTO>('/api/reglamentos', data);
  }

  update(id: number, data: Partial<ReglamentoCreateDTO>): Observable<ReglamentoDTO> {
    return this.http.put<ReglamentoDTO>(`/api/reglamentos/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/reglamentos/${id}`);
  }

  getVersiones(reglamentoId: number): Observable<VersionDTO[]> {
    return this.http.get<VersionDTO[]>(`/api/reglamentos/${reglamentoId}/versiones`);
  }

  getSiguienteEtiqueta(reglamentoId: number): Observable<{ versionEtiqueta: string }> {
    return this.http.get<{ versionEtiqueta: string }>(`/api/reglamentos/${reglamentoId}/versiones/siguiente-etiqueta`);
  }

  crearVersion(reglamentoId: number, data: VersionCreateDTO): Observable<VersionDTO> {
    return this.http.post<VersionDTO>(`/api/reglamentos/${reglamentoId}/versiones`, data);
  }

  activarVersion(versionId: number): Observable<VersionDTO> {
    return this.http.patch<VersionDTO>(`/api/versiones/${versionId}/activar`, {});
  }

  downloadAsMarkdown(pub: ReglamentoDTO): void {
    this.getVersiones(pub.id).subscribe(versions => {
      const v = versions.find(ver => ver.estado === 'PUBLICADO') ?? versions[versions.length - 1];
      if (!v?.contenido) return;
      const blob = new Blob([v.contenido], { type: 'text/markdown' });
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `${pub.titulo}.md`;
      a.click();
      URL.revokeObjectURL(a.href);
    });
  }
}

