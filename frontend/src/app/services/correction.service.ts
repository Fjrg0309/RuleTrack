import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, timeout } from 'rxjs';

/** Representa una corrección individual sugerida por la IA. */
export interface CorrectionItem {
  id: string;
  original: string;
  suggestion: string;
  explanation: string;
}

/** Respuesta completa del endpoint de análisis de correcciones. */
export interface CorrectionResponse {
  corrections: CorrectionItem[];
}

/**
 * Servicio que se comunica con el endpoint de IA para analizar y sugerir
 * correcciones sobre el contenido de un documento.
 */
@Injectable({ providedIn: 'root' })
export class CorrectionService {
  private http = inject(HttpClient);
  private readonly apiUrl = '/api/corrections';

  /**
   * Envía el contenido del documento al backend para su análisis y devuelve
   * la lista de correcciones sugeridas por la IA.
   * @param content Texto en Markdown del documento a analizar.
   * @returns Promesa con el array de correcciones.
   */
  analyzeDocument(content: string): Promise<CorrectionItem[]> {
    return firstValueFrom(
      this.http.post<CorrectionResponse>(`${this.apiUrl}/analyze`, { content }).pipe(
        timeout(20_000)
      )
    ).then(r => r.corrections);
  }
}
