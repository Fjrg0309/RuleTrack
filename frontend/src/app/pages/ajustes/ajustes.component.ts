import { AfterViewInit, Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

/**
 * Componente de ajustes del usuario.
 * Gestiona la información personal, preferencias (idioma, tema),
 * notificaciones y las acciones de zona peligrosa (logout, borrar cuenta).
 */
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

  /**
   * Activa o desactiva el modo oscuro, actualiza el atributo data-theme del
   * documento y persiste la elección en localStorage.
   */
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

  /**
   * Valida los campos de perfil y, si son correctos, envía la actualización
   * al backend. Muestra un toast de éxito o un mensaje de error en el formulario.
   */
  protected saveProfile(): void {
    this.updateError.set('');

    const nombre = this.nombre().trim();
    const email  = this.email().trim();

    // Validar que el nombre no esté vacío
    if (!nombre) {
      this.updateError.set('El nombre no puede estar vacío.');
      return;
    }

    // Validar formato de email con expresión regular estándar
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      this.updateError.set('Introduce un email válido.');
      return;
    }

    this.auth.updateProfile(nombre, email).subscribe({
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
