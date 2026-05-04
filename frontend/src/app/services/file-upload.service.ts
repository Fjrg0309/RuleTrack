import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  readonly fileName = signal<string>('');
  readonly fileContent = signal<string>('');
  readonly rawFile = signal<File | null>(null);
  readonly correctedContent = signal<string>('');
  readonly documentName = signal<string>('');
  readonly documentVersion = signal<string>('1.0');

  /** ID del reglamento existente cuando se está actualizando una publicación. null = nueva publicación. */
  readonly publicacionId = signal<number | null>(null);

  setFile(name: string, content: string): void {
    this.fileName.set(name);
    this.fileContent.set(content);
  }

  setRawFile(file: File): void {
    this.rawFile.set(file);
    this.fileName.set(file.name);
    this.fileContent.set('');
  }

  setCorrectedContent(content: string): void {
    this.correctedContent.set(content);
  }

  setDocumentMeta(name: string, version: string): void {
    this.documentName.set(name);
    this.documentVersion.set(version);
  }

  setPublicacionId(id: number | null): void {
    this.publicacionId.set(id);
  }

  clear(): void {
    this.fileName.set('');
    this.fileContent.set('');
    this.rawFile.set(null);
    this.correctedContent.set('');
    this.documentName.set('');
    this.documentVersion.set('1.0');
    this.publicacionId.set(null);
  }
}
