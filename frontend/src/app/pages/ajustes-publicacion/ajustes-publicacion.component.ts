import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ajustes-publicacion',
  templateUrl: './ajustes-publicacion.component.html',
  styleUrl: './ajustes-publicacion.component.scss',
})
export class AjustesPublicacionComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  protected url = signal('https://FederacionDeCadiz/reglamento.md');
  protected estado = signal('Público');
  protected fecha = signal('22/03/2026');
  protected version = signal('2.1');
  protected descripcion = signal('Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer rutrum varius vehicula. Aenean dapibus, orci et convallis rhoncus, risus eros varius eros, non pharetra metus augue eu dolor. Aliquam ut lobortis metus. Morbi pellentesque accumsan mi sed tempus. Integer imperdiet condimentum arcu, vitae malesuada elit lacinia vel. Sed lobortis vel sapien laoreet imperdiet.');

  protected showDeleteConfirm = signal(false);

  protected saveChanges(): void {
    // TODO: call backend
  }

  protected confirmDelete(): void {
    this.showDeleteConfirm.set(true);
  }

  protected cancelDelete(): void {
    this.showDeleteConfirm.set(false);
  }

  protected deletePublication(): void {
    this.showDeleteConfirm.set(false);
    this.router.navigate(['/publicaciones']);
  }
}
