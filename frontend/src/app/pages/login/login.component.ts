import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  error = signal('');

  submit(): void {
    if (!this.username || !this.password) {
      this.error.set('Por favor, completa todos los campos.');
      return;
    }
    const ok = this.authService.login(this.username, this.password);
    if (ok) {
      const role = this.authService.currentUser()?.role;
      this.router.navigate([role === 'organizer' ? '/organizer' : '/']);
    } else {
      this.error.set('Usuario o contraseña incorrectos.');
    }
  }
}
