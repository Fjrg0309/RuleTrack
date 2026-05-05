import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, OrganizacionInfo } from '../../services/auth.service';

@Component({
  selector: 'app-organizacion',
  templateUrl: './organizacion.component.html',
  styleUrl: './organizacion.component.scss',
})
export class OrganizacionComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);

  protected org = signal<OrganizacionInfo | null>(null);

  ngOnInit(): void {
    const nombre = this.auth.currentUser()?.organizacionNombre;
    if (nombre) {
      this.auth.getOrganizacionInfo(nombre).subscribe({
        next: (data) => this.org.set(data),
        error: () => {}
      });
    }
  }

  protected goToPublications(): void {
    this.router.navigate(['/publicaciones']);
  }
}
