import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-preview',
  imports: [],
  templateUrl: './preview.component.html',
  styleUrl: './preview.component.scss',
})
export class PreviewComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private fileUploadService = inject(FileUploadService);

  fileName = '';
  fileContent = '';
  isAfterCorrection = false;

  ngOnInit(): void {
    const mode = this.route.snapshot.queryParamMap.get('mode');
    this.isAfterCorrection = mode === 'corrected';

    if (this.isAfterCorrection) {
      this.fileName = this.fileUploadService.documentName() || this.fileUploadService.fileName();
      this.fileContent = this.fileUploadService.correctedContent() || this.fileUploadService.fileContent();
    } else {
      this.fileName = this.fileUploadService.fileName();
      this.fileContent = this.fileUploadService.fileContent();
    }

    if (!this.fileName) {
      this.router.navigate(['/upload']);
    }
  }

  correctDocument(): void {
    if (this.isAfterCorrection) {
      // Update fileContent with corrected version for next round
      this.fileUploadService.setFile(this.fileName, this.fileContent);
    }
    this.router.navigate(['/correcting']);
  }

  cancel(): void {
    this.fileUploadService.clear();
    this.router.navigate(['/']);
  }
}
