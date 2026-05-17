import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FileUploadService } from '../../services/file-upload.service';
import { PublicacionesService, VersionDTO } from '../../services/publicaciones.service';
import { AuthService } from '../../services/auth.service';

/**
 * Componente de publicación del documento corregido.
 * Permite introducir metadatos (nombre, versión, visibilidad) y publicar
 * el reglamento como nueva publicación o como nueva versión de una existente.
 */
@Component({
  selector: 'app-corrected',
  imports: [FormsModule],
  templateUrl: './corrected.component.html',
  styleUrl: './corrected.component.scss',
})
export class CorrectedComponent implements OnInit {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);
  private pubService = inject(PublicacionesService);
  private authService = inject(AuthService);

  documentName = '';
  documentVersion = '1.0';
  documentDescripcion = '';
  documentVisibilidad: 'PUBLICO' | 'SOLO_MIEMBROS' | 'PRIVADO' = 'PUBLICO';

  isUpdateMode = false;
  existingVersions: VersionDTO[] = [];
  versionError = signal('');
  isSubmitting = signal(false);

  ngOnInit(): void {
    if (!this.fileUploadService.correctedContent()) {
      this.router.navigate(['/upload']);
      return;
    }

    const publicacionId = this.fileUploadService.publicacionId();
    this.isUpdateMode = publicacionId !== null;

    const savedName = this.fileUploadService.documentName();
    const original = this.fileUploadService.fileName();
    this.documentName = savedName || original.replace(/\.[^.]+$/, '');

    if (this.isUpdateMode && publicacionId) {
      // Pre-cargar nombre y siguiente versión por defecto
      this.pubService.getById(publicacionId).subscribe(pub => {
        if (!savedName) {
          this.documentName = pub.titulo;
        }
        this.documentDescripcion = pub.descripcion ?? '';
        this.documentVisibilidad = pub.visibilidad;
      });

      this.pubService.getVersiones(publicacionId).subscribe(versions => {
        this.existingVersions = versions;
      });

      this.pubService.getSiguienteEtiqueta(publicacionId).subscribe(r => {
        this.documentVersion = r.versionEtiqueta;
      });
    }
  }

  /**
   * Navega a la vista previa del documento corregido tras validar el nombre.
   * Guarda los metadatos en el servicio de estado para usarlos en la vista previa.
   */
  consultarDocumento(): void {
    if (!this.documentName.trim()) {
      this.versionError.set('El nombre del documento no puede estar vacío.');
      return;
    }
    this.versionError.set('');
    this.fileUploadService.setDocumentMeta(this.documentName, this.documentVersion);
    this.router.navigate(['/preview'], { queryParams: { mode: 'corrected' } });
  }

  /**
   * Valida los campos y publica el documento como nuevo reglamento o como nueva
   * versión de uno existente, según el modo de operación (isUpdateMode).
   */
  crearEnlace(): void {
    // Validar nombre no vacío
    if (!this.documentName.trim()) {
      this.versionError.set('El nombre del documento no puede estar vacío.');
      return;
    }

    // Validar formato de versión: X.Y o X.Y.Z (ej. 1.0, 2.3.1)
    const versionRegex = /^\d+\.\d+(\.\d+)?$/;
    if (!versionRegex.test(this.documentVersion.trim())) {
      this.versionError.set('El formato de versión no es válido. Usa X.Y o X.Y.Z (ej. 1.0, 2.3.1).');
      return;
    }

    const user = this.authService.currentUser();
    if (!user) {
      this.versionError.set('Debes iniciar sesión para publicar. Ve a Ajustes e inicia sesión.');
      return;
    }
    if (user.rol !== 'ORGANIZADOR') {
      this.versionError.set('Solo los organizadores pueden publicar reglamentos.');
      return;
    }

    const etiqueta = this.documentVersion.trim();

    // Validar duplicado de versión en modo actualización
    if (this.isUpdateMode) {
      const duplicate = this.existingVersions.some(v => v.versionEtiqueta === etiqueta);
      if (duplicate) {
        this.versionError.set(`Ya existe la versión "${etiqueta}". Elige una etiqueta diferente.`);
        return;
      }
    }

    this.versionError.set('');
    this.isSubmitting.set(true);
    this.fileUploadService.setDocumentMeta(this.documentName, etiqueta);

    const contenido = this.fileUploadService.correctedContent()
      || this.fileUploadService.fileContent();

    const publicacionId = this.fileUploadService.publicacionId();

    if (this.isUpdateMode && publicacionId) {
      // Añadir nueva versión a publicación existente
      this.pubService.crearVersion(publicacionId, { contenido, versionEtiqueta: etiqueta }).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.router.navigate(['/link-created']);
        },
        error: (err) => {
          this.isSubmitting.set(false);
          this.versionError.set(err?.error?.message ?? 'Error al guardar la nueva versión.');
        }
      });
    } else {
      // Crear nueva publicación
      this.pubService.create({
        titulo: this.documentName,
        descripcion: this.documentDescripcion,
        visibilidad: this.documentVisibilidad,
        contenido,
        versionEtiqueta: etiqueta
      }).subscribe({
        next: (pub) => {
          this.fileUploadService.setPublicacionId(pub.id);
          this.isSubmitting.set(false);
          this.router.navigate(['/link-created']);
        },
        error: (err) => {
          this.isSubmitting.set(false);
          if (err?.status === 403) {
            this.versionError.set('Sin permiso. Asegúrate de haber iniciado sesión como organizador.');
          } else {
            this.versionError.set(err?.error?.message ?? 'Error al crear la publicación.');
          }
        }
      });
    }
  }
}

