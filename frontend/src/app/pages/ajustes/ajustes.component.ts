import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ajustes',
  templateUrl: './ajustes.component.html',
  styleUrl: './ajustes.component.scss',
})
export class AjustesComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  protected darkMode = signal(false);
  protected language = signal('Español');
  protected notifNews = signal(true);
  protected notifEmails = signal(true);
  protected showDeleteConfirm = signal(false);

  protected toggleDarkMode(): void {
    this.darkMode.update(v => !v);
    document.documentElement.setAttribute('data-theme', this.darkMode() ? 'dark' : '');
  }

  protected toggleNotifNews(): void {
    this.notifNews.update(v => !v);
  }

  protected toggleNotifEmails(): void {
    this.notifEmails.update(v => !v);
  }

  protected logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  protected confirmDelete(): void {
    this.showDeleteConfirm.set(true);
  }

  protected cancelDelete(): void {
    this.showDeleteConfirm.set(false);
  }

  protected deleteAccount(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
