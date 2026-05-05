import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
export interface User {
  username: string;
  nombre: string;
  apellidos: string;
  email: string;
  rol: 'ORGANIZADOR' | 'USUARIO';
  organizacionNombre: string;
  fechaNacimiento?: string;
  /** @deprecated use rol */
  role: 'organizer' | 'user';
  displayName: string;
}

interface AuthResponse {
  token: string;
  username: string;
  nombre: string;
  apellidos: string;
  email: string;
  rol: string;
  organizacionNombre: string;
  fechaNacimiento?: string;
}

export interface RegisterRequest {
  username: string;
  nombre: string;
  apellidos: string;
  fechaNacimiento: string; // ISO date YYYY-MM-DD
  email: string;
  dni: string;
  password: string;
  rol: 'ORGANIZADOR' | 'USUARIO';
  organizacionNombre: string;
  crearOrganizacion: boolean;
}

export interface OrganizacionInfo {
  nombre: string;
  anioFundacion: number;
  numOrganizadores: number;
  numMiembros: number;
}

const TOKEN_KEY = 'rt_token';
const USER_KEY  = 'rt_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private readonly apiBase = '/api/auth';

  readonly currentUser = signal<User | null>(this.loadUser());

  private loadUser(): User | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiBase}/login`, { username, password }).pipe(
      tap(res => this.persistSession(res))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiBase}/register`, request).pipe(
      tap(res => this.persistSession(res))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
  }

  getOrganizacionInfo(nombre: string): Observable<OrganizacionInfo> {
    return this.http.get<OrganizacionInfo>(`${this.apiBase}/organizacion/info`, { params: { nombre } });
  }

  updateProfile(nombre: string, email: string): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(`${this.apiBase}/me`, { nombre, email }).pipe(
      tap(res => this.persistSession(res))
    );
  }

  refreshMe(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiBase}/me`).pipe(
      tap(res => this.persistSession(res))
    );
  }

  private persistSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    const rol = res.rol as 'ORGANIZADOR' | 'USUARIO';
    const user: User = {
      username: res.username,
      nombre: res.nombre,
      apellidos: res.apellidos,
      email: res.email,
      rol,
      organizacionNombre: res.organizacionNombre,
      fechaNacimiento: res.fechaNacimiento,
      role: rol === 'ORGANIZADOR' ? 'organizer' : 'user',
      displayName: res.nombre,
    };
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }
}

