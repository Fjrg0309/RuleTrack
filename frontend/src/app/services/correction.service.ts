import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export interface CorrectionItem {
  id: string;
  original: string;
  suggestion: string;
  explanation: string;
}

export interface CorrectionResponse {
  corrections: CorrectionItem[];
}

@Injectable({ providedIn: 'root' })
export class CorrectionService {
  private http = inject(HttpClient);
  private readonly apiUrl = '/api/corrections';

  analyzeDocument(content: string): Promise<CorrectionItem[]> {
    return firstValueFrom(
      this.http.post<CorrectionResponse>(`${this.apiUrl}/analyze`, { content })
    ).then(r => r.corrections);
  }
}
