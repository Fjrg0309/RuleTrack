import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import jsPDF from 'jspdf';

export interface ReglamentoDTO {
  id: number;
  titulo: string;
  visibilidad: 'PUBLICO' | 'SOLO_MIEMBROS' | 'PRIVADO';
  ultimaVersion: string;
  organizacionNombre: string;
  creadoPorNombre: string;
  creadoPorUsername: string;
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

export interface ResumenSeccionDTO {
  titulo: string;
  puntos: string[];
}

export interface ResumenEstructuradoDTO {
  tituloReglamento: string;
  secciones: ResumenSeccionDTO[];
}

export interface VersionPublicaDTO {
  id: number;
  numeroVersion: number;
  versionEtiqueta: string;
  contenido: string;
  fechaCreacion: string;
}

/**
 * Servicio de acceso a la API de reglamentos y versiones.
 * Gestiona las operaciones CRUD sobre publicaciones y su descarga en PDF/MD.
 */
@Injectable({ providedIn: 'root' })
export class PublicacionesService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  /**
   * Devuelve los reglamentos visibles según el rol del usuario.
   * Los usuarios autenticados ven los visibles para su organización;
   * los anónimos sólo ven los públicos.
   */
  getVisibles(): Observable<ReglamentoDTO[]> {
    if (this.auth.currentUser()) {
      return this.http.get<ReglamentoDTO[]>('/api/reglamentos/visibles');
    }
    return this.http.get<ReglamentoDTO[]>('/api/reglamentos/publicos');
  }

  /** Devuelve únicamente los reglamentos con visibilidad PÚBÁLICO. */
  getPublicos(): Observable<ReglamentoDTO[]> {
    return this.http.get<ReglamentoDTO[]>('/api/reglamentos/publicos');
  }

  /** Devuelve todos los reglamentos de la organización del usuario autenticado. */
  getTodasDeOrganizacion(): Observable<ReglamentoDTO[]> {
    return this.http.get<ReglamentoDTO[]>('/api/reglamentos/organizacion');
  }

  /**
   * Obtiene un reglamento por su ID.
   * @param id Identificador del reglamento.
   */
  getById(id: number): Observable<ReglamentoDTO> {
    return this.http.get<ReglamentoDTO>(`/api/reglamentos/${id}`);
  }

  /**
   * Devuelve la vista pública de un reglamento (sin autenticación).
   * @param id Identificador del reglamento.
   */
  getPublicoView(id: number): Observable<PublicoViewDTO> {
    return this.http.get<PublicoViewDTO>(`/api/reglamentos/publico/${id}`);
  }

  getResumenPublico(id: number): Observable<ResumenEstructuradoDTO> {
    return this.http.get<ResumenEstructuradoDTO>(`/api/reglamentos/publico/${id}/resumen`);
  }

  getVersionesPublicas(id: number): Observable<VersionPublicaDTO[]> {
    return this.http.get<VersionPublicaDTO[]>(`/api/reglamentos/publico/${id}/versiones`);
  }

  /**
   * Crea un nuevo reglamento con su primera versión.
   * @param data Datos del reglamento a crear.
   */
  create(data: ReglamentoCreateDTO): Observable<ReglamentoDTO> {
    return this.http.post<ReglamentoDTO>('/api/reglamentos', data);
  }

  /**
   * Actualiza los metadatos de un reglamento existente.
   * @param id Identificador del reglamento.
   * @param data Campos a actualizar.
   */
  update(id: number, data: Partial<ReglamentoCreateDTO>): Observable<ReglamentoDTO> {
    return this.http.put<ReglamentoDTO>(`/api/reglamentos/${id}`, data);
  }

  /**
   * Elimina un reglamento y todas sus versiones.
   * @param id Identificador del reglamento a eliminar.
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/reglamentos/${id}`);
  }

  /**
   * Obtiene todas las versiones de un reglamento ordenadas por fecha.
   * @param reglamentoId Identificador del reglamento.
   */
  getVersiones(reglamentoId: number): Observable<VersionDTO[]> {
    return this.http.get<VersionDTO[]>(`/api/reglamentos/${reglamentoId}/versiones`);
  }

  /**
   * Sugiere la siguiente etiqueta de versión para un reglamento (ej. '1.1').
   * @param reglamentoId Identificador del reglamento.
   */
  getSiguienteEtiqueta(reglamentoId: number): Observable<{ versionEtiqueta: string }> {
    return this.http.get<{ versionEtiqueta: string }>(`/api/reglamentos/${reglamentoId}/versiones/siguiente-etiqueta`);
  }

  /**
   * Añade una nueva versión a un reglamento existente.
   * @param reglamentoId Identificador del reglamento.
   * @param data Contenido y etiqueta de la nueva versión.
   */
  crearVersion(reglamentoId: number, data: VersionCreateDTO): Observable<VersionDTO> {
    return this.http.post<VersionDTO>(`/api/reglamentos/${reglamentoId}/versiones`, data);
  }

  /**
   * Activa una versión concreta, marcándola como PUBLICADO.
   * @param versionId Identificador de la versión a activar.
   */
  activarVersion(versionId: number): Observable<VersionDTO> {
    return this.http.patch<VersionDTO>(`/api/versiones/${versionId}/activar`, {});
  }

  /**
   * Descarga la última versión publicada del reglamento como fichero Markdown.
   * Crea un elemento <a> dinámico para disparar la descarga desde el navegador.
   * @param pub Reglamento a descargar.
   */
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

  /**
   * Descarga la última versión publicada del reglamento como fichero PDF
   * generado con jsPDF directamente en el navegador (sin llamada al servidor).
   * @param pub Reglamento a descargar.
   */
  downloadAsPdf(pub: ReglamentoDTO): void {
    this.getVersiones(pub.id).subscribe(versions => {
      const v = versions.find(ver => ver.estado === 'PUBLICADO') ?? versions[versions.length - 1];
      if (!v?.contenido) return;
      PublicacionesService.generatePdf(pub.titulo, v.contenido);
    });
  }

  /**
   * Genera y descarga un PDF a partir del título y el contenido en Markdown.
   * Parsea los encabezados, listas y párrafos para aplicar estilos con jsPDF.
   * @param titulo Título del documento a mostrar en el PDF.
   * @param contenido Contenido del documento en formato Markdown.
   */
  static generatePdf(titulo: string, contenido: string): void {
    const doc = new jsPDF({ unit: 'pt', format: 'a4' });
    const margin = 56;
    const pageWidth = doc.internal.pageSize.getWidth();
    const pageHeight = doc.internal.pageSize.getHeight();
    const maxWidth = pageWidth - margin * 2;
    let y = margin;

    const checkPage = (needed: number) => {
      if (y + needed > pageHeight - margin) {
        doc.addPage();
        y = margin;
      }
    };

    // Strip inline markdown markers (bold, italic, code, links)
    const stripInline = (text: string) =>
      text
        .replace(/\*\*(.+?)\*\*/g, '$1')
        .replace(/__(.+?)__/g, '$1')
        .replace(/\*(.+?)\*/g, '$1')
        .replace(/_(.+?)_/g, '$1')
        .replace(/`(.+?)`/g, '$1')
        .replace(/\[(.+?)\]\(.+?\)/g, '$1');

    // Document title
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.setTextColor(31, 58, 110);
    checkPage(28);
    doc.text(titulo, margin, y);
    y += 10;
    doc.setDrawColor(91, 141, 239);
    doc.setLineWidth(1.5);
    doc.line(margin, y, pageWidth - margin, y);
    y += 22;

    const rawLines = contenido.split('\n');
    let i = 0;

    while (i < rawLines.length) {
      const raw = rawLines[i];
      const trimmed = raw.trim();

      // Empty line
      if (trimmed === '') {
        y += 5;
        i++;
        continue;
      }

      // Heading 1 (#)
      if (/^# /.test(raw)) {
        const text = stripInline(raw.replace(/^# /, ''));
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(15);
        doc.setTextColor(31, 58, 110);
        const wrapped = doc.splitTextToSize(text, maxWidth);
        for (const line of wrapped) {
          checkPage(20);
          doc.text(line, margin, y);
          y += 20;
        }
        doc.setDrawColor(186, 210, 254);
        doc.setLineWidth(0.7);
        checkPage(8);
        doc.line(margin, y, pageWidth - margin, y);
        y += 10;
        i++;
        continue;
      }

      // Heading 2 (##)
      if (/^## /.test(raw)) {
        const text = stripInline(raw.replace(/^## /, ''));
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(12);
        doc.setTextColor(40, 40, 40);
        const wrapped = doc.splitTextToSize(text, maxWidth);
        for (const line of wrapped) {
          checkPage(18);
          doc.text(line, margin, y);
          y += 18;
        }
        y += 3;
        i++;
        continue;
      }

      // Heading 3 (###)
      if (/^### /.test(raw)) {
        const text = stripInline(raw.replace(/^### /, ''));
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(11);
        doc.setTextColor(60, 60, 60);
        const wrapped = doc.splitTextToSize(text, maxWidth);
        for (const line of wrapped) {
          checkPage(16);
          doc.text(line, margin, y);
          y += 16;
        }
        y += 2;
        i++;
        continue;
      }

      // Heading 4–6 (####+)
      if (/^#{4,} /.test(raw)) {
        const text = stripInline(raw.replace(/^#{4,} /, ''));
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(10);
        doc.setTextColor(80, 80, 80);
        const wrapped = doc.splitTextToSize(text, maxWidth);
        for (const line of wrapped) {
          checkPage(14);
          doc.text(line, margin, y);
          y += 14;
        }
        i++;
        continue;
      }

      // Horizontal rule (--- or ***)
      if (/^[-*]{3,}$/.test(trimmed)) {
        checkPage(10);
        doc.setDrawColor(180, 180, 180);
        doc.setLineWidth(0.5);
        doc.line(margin, y, pageWidth - margin, y);
        y += 10;
        i++;
        continue;
      }

      // Unordered list item
      if (/^[\-*+] /.test(raw)) {
        const text = stripInline(raw.replace(/^[\-*+] /, ''));
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(10);
        doc.setTextColor(40, 40, 40);
        const indent = margin + 14;
        const wrapped = doc.splitTextToSize(text, maxWidth - 14);
        checkPage(14);
        doc.text('\u2022', margin + 2, y);
        for (const line of wrapped) {
          checkPage(14);
          doc.text(line, indent, y);
          y += 14;
        }
        i++;
        continue;
      }

      // Ordered list item (1. 2. ...)
      if (/^\d+\.\s/.test(raw)) {
        const match = raw.match(/^(\d+)\.\s(.*)/);
        if (match) {
          const num = match[1];
          const text = stripInline(match[2]);
          doc.setFont('helvetica', 'normal');
          doc.setFontSize(10);
          doc.setTextColor(40, 40, 40);
          const indent = margin + 20;
          const wrapped = doc.splitTextToSize(text, maxWidth - 20);
          checkPage(14);
          doc.text(`${num}.`, margin, y);
          for (const line of wrapped) {
            checkPage(14);
            doc.text(line, indent, y);
            y += 14;
          }
        }
        i++;
        continue;
      }

      // Blockquote (> text)
      if (/^> /.test(raw)) {
        const text = stripInline(raw.replace(/^> /, ''));
        doc.setFont('helvetica', 'italic');
        doc.setFontSize(10);
        doc.setTextColor(90, 90, 90);
        const qIndent = margin + 12;
        const wrapped = doc.splitTextToSize(text, maxWidth - 12);
        checkPage(14);
        doc.setDrawColor(91, 141, 239);
        doc.setLineWidth(2);
        doc.line(margin + 2, y - 10, margin + 2, y + (wrapped.length - 1) * 14);
        for (const line of wrapped) {
          checkPage(14);
          doc.text(line, qIndent, y);
          y += 14;
        }
        y += 2;
        i++;
        continue;
      }

      // Normal paragraph
      const text = stripInline(raw);
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(10);
      doc.setTextColor(40, 40, 40);
      const wrapped = doc.splitTextToSize(text, maxWidth);
      for (const line of wrapped) {
        checkPage(14);
        doc.text(line, margin, y);
        y += 14;
      }
      y += 3;
      i++;
    }

    doc.save(`${titulo}.pdf`);
  }

  /** @deprecated use generatePdf */
  static printAsPdf(titulo: string, contenido: string): void {
    PublicacionesService.generatePdf(titulo, contenido);
  }
}

