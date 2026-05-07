import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
/** Interfaz que representa al usuario autenticado en el cliente. */
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

/** Payload de petición de registro de nuevo usuario. */
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

/** Datos básicos de una organización. */
export interface OrganizacionInfo {
  nombre: string;
  anioFundacion: number;
  numOrganizadores: number;
  numMiembros: number;
}

const TOKEN_KEY = 'rt_token';
const USER_KEY  = 'rt_user';

/**
 * Servicio de autenticación: gestiona login, registro, sesión y perfil.
 * Persiste el token y el usuario en localStorage para sobrevivir recargas.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private readonly apiBase = '/api/auth';

  readonly currentUser = signal<User | null>(this.loadUser());

  /** Carga el usuario guardado en localStorage al inicializar el servicio. */
  private loadUser(): User | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }

  /** Devuelve el token JWT almacenado en localStorage, o null si no existe. */
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  /**
   * Autentica al usuario contra el backend y persiste la sesión.
   * @param username Nombre de usuario.
   * @param password Contraseña en texto plano.
   */
  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiBase}/login`, { username, password }).pipe(
      tap(res => this.persistSession(res))
    );
  }

  /**
   * Registra un nuevo usuario y persiste la sesión automáticamente.
   * @param request Datos completos del nuevo usuario.
   */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiBase}/register`, request).pipe(
      tap(res => this.persistSession(res))
    );
  }

  /** Cierra la sesión eliminando token y datos de usuario del almacenamiento local. */
  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
  }

  getOrganizacionInfo(nombre: string): Observable<OrganizacionInfo> {
    return this.http.get<OrganizacionInfo>(`${this.apiBase}/organizacion/info`, { params: { nombre } });
  }

  /**
   * Actualiza el nombre y email del perfil del usuario autenticado.
   * @param nombre Nuevo nombre completo.
   * @param email Nuevo email.
   */
  updateProfile(nombre: string, email: string): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(`${this.apiBase}/me`, { nombre, email }).pipe(
      tap(res => this.persistSession(res))
    );
  }

  /** Recarga los datos del usuario desde el backend y actualiza la sesión local. */
  refreshMe(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiBase}/me`).pipe(
      tap(res => this.persistSession(res))
    );
  }

  /**
   * Persiste el token y los datos de usuario en localStorage y actualiza el signal.
   * @param res Respuesta de autenticación del backend.
   */
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

