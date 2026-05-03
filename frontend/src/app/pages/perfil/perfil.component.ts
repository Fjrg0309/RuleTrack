import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.scss',
})
export class PerfilComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  protected goToPublications(): void {
    this.router.navigate(['/publicaciones']);
  }
}

