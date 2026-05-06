import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, VersionDTO } from '../../services/publicaciones.service';
import { FileUploadService } from '../../services/file-upload.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-ajustes-publicacion',
  imports: [FormsModule],
  templateUrl: './ajustes-publicacion.component.html',
  styleUrl: './ajustes-publicacion.component.scss',
})
export class AjustesPublicacionComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pubService = inject(PublicacionesService);
  private fileUploadService = inject(FileUploadService);
  private toast = inject(ToastService);

  protected reglamentoId: number | null = null;

  protected titulo = signal('');
  protected descripcion = signal('');
  protected visibilidad = signal<'PUBLICO' | 'SOLO_MIEMBROS' | 'PRIVADO'>('PUBLICO');
  protected url = signal('');
  protected versiones = signal<VersionDTO[]>([]);
  protected selectedVersionId = signal<number | null>(null);

  protected showDeleteConfirm = signal(false);
  protected errorMsg = signal('');

  /** La versión actualmente seleccionada en el dropdown */
  protected get selectedVersion(): VersionDTO | undefined {
    return this.versiones().find(v => v.id === this.selectedVersionId());
  }

  /** Solo se puede actualizar si la versión seleccionada es PUBLICADO */
  protected get canUpdate(): boolean {
    return this.selectedVersion?.estado === 'PUBLICADO';
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = params['id'] ? Number(params['id']) : null;
      this.reglamentoId = id;
      if (id) {
        this.url.set(`${window.location.origin}/view/${id}`);
        this.loadData(id);
      }
    });
  }

  private loadData(id: number): void {
    this.pubService.getById(id).subscribe(pub => {
      this.titulo.set(pub.titulo);
      this.descripcion.set(pub.descripcion ?? '');
      this.visibilidad.set(pub.visibilidad);
    });

    this.pubService.getVersiones(id).subscribe(versions => {
      this.versiones.set(versions);
      const active = versions.find(v => v.estado === 'PUBLICADO');
      if (active) this.selectedVersionId.set(active.id);
      else if (versions.length > 0) this.selectedVersionId.set(versions[versions.length - 1].id);
    });
  }

  protected onVersionChange(versionId: number): void {
    this.selectedVersionId.set(versionId);
    // Activar si no es PUBLICADO
    const v = this.versiones().find(ver => ver.id === versionId);
    if (v && v.estado !== 'PUBLICADO' && this.reglamentoId) {
      this.pubService.activarVersion(versionId).subscribe({
        next: () => {
          // Recargar versiones
          this.pubService.getVersiones(this.reglamentoId!).subscribe(versions => {
            this.versiones.set(versions);
          });
        },
        error: (err) => this.errorMsg.set(err?.error?.message ?? 'Error al activar versión.')
      });
    }
  }

  protected saveChanges(): void {
    if (!this.reglamentoId) return;
    this.pubService.update(this.reglamentoId, {
      titulo: this.titulo(),
      descripcion: this.descripcion(),
      visibilidad: this.visibilidad()
    }).subscribe({
      next: () => this.toast.show('Cambios guardados correctamente.', 'info'),
      error: (err) => this.errorMsg.set(err?.error?.message ?? 'Error al guardar cambios.')
    });
  }

  protected actualizarVersion(): void {
    if (!this.canUpdate || !this.reglamentoId) return;
    const version = this.selectedVersion;
    if (!version?.contenido) return;
    this.fileUploadService.setPublicacionId(this.reglamentoId);
    this.fileUploadService.setFile(this.titulo(), version.contenido);
    this.router.navigate(['/preview']);
  }

  protected confirmDelete(): void {
    this.showDeleteConfirm.set(true);
  }

  protected cancelDelete(): void {
    this.showDeleteConfirm.set(false);
  }

  protected deletePublication(): void {
    if (!this.reglamentoId) {
      this.router.navigate(['/publicaciones']);
      return;
    }
    this.pubService.delete(this.reglamentoId).subscribe({
      next: () => {
        this.fileUploadService.setPublicacionId(null);
        this.showDeleteConfirm.set(false);
        this.router.navigate(['/publicaciones']);
      },
      error: (err) => {
        this.showDeleteConfirm.set(false);
        this.errorMsg.set(err?.error?.message ?? 'Error al borrar la publicación.');
      }
    });
  }
}

