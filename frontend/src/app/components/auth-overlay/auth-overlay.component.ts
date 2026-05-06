import { Component, ElementRef, inject, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthModalService } from '../../services/auth-modal.service';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-auth-overlay',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './auth-overlay.component.html',
  styleUrl: './auth-overlay.component.scss',
})
export class AuthOverlayComponent {
  protected authModal = inject(AuthModalService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  @ViewChild('card') cardRef!: ElementRef<HTMLElement>;

  // ── Login ──
  loginForm = { username: '', password: '' };
  loginError = signal('');
  loginLoading = signal(false);

  submitLogin(): void {
    const { username, password } = this.loginForm;
    if (!username || !password) { this.loginError.set('Por favor, completa todos los campos.'); return; }
    this.loginLoading.set(true);
    this.loginError.set('');
    this.auth.login(username, password).subscribe({
      next: () => {
        this.loginLoading.set(false);
        this.authModal.close();
        this.loginForm = { username: '', password: '' };
        const rol = this.auth.currentUser()?.rol;
        this.router.navigate([rol === 'ORGANIZADOR' ? '/organizer' : '/']);
      },
      error: () => {
        this.loginLoading.set(false);
        this.loginError.set('Usuario o contraseña incorrectos.');
      },
    });
  }

  // ── Registro ──
  regForm = {
    username: '', nombre: '', apellidos: '', fechaNacimiento: '',
    email: '', dni: '', password: '', rol: 'USUARIO' as 'USUARIO' | 'ORGANIZADOR',
    organizacionNombre: '',
  };
  regError = signal('');
  regErrors = signal<Record<string, string>>({});
  regLoading = signal(false);
  orgMode = signal<'unirse' | 'crear'>('unirse');
  orgExiste = signal<boolean | null>(null);
  checkingOrg = signal(false);

  inputClass(field: string): string {
    return 'auth-overlay__input' + (this.regErrors()[field] ? ' auth-overlay__input--error' : '');
  }

  setOrgMode(mode: 'unirse' | 'crear', event: Event): void {
    event.preventDefault();
    this.orgMode.set(mode);
    this.orgExiste.set(null);
    this.regErrors.update(e => { const n = { ...e }; delete n['organizacionNombre']; return n; });
  }

  onOrgBlur(): void {
    if (this.orgMode() === 'crear') return;
    const nombre = this.regForm.organizacionNombre?.trim();
    if (!nombre) return;
    this.checkingOrg.set(true);
    this.http.get<{ existe: boolean }>(`/api/auth/organizacion/existe?nombre=${encodeURIComponent(nombre)}`)
      .subscribe({
        next: (res) => {
          this.orgExiste.set(res.existe);
          this.checkingOrg.set(false);
          if (!res.existe) {
            this.regErrors.update(e => ({ ...e, organizacionNombre: 'Esta organización no existe. Puedes crearla usando el enlace de abajo.' }));
          } else {
            this.regErrors.update(e => { const n = { ...e }; delete n['organizacionNombre']; return n; });
          }
        },
        error: () => this.checkingOrg.set(false),
      });
  }

  private calcularEdad(fecha: string): number {
    const hoy = new Date(); const nac = new Date(fecha);
    let edad = hoy.getFullYear() - nac.getFullYear();
    const m = hoy.getMonth() - nac.getMonth();
    if (m < 0 || (m === 0 && hoy.getDate() < nac.getDate())) edad--;
    return edad;
  }

  private validate(): boolean {
    const errors: Record<string, string> = {};
    const f = this.regForm;
    if (!f.nombre?.trim()) errors['nombre'] = 'El nombre es obligatorio.';
    if (!f.apellidos?.trim()) errors['apellidos'] = 'Los apellidos son obligatorios.';
    if (!f.fechaNacimiento) { errors['fechaNacimiento'] = 'La fecha de nacimiento es obligatoria.'; }
    else if (f.rol === 'ORGANIZADOR' && this.calcularEdad(f.fechaNacimiento) < 18) { errors['fechaNacimiento'] = 'Los organizadores deben ser mayores de 18 años.'; }
    if (!f.dni?.trim()) errors['dni'] = 'El DNI es obligatorio.';
    else if (!/^[0-9]{8}[A-Za-z]$/.test(f.dni.trim())) errors['dni'] = 'Formato no válido (ej: 12345678A).';
    if (!f.username?.trim()) errors['username'] = 'El nombre de usuario es obligatorio.';
    else if (f.username.trim().length < 3) errors['username'] = 'Mínimo 3 caracteres.';
    if (!f.email?.trim()) errors['email'] = 'El correo es obligatorio.';
    else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(f.email.trim())) errors['email'] = 'Correo no válido.';
    if (!f.password) errors['password'] = 'La contraseña es obligatoria.';
    else if (f.password.length < 8) errors['password'] = 'Mínimo 8 caracteres.';
    else if (!/(?=.*[a-zA-Z])(?=.*[0-9])/.test(f.password)) errors['password'] = 'Debe contener letras y números.';
    if (!f.organizacionNombre?.trim()) errors['organizacionNombre'] = 'El nombre de organización es obligatorio.';
    else if (this.orgMode() === 'unirse' && this.orgExiste() === false) errors['organizacionNombre'] = 'Esta organización no existe.';
    this.regErrors.set(errors);
    return Object.keys(errors).length === 0;
  }

  submitRegistro(): void {
    this.regError.set('');
    if (!this.validate()) return;

    if (this.orgMode() === 'unirse' && this.orgExiste() === null) {
      const nombre = this.regForm.organizacionNombre.trim();
      this.checkingOrg.set(true);
      this.http.get<{ existe: boolean }>(`/api/auth/organizacion/existe?nombre=${encodeURIComponent(nombre)}`).subscribe({
        next: (res) => {
          this.checkingOrg.set(false);
          this.orgExiste.set(res.existe);
          if (!res.existe) { this.regErrors.update(e => ({ ...e, organizacionNombre: 'Esta organización no existe.' })); return; }
          this.doRegister();
        },
        error: () => { this.checkingOrg.set(false); this.regError.set('No se pudo verificar la organización.'); }
      });
      return;
    }
    this.doRegister();
  }

  private doRegister(): void {
    const f = this.regForm;
    const payload: RegisterRequest = {
      username: f.username.trim(), nombre: f.nombre.trim(), apellidos: f.apellidos.trim(),
      fechaNacimiento: f.fechaNacimiento, email: f.email.trim(), dni: f.dni.trim(),
      password: f.password, rol: f.rol,
      organizacionNombre: f.organizacionNombre.trim(),
      crearOrganizacion: this.orgMode() === 'crear',
    };
    this.regLoading.set(true);
    this.auth.register(payload).subscribe({
      next: () => {
        this.regLoading.set(false);
        this.authModal.close();
        const rol = this.auth.currentUser()?.rol;
        this.router.navigate([rol === 'ORGANIZADOR' ? '/organizer' : '/']);
      },
      error: (err) => {
        this.regLoading.set(false);
        this.regError.set(err?.error?.message ?? 'Error al crear la cuenta. Inténtalo de nuevo.');
      },
    });
  }

  onOverlayClick(event: MouseEvent): void {
    if (!(event.target as HTMLElement).closest('.auth-overlay__card')) {
      this.authModal.close();
    }
  }
}
