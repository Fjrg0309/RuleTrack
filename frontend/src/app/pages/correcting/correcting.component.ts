import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { FileUploadService } from '../../services/file-upload.service';
import { CorrectionService, CorrectionItem } from '../../services/correction.service';

export interface TextSegment {
  type: 'text' | 'correction' | 'applied';
  content: string;
  correctionId?: string;
}

export interface CorrectionState {
  item: CorrectionItem;
  status: 'pending' | 'applied' | 'rejected' | 'excluded';
}

@Component({
  selector: 'app-correcting',
  imports: [FormsModule],
  templateUrl: './correcting.component.html',
  styleUrl: './correcting.component.scss',
})
export class CorrectingComponent implements OnInit {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);
  private correctionService = inject(CorrectionService);

  isLoading = signal(true);
  errorMessage = signal('');
  corrections = signal<CorrectionState[]>([]);
  segments = signal<TextSegment[]>([]);
  activeCorrection = signal<CorrectionState | null>(null);

  modalSuggestion = '';
  noCorrectMore = false;
  showJustification = false;

  get fileName(): string { return this.fileUploadService.fileName(); }
  get fileContent(): string { return this.fileUploadService.fileContent(); }
  get pendingCount(): number {
    return this.corrections().filter(s => s.status === 'pending').length;
  }

  async ngOnInit(): Promise<void> {
    if (!this.fileContent) {
      this.router.navigate(['/upload']);
      return;
    }

    try {
      const items = await this.correctionService.analyzeDocument(this.fileContent);
      const states: CorrectionState[] = items.map(item => ({ item, status: 'pending' }));
      this.corrections.set(states);
      this.buildSegments(states);
    } catch (err) {
      const status = (err instanceof HttpErrorResponse) ? err.status : 0;
      if (status === 503) {
        this.errorMessage.set(
          'El agente de IA no está configurado. Configura la variable LLM_API_KEY con tu clave y reinicia el servidor.'
        );
      } else if (status === 0) {
        this.errorMessage.set(
          'No se pudo conectar con el servidor. Asegúrate de que el backend está en ejecución.'
        );
      } else {
        this.errorMessage.set(
          `Error del servidor (${status}). Comprueba los logs del backend.`
        );
      }
    } finally {
      this.isLoading.set(false);
    }
  }

  private buildSegments(states: CorrectionState[]): void {
    const text = this.fileContent;
    const activeStates = states.filter(s => s.status === 'pending' || s.status === 'applied');

    interface Match { start: number; end: number; correctionId: string; status: 'pending' | 'applied'; suggestion: string; }
    const matches: Match[] = [];

    for (const state of activeStates) {
      const idx = text.indexOf(state.item.original);
      if (idx !== -1) {
        matches.push({
          start: idx,
          end: idx + state.item.original.length,
          correctionId: state.item.id,
          status: state.status as 'pending' | 'applied',
          suggestion: state.item.suggestion,
        });
      }
    }

    matches.sort((a, b) => a.start - b.start);

    const segments: TextSegment[] = [];
    let cursor = 0;

    for (const match of matches) {
      if (match.start < cursor) continue;
      if (match.start > cursor) {
        segments.push({ type: 'text', content: text.substring(cursor, match.start) });
      }
      segments.push({
        type: match.status === 'applied' ? 'applied' : 'correction',
        content: match.status === 'applied' ? match.suggestion : text.substring(match.start, match.end),
        correctionId: match.correctionId,
      });
      cursor = match.end;
    }

    if (cursor < text.length) {
      segments.push({ type: 'text', content: text.substring(cursor) });
    }

    this.segments.set(segments);
  }

  openModal(correctionId: string): void {
    const state = this.corrections().find(s => s.item.id === correctionId);
    if (state) {
      this.activeCorrection.set(state);
      this.modalSuggestion = state.item.suggestion;
      this.noCorrectMore = false;
      this.showJustification = false;
    }
  }

  closeModal(): void {
    this.activeCorrection.set(null);
  }

  applyCorrection(): void {
    const state = this.activeCorrection();
    if (!state) return;
    const updatedItem: CorrectionItem = { ...state.item, suggestion: this.modalSuggestion };
    this.corrections.update(list =>
      list.map(s => s.item.id === state.item.id ? { item: updatedItem, status: 'applied' as const } : s)
    );
    this.buildSegments(this.corrections());
    this.closeModal();
  }

  rejectCorrection(): void {
    const state = this.activeCorrection();
    if (!state) return;
    const newStatus = this.noCorrectMore ? 'excluded' as const : 'rejected' as const;
    this.corrections.update(list =>
      list.map(s => s.item.id === state.item.id ? { ...s, status: newStatus } : s)
    );
    this.buildSegments(this.corrections());
    this.closeModal();
  }

  applyAllCorrections(): void {
    let correctedText = this.fileContent;
    const toApply = this.corrections().filter(s => s.status === 'pending' || s.status === 'applied');

    // Apply in reverse order by position to preserve indices
    const sorted = [...toApply].sort((a, b) => {
      const posA = correctedText.lastIndexOf(a.item.original);
      const posB = correctedText.lastIndexOf(b.item.original);
      return posB - posA;
    });

    for (const state of sorted) {
      correctedText = correctedText.replace(state.item.original, state.item.suggestion);
    }

    this.fileUploadService.setCorrectedContent(correctedText);
    this.router.navigate(['/corrected']);
  }

  ignoreCorrections(): void {
    this.router.navigate(['/preview']);
  }
}
