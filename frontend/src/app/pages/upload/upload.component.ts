import { Component, inject, ElementRef, ViewChild, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FileUploadService } from '../../services/file-upload.service';

/** Extensiones permitidas para la subida de documentos. */
const ALLOWED_EXTENSIONS = ['pdf', 'md', 'doc', 'docx', 'txt'];

/** Tamaño máximo admitido: 10 MB. */
const MAX_SIZE_BYTES = 10 * 1024 * 1024;

/**
 * Componente de subida de documentos.
 * Permite al usuario seleccionar o arrastrar un fichero (PDF, DOCX, MD, TXT)
 * y lo envía al servicio de estado para su posterior conversión.
 */
@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.scss',
})
export class UploadComponent {
  /** Referencia al input oculto de tipo file. */
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);

  isDragging = false;

  /** Mensaje de error de validación del fichero. Vacío si no hay error. */
  fileError = signal('');

  /** Maneja el evento dragover activando el estado visual de arrastre. */
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  /** Desactiva el estado visual de arrastre al salir de la zona. */
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  /** Procesa el fichero soltado sobre la zona de drop. */
  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.processFile(files[0]);
    }
  }

  /** Abre el selector de ficheros del sistema operativo. */
  openFilePicker(): void {
    this.fileInput.nativeElement.click();
  }

  /** Procesa el fichero seleccionado mediante el input de tipo file. */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.processFile(input.files[0]);
    }
  }

  /**
   * Valida la extensión y el tamaño del fichero antes de continuar al flujo
   * de conversión. Si la validación falla, muestra un mensaje de error.
   * @param file Fichero seleccionado por el usuario.
   */
  private processFile(file: File): void {
    this.fileError.set('');

    const ext = file.name.split('.').pop()?.toLowerCase() ?? '';
    if (!ALLOWED_EXTENSIONS.includes(ext)) {
      this.fileError.set(
        `Formato no admitido (.${ext}). Usa: ${ALLOWED_EXTENSIONS.map(e => '.' + e).join(', ')}`
      );
      return;
    }

    if (file.size > MAX_SIZE_BYTES) {
      this.fileError.set(
        `El archivo supera el límite de 10 MB (tamaño actual: ${(file.size / 1024 / 1024).toFixed(1)} MB).`
      );
      return;
    }

    this.fileUploadService.setRawFile(file);
    this.router.navigate(['/converting']);
  }
}
