import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  readonly fileName = signal<string>('');
  readonly fileContent = signal<string>('');
  readonly rawFile = signal<File | null>(null);

  setFile(name: string, content: string): void {
    this.fileName.set(name);
    this.fileContent.set(content);
  }

  setRawFile(file: File): void {
    this.rawFile.set(file);
    this.fileName.set(file.name);
    this.fileContent.set('');
  }

  clear(): void {
    this.fileName.set('');
    this.fileContent.set('');
    this.rawFile.set(null);
  }
}
