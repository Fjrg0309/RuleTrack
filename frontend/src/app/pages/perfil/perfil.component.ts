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

  // Mock data — in a real app this would come from a profile service
  protected organizerProfile = {
    name: 'Francisco José Redondo González',
    birthDate: '13/09/2003',
    organizers: 1,
    members: 150,
  };

  protected userProfile = {
    name: 'Iker Jiménez Ciria',
    birthDate: '04/10/2005',
    organization: 'Federación Andaluza de Deportes',
    nick: 'Ik_05',
  };

  protected goToPublications(): void {
    this.router.navigate(['/publicaciones']);
  }
}
