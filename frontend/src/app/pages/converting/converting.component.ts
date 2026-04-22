import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-converting',
  imports: [RouterLink],
  templateUrl: './converting.component.html',
  styleUrl: './converting.component.scss',
})
export class ConvertingComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);
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

  private async convert(): Promise<void> {
    const file = this.fileUploadService.rawFile();
    const ext = this.fileName.split('.').pop()?.toLowerCase() ?? '';
    const baseName = this.fileName.replace(/\.[^.]+$/, '');
    const mdName = baseName + '.md';

    // Show animation for at least 2 seconds
    const minDelay = new Promise<void>(resolve => {
      this.timerId = setTimeout(resolve, 2000);
    });

    try {
      let mdContent = '';

      if (ext === 'md' || ext === 'txt') {
        // Plain text / already markdown — read as text
        mdContent = await this.readAsText(file!);
      } else if (ext === 'pdf') {
        const rawText = await this.extractPdfText(file!);
        mdContent = this.formatAsMarkdown(rawText, baseName);
      } else {
        // Generic text fallback
        mdContent = await this.readAsText(file!);
        mdContent = this.formatAsMarkdown(mdContent, baseName);
      }

      await minDelay;
      this.fileUploadService.setFile(mdName, mdContent);
      this.router.navigate(['/preview']);
    } catch {
      await minDelay;
      this.conversionError.set('No se pudo leer el archivo. Prueba con un PDF de texto o un archivo .md / .txt.');
    }
  }

  private readAsText(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => reject(reader.error);
      reader.readAsText(file, 'UTF-8');
    });
  }

  private async extractPdfText(file: File): Promise<string> {
    const pdfjsLib = await import('pdfjs-dist');
    pdfjsLib.GlobalWorkerOptions.workerSrc =
      `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.mjs`;

    const arrayBuffer = await file.arrayBuffer();
    const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
    const pdf = await loadingTask.promise;

    const pages: string[] = [];
    for (let p = 1; p <= pdf.numPages; p++) {
      const page = await pdf.getPage(p);
      const textContent = await page.getTextContent();
      const pageLines = textContent.items
        .map((item: any) => ('str' in item ? item.str : ''))
        .join(' ')
        .replace(/\s{2,}/g, ' ')
        .trim();
      if (pageLines) pages.push(pageLines);
    }

    return pages.join('\n\n');
  }

  private formatAsMarkdown(text: string, title: string): string {
    const lines = text
      .split(/\r?\n/)
      .map(l => l.trim())
      .filter(l => l.length > 0);

    const mdLines: string[] = [`# ${title}`, ''];

    for (const line of lines) {
      if (line.length < 80 && /^[A-ZÁÉÍÓÚÑ\d]/.test(line) && !line.endsWith(',')) {
        // Looks like a heading
        mdLines.push(`## ${line}`, '');
      } else {
        mdLines.push(line, '');
      }
    }

    return mdLines.join('\n');
  }
}

