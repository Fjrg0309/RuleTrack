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
    const name = this.fileUploadService.documentName() || this.fileUploadService.fileName();
    const slug = name
      .replace(/\.[^.]+$/, '')
      .replace(/\s+/g, '-')
      .toLowerCase();
    this.documentUrl.set(`https://ruletrack.app/docs/${slug}`);
  }

  ajustarAcceso(): void {
    // TODO: navigate to access settings page
  }

  volverInicio(): void {
    this.fileUploadService.clear();
    const home = this.auth.currentUser()?.role === 'organizer' ? '/organizer' : '/';
    this.router.navigate([home]);
  }
}
