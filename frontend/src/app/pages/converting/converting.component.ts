import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
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
      let mdContent: string;

      if (ext === 'pdf') {
        // Convert via backend (Apache PDFBox) – works in all environments including Docker
        mdContent = await this.convertViaBackend(file!);
      } else if (ext === 'md') {
        mdContent = await this.readAsText(file!);
      } else {
        // txt or other text formats
        const text = await this.readAsText(file!);
        mdContent = this.formatAsMarkdown(text, baseName);
      }

      await minDelay;
      this.fileUploadService.setFile(mdName, mdContent);
      this.router.navigate(['/preview']);
    } catch {
      await minDelay;
      this.conversionError.set('No se pudo leer el archivo. Prueba con un PDF de texto o un archivo .md / .txt.');
    }
  }

  private convertViaBackend(file: File): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    return firstValueFrom(
      this.http.post('/api/documents/convert', formData, { responseType: 'text' })
    );
  }

  private readAsText(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => reject(reader.error);
      reader.readAsText(file, 'UTF-8');
    });
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

