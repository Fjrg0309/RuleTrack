import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-corrected',
  imports: [FormsModule],
  templateUrl: './corrected.component.html',
  styleUrl: './corrected.component.scss',
})
export class CorrectedComponent implements OnInit {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);

  documentName = '';
  documentVersion = '1.0';

  ngOnInit(): void {
    if (!this.fileUploadService.correctedContent()) {
      this.router.navigate(['/upload']);
      return;
    }
    const original = this.fileUploadService.fileName();
    this.documentName = original.replace(/\.[^.]+$/, '-corregido');
  }

  consultarDocumento(): void {
    this.fileUploadService.setDocumentMeta(this.documentName, this.documentVersion);
    this.router.navigate(['/preview'], { queryParams: { mode: 'corrected' } });
  }

  crearEnlace(): void {
    this.fileUploadService.setDocumentMeta(this.documentName, this.documentVersion);
    this.router.navigate(['/link-created']);
  }
}
