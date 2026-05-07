import { Injectable, signal } from '@angular/core';

/**
 * Servicio de estado compartido para el flujo de carga y corrección de ficheros.
 * Actúa como almacén temporal entre los pasos del proceso de publicación.
 */
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

  /**
   * Almacena el nombre y el contenido en texto plano de un fichero ya convertido.
   * @param name Nombre del fichero (con extensión .md).
   * @param content Contenido del fichero en formato Markdown.
   */
  setFile(name: string, content: string): void {
    this.fileName.set(name);
    this.fileContent.set(content);
  }

  /**
   * Almacena el fichero original seleccionado por el usuario antes de la conversión.
   * @param file Fichero nativo del sistema de ficheros.
   */
  setRawFile(file: File): void {
    this.rawFile.set(file);
    this.fileName.set(file.name);
    this.fileContent.set('');
  }

  /**
   * Almacena el contenido del documento tras aplicar las correcciones de la IA.
   * @param content Contenido corregido en Markdown.
   */
  setCorrectedContent(content: string): void {
    this.correctedContent.set(content);
  }

  /**
   * Guarda el título y la etiqueta de versión del documento para usarlos al publicar.
   * @param name Título del documento.
   * @param version Etiqueta de versión (ej. '1.0').
   */
  setDocumentMeta(name: string, version: string): void {
    this.documentName.set(name);
    this.documentVersion.set(version);
  }

  /**
   * Guarda el ID de una publicación existente para el modo de actualización de versión.
   * @param id ID de la publicación, o null para modo de nueva creación.
   */
  setPublicacionId(id: number | null): void {
    this.publicacionId.set(id);
  }

  /** Limpia todos los signals del servicio volviendo al estado inicial. */
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
