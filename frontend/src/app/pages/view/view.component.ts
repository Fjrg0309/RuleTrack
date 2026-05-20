import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ModalComponent } from '../../components/modal/modal.component';
import { PublicacionesService, ResumenEstructuradoDTO, VersionPublicaDTO } from '../../services/publicaciones.service';
import { marked } from 'marked';

export interface TocItem {
  id: string;
  text: string;
  level: number;
}

export interface DiffLine {
  type: 'added' | 'removed' | 'unchanged' | 'separator';
  text: string;
  count?: number;
}

export type ViewMode = 'normas' | 'resumen' | 'historial';

@Component({
  selector: 'app-view',
  imports: [ModalComponent, FormsModule],
  templateUrl: './view.component.html',
  styleUrl: './view.component.scss',
})
export class ViewComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);

  titulo = '';
  contenido = '';
  isLoading = true;
  errorMsg = '';
  downloadModalVisible = false;
  private reglamentoId = 0;

  // Mode
  mode = signal<ViewMode>('normas');

  // TOC
  tocItems: TocItem[] = [];
  activeTocId = signal('');
  private scrollCleanup: (() => void) | null = null;

  // Rendered HTML
  private baseHtml = '';
  displayHtml = signal('');

  // Search
  searchQuery = signal('');
  searchResultCount = signal(0);

  // Resumen
  resumen = signal<ResumenEstructuradoDTO | null>(null);
  isLoadingResumen = signal(false);
  resumenError = signal('');

  // Historial
  versiones = signal<VersionPublicaDTO[]>([]);
  isLoadingVersiones = signal(false);
  versionesError = signal('');
  selectedVersionIdx = signal<number>(0);
  diffLines = signal<DiffLine[]>([]);
  showFullContent = signal(false);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/']);
      return;
    }
    this.reglamentoId = id;

    this.pubService.getPublicoView(id).subscribe({
      next: (data) => {
        this.titulo = data.titulo;
        this.contenido = data.contenido;
        this.processMarkdown();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Documento no encontrado o no disponible públicamente.';
        this.isLoading = false;
      },
    });
  }

  // ── Markdown processing ───────────────────────────────────────────────

  private processMarkdown(): void {
    const html = marked.parse(this.contenido, { async: false }) as string;
    this.buildToc();
    this.baseHtml = html;
    this.displayHtml.set(html);
    // Wait one tick for Angular to render [innerHTML], then fix IDs and wire scroll
    setTimeout(() => {
      this.injectHeadingIdsToDom();
      this.setupTocScrollListener();
    }, 50);
  }

  private buildToc(): void {
    const regex = /^(#{1,6})\s+(.+)$/gm;
    this.tocItems = [];
    const idCount = new Map<string, number>();
    let match;
    while ((match = regex.exec(this.contenido)) !== null) {
      const level = match[1].length;
      if (level > 3) continue; // only h1-h3 in TOC
      const text = match[2].trim().replace(/\*\*|__|\[|\]|~~|`/g, '');
      const baseId =
        'rt-' +
        text
          .toLowerCase()
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '')
          .replace(/[^a-z0-9\s-]/g, '')
          .trim()
          .replace(/\s+/g, '-')
          .substring(0, 60);
      const count = idCount.get(baseId) ?? 0;
      idCount.set(baseId, count + 1);
      const id = count === 0 ? baseId : `${baseId}-${count + 1}`;
      this.tocItems.push({ id, level, text });
    }
  }

  /**
   * Assigns our IDs directly to DOM heading elements in document order.
   * This is robust against any IDs that `marked` may already have injected.
   */
  private injectHeadingIdsToDom(): void {
    const article = document.querySelector('.view__markdown') as HTMLElement | null;
    if (!article || !this.tocItems.length) return;
    const domHeadings = Array.from(article.querySelectorAll('h1, h2, h3'));
    // tocItems and DOM headings are produced from the same source in the same order
    domHeadings.forEach((el, i) => {
      if (this.tocItems[i]) el.id = this.tocItems[i].id;
    });
  }

  scrollToHeading(id: string): void {
    this.activeTocId.set(id);
    const el = document.getElementById(id);
    if (!el) return;
    const article = document.querySelector('.view__markdown') as HTMLElement | null;
    if (article) {
      const elTop = el.getBoundingClientRect().top - article.getBoundingClientRect().top + article.scrollTop;
      article.scrollTo({ top: elTop - 16, behavior: 'smooth' });
    } else {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  private setupTocScrollListener(): void {
    this.scrollCleanup?.();
    this.scrollCleanup = null;
    const article = document.querySelector('.view__markdown') as HTMLElement | null;
    if (!article || !this.tocItems.length) return;
    const headingEls = this.tocItems
      .map(item => document.getElementById(item.id))
      .filter((el): el is HTMLElement => el !== null);
    if (!headingEls.length) return;
    this.activeTocId.set(headingEls[0].id);
    const onScroll = () => {
      const articleTop = article.getBoundingClientRect().top;
      let activeId = headingEls[0].id;
      for (const el of headingEls) {
        if (el.getBoundingClientRect().top - articleTop <= 60) {
          activeId = el.id;
        } else {
          break;
        }
      }
      this.activeTocId.set(activeId);
    };
    article.addEventListener('scroll', onScroll, { passive: true });
    this.scrollCleanup = () => article.removeEventListener('scroll', onScroll);
  }

  // ── Mode ──────────────────────────────────────────────────────────────

  setMode(m: ViewMode): void {
    this.mode.set(m);
    if (m === 'resumen' && !this.resumen() && !this.isLoadingResumen()) {
      this.loadResumen();
    }
    if (m === 'historial' && this.versiones().length === 0 && !this.isLoadingVersiones()) {
      this.loadVersiones();
    }
  }

  // ── Search ────────────────────────────────────────────────────────────

  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    if (!query.trim()) {
      this.displayHtml.set(this.baseHtml);
      this.searchResultCount.set(0);
      setTimeout(() => this.injectHeadingIdsToDom(), 0);
      return;
    }
    const escaped = query.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    let count = 0;
    const highlighted = this.baseHtml.replace(
      new RegExp(`(?![^<]*>)(${escaped})`, 'gi'),
      (match) => {
        count++;
        return `<mark class="search-highlight">${match}</mark>`;
      },
    );
    this.searchResultCount.set(count);
    this.displayHtml.set(highlighted);
    setTimeout(() => this.injectHeadingIdsToDom(), 0);
  }

  clearSearch(): void {
    this.searchQuery.set('');
    this.searchResultCount.set(0);
    this.displayHtml.set(this.baseHtml);
    setTimeout(() => this.injectHeadingIdsToDom(), 0);
  }

  // ── Resumen ───────────────────────────────────────────────────────────

  private loadResumen(): void {
    this.isLoadingResumen.set(true);
    this.resumenError.set('');
    this.pubService.getResumenPublico(this.reglamentoId).subscribe({
      next: (data) => {
        this.resumen.set(data);
        this.isLoadingResumen.set(false);
      },
      error: () => {
        this.resumenError.set('No se pudo generar el resumen. Inténtelo de nuevo.');
        this.isLoadingResumen.set(false);
      },
    });
  }

  retryResumen(): void {
    this.resumen.set(null);
    this.loadResumen();
  }

  // ── Historial ─────────────────────────────────────────────────────────

  private loadVersiones(): void {
    this.isLoadingVersiones.set(true);
    this.versionesError.set('');
    this.pubService.getVersionesPublicas(this.reglamentoId).subscribe({
      next: (data) => {
        this.versiones.set(data);
        this.isLoadingVersiones.set(false);
        if (data.length > 0) {
          this.selectVersion(0);
        }
      },
      error: () => {
        this.versionesError.set('No se pudo cargar el historial de versiones.');
        this.isLoadingVersiones.set(false);
      },
    });
  }

  selectVersion(idx: number): void {
    const versions = this.versiones();
    this.selectedVersionIdx.set(idx);
    const current = versions[idx];
    const prev = versions[idx + 1]; // newest-first; idx+1 is older
    if (!prev) {
      this.showFullContent.set(true);
      this.diffLines.set([]);
    } else {
      this.showFullContent.set(false);
      const raw = this.computeLineDiff(prev.contenido, current.contenido);
      this.diffLines.set(this.condenseDiff(raw));
    }
  }

  private computeLineDiff(oldText: string, newText: string): DiffLine[] {
    const oldLines = oldText.split('\n');
    const newLines = newText.split('\n');
    const n = oldLines.length;
    const m = newLines.length;

    const dp: number[][] = Array.from({ length: n + 1 }, () => new Array(m + 1).fill(0));
    for (let i = 1; i <= n; i++) {
      for (let j = 1; j <= m; j++) {
        dp[i][j] =
          oldLines[i - 1] === newLines[j - 1]
            ? dp[i - 1][j - 1] + 1
            : Math.max(dp[i - 1][j], dp[i][j - 1]);
      }
    }

    const result: DiffLine[] = [];
    let i = n, j = m;
    while (i > 0 || j > 0) {
      if (i > 0 && j > 0 && oldLines[i - 1] === newLines[j - 1]) {
        result.unshift({ type: 'unchanged', text: oldLines[i - 1] });
        i--; j--;
      } else if (j > 0 && (i === 0 || dp[i][j - 1] >= dp[i - 1][j])) {
        result.unshift({ type: 'added', text: newLines[j - 1] });
        j--;
      } else {
        result.unshift({ type: 'removed', text: oldLines[i - 1] });
        i--;
      }
    }
    return result;
  }

  private condenseDiff(lines: DiffLine[], ctx = 3): DiffLine[] {
    const n = lines.length;
    const show = new Uint8Array(n);
    for (let i = 0; i < n; i++) {
      if (lines[i].type !== 'unchanged') {
        for (let k = Math.max(0, i - ctx); k <= Math.min(n - 1, i + ctx); k++) {
          show[k] = 1;
        }
      }
    }
    const result: DiffLine[] = [];
    let hiddenCount = 0;
    for (let i = 0; i < n; i++) {
      if (show[i]) {
        if (hiddenCount > 0) {
          result.push({ type: 'separator', text: `${hiddenCount} línea${hiddenCount === 1 ? '' : 's'} sin cambios`, count: hiddenCount });
          hiddenCount = 0;
        }
        result.push(lines[i]);
      } else {
        hiddenCount++;
      }
    }
    if (hiddenCount > 0 && result.length > 0) {
      result.push({ type: 'separator', text: `${hiddenCount} línea${hiddenCount === 1 ? '' : 's'} sin cambios`, count: hiddenCount });
    }
    return result;
  }

  get selectedVersion(): VersionPublicaDTO | null {
    return this.versiones()[this.selectedVersionIdx()] ?? null;
  }

  get prevVersion(): VersionPublicaDTO | null {
    return this.versiones()[this.selectedVersionIdx() + 1] ?? null;
  }

  hasChanges(): boolean {
    return this.diffLines().some(l => l.type === 'added' || l.type === 'removed');
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('es-ES', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  }

  // ── Download ──────────────────────────────────────────────────────────

  openDownloadModal(): void {
    this.downloadModalVisible = true;
  }

  onDownloadFormat(format: 'md' | 'pdf'): void {
    this.downloadModalVisible = false;
    if (format === 'md') {
      const blob = new Blob([this.contenido], { type: 'text/markdown' });
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `${this.titulo}.md`;
      a.click();
      URL.revokeObjectURL(a.href);
    } else {
      PublicacionesService.printAsPdf(this.titulo, this.contenido);
    }
  }

  onDownloadCancelled(): void {
    this.downloadModalVisible = false;
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.scrollCleanup?.();
  }
}
