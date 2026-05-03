import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.scss'
})
export class RegistroComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  loading = signal(false);
  error = signal('');
  fieldErrors = signal<Record<string, string>>({});
  orgMode = signal<'unirse' | 'crear'>('unirse');
  orgExiste = signal<boolean | null>(null);
  checkingOrg = signal(false);

  form = {
    username: '',
    nombre: '',
    apellidos: '',
    fechaNacimiento: '',
    email: '',
    dni: '',
    password: '',
    rol: 'USUARIO' as 'USUARIO' | 'ORGANIZADOR',
    organizacionNombre: '',
  };

  setOrgMode(mode: 'unirse' | 'crear', event: Event): void {
    event.preventDefault();
    this.orgMode.set(mode);
    this.orgExiste.set(null);
    this.fieldErrors.update(e => { const n = { ...e }; delete n['organizacionNombre']; return n; });
  }

  onOrgBlur(): void {
    if (this.orgMode() === 'crear') return;
    const nombre = this.form.organizacionNombre?.trim();
    if (!nombre) return;
    this.checkingOrg.set(true);
    this.http.get<{ existe: boolean }>(`/api/auth/organizacion/existe?nombre=${encodeURIComponent(nombre)}`)
      .subscribe({
        next: (res) => {
          this.orgExiste.set(res.existe);
          this.checkingOrg.set(false);
          if (!res.existe) {
            this.fieldErrors.update(e => ({ ...e, organizacionNombre: 'Esta organización no existe. Puedes crearla usando el enlace de abajo.' }));
          } else {
            this.fieldErrors.update(e => { const n = { ...e }; delete n['organizacionNombre']; return n; });
          }
        },
        error: () => { this.checkingOrg.set(false); }
      });
  }

  private calcularEdad(fechaNacimiento: string): number {
    const hoy = new Date();
    const nac = new Date(fechaNacimiento);
    let edad = hoy.getFullYear() - nac.getFullYear();
    const m = hoy.getMonth() - nac.getMonth();
    if (m < 0 || (m === 0 && hoy.getDate() < nac.getDate())) edad--;
    return edad;
  }

  private validateFields(): boolean {
    const errors: Record<string, string> = {};
    const f = this.form;

    if (!f.nombre?.trim()) errors['nombre'] = 'El nombre es obligatorio.';
    if (!f.apellidos?.trim()) errors['apellidos'] = 'Los apellidos son obligatorios.';

    if (!f.fechaNacimiento) {
      errors['fechaNacimiento'] = 'La fecha de nacimiento es obligatoria.';
    } else if (f.rol === 'ORGANIZADOR' && this.calcularEdad(f.fechaNacimiento) < 18) {
      errors['fechaNacimiento'] = 'Los organizadores deben ser mayores de 18 años.';
    }

    if (!f.dni?.trim()) {
      errors['dni'] = 'El DNI es obligatorio.';
    } else if (!/^[0-9]{8}[A-Za-z]$/.test(f.dni.trim())) {
      errors['dni'] = 'Formato no válido. Debe tener 8 números y 1 letra (ej: 12345678A).';
    }

    if (!f.username?.trim()) {
      errors['username'] = 'El nombre de usuario es obligatorio.';
    } else if (f.username.trim().length < 3) {
      errors['username'] = 'Debe tener al menos 3 caracteres.';
    }

    if (!f.email?.trim()) {
      errors['email'] = 'El correo electrónico es obligatorio.';
    } else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(f.email.trim())) {
      errors['email'] = 'Introduce un correo electrónico válido.';
    }

    if (!f.password) {
      errors['password'] = 'La contraseña es obligatoria.';
    } else if (f.password.length < 8) {
      errors['password'] = 'Debe tener al menos 8 caracteres.';
    } else if (!/(?=.*[a-zA-Z])(?=.*[0-9])/.test(f.password)) {
      errors['password'] = 'La contraseña debe contener letras y números.';
    }

    if (!f.organizacionNombre?.trim()) {
      errors['organizacionNombre'] = 'El nombre de organización es obligatorio.';
    } else if (this.orgMode() === 'unirse' && this.orgExiste() === false) {
      errors['organizacionNombre'] = 'Esta organización no existe. Usa el enlace de abajo para crearla.';
    }

    this.fieldErrors.set(errors);
    return Object.keys(errors).length === 0;
  }

  submit(): void {
    this.error.set('');
    if (!this.validateFields()) return;

    if (this.orgMode() === 'unirse' && this.orgExiste() === null) {
      const nombre = this.form.organizacionNombre.trim();
      this.checkingOrg.set(true);
      this.http.get<{ existe: boolean }>(`/api/auth/organizacion/existe?nombre=${encodeURIComponent(nombre)}`)
        .subscribe({
          next: (res) => {
            this.checkingOrg.set(false);
            this.orgExiste.set(res.existe);
            if (!res.existe) {
              this.fieldErrors.update(e => ({
                ...e,
                organizacionNombre: 'Esta organización no existe. Usa el enlace de abajo para crearla.'
              }));
              return;
            }
            this.doRegister();
          },
          error: () => {
            this.checkingOrg.set(false);
            this.error.set('No se pudo verificar la organización. Comprueba tu conexión e inténtalo de nuevo.');
          }
        });
      return;
    }

    this.doRegister();
  }

  private doRegister(): void {
    const payload: RegisterRequest = {
      username: this.form.username.trim(),
      nombre: this.form.nombre.trim(),
      apellidos: this.form.apellidos.trim(),
      fechaNacimiento: this.form.fechaNacimiento,
      email: this.form.email.trim(),
      dni: this.form.dni.trim().toUpperCase(),
      password: this.form.password,
      rol: this.form.rol,
      organizacionNombre: this.form.organizacionNombre.trim(),
      crearOrganizacion: this.orgMode() === 'crear',
    };

    this.loading.set(true);
    this.auth.register(payload).subscribe({
      next: () => {
        const user = this.auth.currentUser();
        this.loading.set(false);
        this.router.navigate([user?.rol === 'ORGANIZADOR' ? '/organizer' : '/']);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Error al crear la cuenta. Revisa los datos e inténtalo de nuevo.');
        this.loading.set(false);
      }
    });
  }
}
