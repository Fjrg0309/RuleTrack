import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast-outlet',
  standalone: true,
  templateUrl: './toast-outlet.component.html',
  styleUrl: './toast-outlet.component.scss',
})
export class ToastOutletComponent {
  readonly toastService = inject(ToastService);
  private router = inject(Router);

  navigate(link: string | undefined, toastId: number): void {
    if (!link) return;
    this.toastService.dismiss(toastId);
    this.router.navigateByUrl(link);
  }
}
