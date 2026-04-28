import { Injectable, signal } from '@angular/core';

export interface User {
  username: string;
  role: 'organizer' | 'user';
  displayName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly currentUser = signal<User | null>(null);

  login(username: string, password: string): boolean {
    if (username === 'organizador' && password === '1234') {
      this.currentUser.set({ username, role: 'organizer', displayName: 'Organizador' });
      return true;
    }
    if (username === 'usuario' && password === '1234') {
      this.currentUser.set({ username, role: 'user', displayName: 'Usuario' });
      return true;
    }
    return false;
  }

  logout(): void {
    this.currentUser.set(null);
  }
}
