import { AfterViewInit, Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-ajustes',
  templateUrl: './ajustes.component.html',
  styleUrl: './ajustes.component.scss',
})
export class AjustesComponent implements AfterViewInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  protected darkMode = signal(localStorage.getItem('theme') === 'dark');
  protected toggleReady = signal(false);
  protected language = signal('Español');
  protected notifNews = signal(true);
  protected notifEmails = signal(true);
  protected showDeleteConfirm = signal(false);

  protected nombre = signal(this.auth.currentUser()?.nombre ?? '');
  protected email = signal(this.auth.currentUser()?.email ?? '');
  protected updateError = signal('');

  ngAfterViewInit(): void {
    // Habilitar transición solo tras el primer render para evitar animación al cargar
    requestAnimationFrame(() => this.toggleReady.set(true));
  }

  protected toggleDarkMode(): void {
    this.darkMode.update(v => !v);
    const theme = this.darkMode() ? 'dark' : '';
    document.documentElement.setAttribute('data-theme', theme);
    if (this.darkMode()) {
      localStorage.setItem('theme', 'dark');
    } else {
      localStorage.removeItem('theme');
    }
  }

  protected toggleNotifNews(): void {
    this.notifNews.update(v => !v);
  }

  protected toggleNotifEmails(): void {
    this.notifEmails.update(v => !v);
  }

  protected saveProfile(): void {
    this.updateError.set('');
    this.auth.updateProfile(this.nombre(), this.email()).subscribe({
      next: () => this.toast.show('Perfil actualizado correctamente.', 'info'),
      error: (err) => this.updateError.set(err?.error?.message ?? 'Error al actualizar')
    });
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
