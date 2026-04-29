import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-organizacion',
  templateUrl: './organizacion.component.html',
  styleUrl: './organizacion.component.scss',
})
export class OrganizacionComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  protected org = {
    name: 'Federación Andaluza de Deportes',
    founded: '2019',
    organizers: 1,
    members: 150,
  };

  protected goToPublications(): void {
    this.router.navigate(['/publicaciones']);
  }
}
