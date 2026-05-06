import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

export interface Miembro {
  username: string;
  nombre: string;
  apellidos: string;
  rol: 'ORGANIZADOR' | 'USUARIO';
}

@Component({
  selector: 'app-org-members',
  standalone: true,
  imports: [],
  templateUrl: './org-members.component.html',
  styleUrl: './org-members.component.scss',
})
export class OrgMembersComponent implements OnInit {
  private http = inject(HttpClient);
  protected auth = inject(AuthService);

  miembros = signal<Miembro[]>([]);
  loading = signal(true);
  error = signal('');

  get organizadores(): Miembro[] {
    return this.miembros().filter(m => m.rol === 'ORGANIZADOR');
  }

  get usuarios(): Miembro[] {
    return this.miembros().filter(m => m.rol === 'USUARIO');
  }

  ngOnInit(): void {
    this.http.get<Miembro[]>('/api/auth/organizacion/miembros').subscribe({
      next: (data) => {
        this.miembros.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los miembros de la organización.');
        this.loading.set(false);
      },
    });
  }
}
