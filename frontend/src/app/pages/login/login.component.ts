import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  error = signal('');
  loading = signal(false);

  submit(): void {
    if (!this.username || !this.password) {
      this.error.set('Por favor, completa todos los campos.');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.loading.set(false);
        const rol = this.authService.currentUser()?.rol;
        this.router.navigate([rol === 'ORGANIZADOR' ? '/organizer' : '/']);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Usuario o contraseña incorrectos.');
      },
    });
  }
}

