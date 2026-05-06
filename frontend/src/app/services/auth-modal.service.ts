import { Injectable, signal } from '@angular/core';

export type AuthView = 'login' | 'registro';

@Injectable({ providedIn: 'root' })
export class AuthModalService {
  readonly visible = signal(false);
  readonly view = signal<AuthView>('login');

  open(view: AuthView = 'login'): void {
    this.view.set(view);
    this.visible.set(true);
  }

  close(): void {
    this.visible.set(false);
  }

  switchTo(view: AuthView): void {
    this.view.set(view);
  }
}
