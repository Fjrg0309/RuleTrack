import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, timeout } from 'rxjs';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-converting',
  imports: [RouterLink],
  templateUrl: './converting.component.html',
  styleUrl: './converting.component.scss',
})
/**
 * Componente de conversión de documentos.
 * Transforma ficheros PDF y DOCX mediante el backend (FormData multipart),
 * y ficheros TXT/MD directamente en el cliente. Muestra una animación
 * durante el proceso y navega al previsualizador al completar la conversión.
 */
export class ConvertingComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);
  private http = inject(HttpClient);
  private timerId: ReturnType<typeof setTimeout> | null = null;

  fileName = '';
  conversionError = signal('');

  ngOnInit(): void {
    this.fileName = this.fileUploadService.fileName();

    if (!this.fileName) {
      this.router.navigate(['/upload']);
      return;
    }

    this.convert();
  }

  ngOnDestroy(): void {
    if (this.timerId) {
      clearTimeout(this.timerId);
    }
  }

  /**
   * Orquesta el proceso de conversión según la extensión del fichero:
   * - PDF/DOCX: envío al backend como multipart/form-data (FormData).
   * - TXT: envío al backend como text/plain.
   * - MD: lectura local directa con FileReader.
   * Mantiene la animación un mínimo de 2 segundos antes de navegar.
   */
  private async convert(): Promise<void> {
    const file = this.fileUploadService.rawFile();
    const ext = this.fileName.split('.').pop()?.toLowerCase() ?? '';
    const baseName = this.fileName.replace(/\.[^.]+$/, '');
    const mdName = baseName + '.md';

    // Muestra la animación al menos 2 segundos para mejorar la experiencia de usuario
    const minDelay = new Promise<void>(resolve => {
      this.timerId = setTimeout(resolve, 2000);
    });

    try {
      let mdContent: string;

      if (ext === 'pdf' || ext === 'docx') {
        // Formato 1: multipart/form-data — backend convierte con PDFBox / Apache POI
        mdContent = await this.convertViaBackend(file!);
      } else if (ext === 'txt') {
        // Formato 2: text/plain — el backend recibe el texto y devuelve Markdown
        mdContent = await this.convertTextViaBackend(file!, baseName);
      } else {
        // MD: lectura local sin llamada al servidor
        mdContent = await this.readAsText(file!);
      }

      await minDelay;
      this.fileUploadService.setFile(mdName, mdContent);
      this.router.navigate(['/preview']);
    } catch (err: unknown) {
      await minDelay;
      let msg = 'Error desconocido al convertir el archivo.';
      if (err instanceof Error) {
        msg = err.message;
      } else if (typeof err === 'object' && err !== null) {
        const httpErr = err as { error?: unknown; status?: number; message?: string };
        if (httpErr.status === 413) {
          msg = 'El archivo es demasiado grande para el servidor.';
        } else if (httpErr.status === 502 || httpErr.status === 503 || httpErr.status === 504) {
          msg = 'El servidor no está disponible en este momento. Por favor, inténtalo de nuevo en unos segundos.';
        } else if (httpErr.status === 401 || httpErr.status === 403) {
          msg = 'No tienes permiso para realizar esta acción. Inicia sesión e inténtalo de nuevo.';
        } else if (httpErr.status === 500) {
          msg = 'Error interno del servidor al procesar el archivo. Asegúrate de que el archivo no está dañado.';
        } else if (typeof httpErr.error === 'string' && !httpErr.error.trim().startsWith('<')) {
          msg = httpErr.error;
        } else if (httpErr.message) {
          msg = httpErr.message;
        } else {
          msg = `Error del servidor (código ${httpErr.status ?? 'desconocido'})`;
        }
      }
      this.conversionError.set(`No se pudo convertir el archivo: ${msg}`);
    }
  }

  /** Reintenta la conversión limpiando el mensaje de error anterior. */
  retry(): void {
    this.conversionError.set('');
    this.convert();
  }

  /**
   * Envía el fichero al backend como multipart/form-data para su conversión.
   * Usado para PDF y DOCX. El backend devuelve el texto en formato Markdown.
   * @param file Fichero a convertir.
   * @returns Promesa con el contenido Markdown resultante.
   */
  private convertViaBackend(file: File): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    return firstValueFrom(
      this.http.post('/api/documents/convert', formData, { responseType: 'text' }).pipe(
        timeout(300_000) // 5 minutos máximo
      )
    );
  }

  /**
   * Envía el contenido del fichero de texto al backend como text/plain.
   * El backend aplica un formateo a Markdown y devuelve el resultado.
   * Demuestra el uso de un segundo formato de comunicación asíncrona distinto al multipart.
   * @param file Fichero de texto plano.
   * @param baseName Nombre base del fichero sin extensión (para el título).
   * @returns Promesa con el contenido Markdown resultante.
   */
  private async convertTextViaBackend(file: File, baseName: string): Promise<string> {
    const text = await this.readAsText(file);
    try {
      return await firstValueFrom(
        this.http.post('/api/documents/convert-text', text, {
          headers: { 'Content-Type': 'text/plain; charset=utf-8' },
          responseType: 'text'
        }).pipe(timeout(60_000))
      );
    } catch {
      // Si el endpoint no existe en el servidor, convierte localmente como fallback
      return this.formatAsMarkdown(text, baseName);
    }
  }

  /**
   * Lee un fichero local como texto UTF-8 mediante la API FileReader del navegador.
   * @param file Fichero a leer.
   * @returns Promesa con el contenido del fichero como cadena de texto.
   */
  private readAsText(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => reject(reader.error);
      reader.readAsText(file, 'UTF-8');
    });
  }

  /**
   * Convierte texto plano a formato Markdown agrupando los párrafos por líneas
   * en blanco y detectando posibles títulos mediante heurística.
   * @param text Texto plano de entrada.
   * @param title Título del documento para el encabezado principal (#).
   * @returns Texto formateado en Markdown.
   */
  private formatAsMarkdown(text: string, title: string): string {
    // Split into blocks separated by one or more blank lines
    const blocks = text
      .split(/\r?\n(?:\s*\r?\n)+/)
      .map(block =>
        block.split(/\r?\n/)
          .map(l => l.trim())
          .filter(l => l.length > 0)
          .join(' ')
      )
      .filter(b => b.length > 0);

    const mdLines: string[] = [`# ${title}`, ''];

    for (const block of blocks) {
      if (this.isHeading(block)) {
        mdLines.push(`## ${block}`, '');
      } else {
        mdLines.push(block, '');
      }
    }

    return mdLines.join('\n');
  }

  private isHeading(line: string): boolean {
    if (line.length > 80) return false;
    if (/^(Art[ií]culo|Cap[ií]tulo|Secci[oó]n|T[ií]tulo|Anexo|Parte)\b/i.test(line)) return true;
    if (line === line.toUpperCase() && line.length > 2 && /[A-ZÁÉÍÓÚÑ]/.test(line)) return true;
    if (line.length < 50 && !line.endsWith('.') && !line.endsWith(',') && !line.endsWith(';')) return true;
    return false;
  }
}

