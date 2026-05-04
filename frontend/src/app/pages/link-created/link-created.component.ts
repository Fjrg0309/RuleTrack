import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FileUploadService } from '../../services/file-upload.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-link-created',
  templateUrl: './link-created.component.html',
  styleUrl: './link-created.component.scss',
})
export class LinkCreatedComponent implements OnInit {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);
  private auth = inject(AuthService);

  documentUrl = signal('');

  ngOnInit(): void {
    const id = this.fileUploadService.publicacionId();
    if (id) {
      this.documentUrl.set(`${window.location.origin}/view/${id}`);
    } else {
      this.documentUrl.set(`${window.location.origin}/publicaciones`);
    }
  }

  verDocumento(): void {
    const id = this.fileUploadService.publicacionId();
    if (id) {
      this.router.navigate(['/view', id]);
    }
  }

  ajustarAcceso(): void {
    const id = this.fileUploadService.publicacionId();
    if (id) {
      this.router.navigate(['/ajustes-publicacion'], { queryParams: { id } });
    } else {
      this.router.navigate(['/ajustes-publicacion']);
    }
  }

  volverInicio(): void {
    this.fileUploadService.clear();
    const home = this.auth.currentUser()?.rol === 'ORGANIZADOR' ? '/organizer' : '/';
    this.router.navigate([home]);
  }
}
