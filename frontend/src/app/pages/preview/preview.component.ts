import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrl: './preview.component.scss',
})
export class PreviewComponent implements OnInit {
  private router = inject(Router);
  private fileUploadService = inject(FileUploadService);

  fileName = '';
  fileContent = '';

  ngOnInit(): void {
    this.fileName = this.fileUploadService.fileName();
    this.fileContent = this.fileUploadService.fileContent();

    if (!this.fileName) {
      this.router.navigate(['/upload']);
    }
  }

  sendToAgent(): void {
    // TODO: integrate with backend agent
    this.router.navigate(['/organizer']);
  }

  cancel(): void {
    this.fileUploadService.clear();
    this.router.navigate(['/organizer']);
  }
}
