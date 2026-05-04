import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink, CardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);

  protected toastMessage = signal('');
  protected publicaciones = signal<ReglamentoDTO[]>([]);
  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    if (this.auth.currentUser()?.rol === 'ORGANIZADOR') {
      this.router.navigate(['/organizer'], { replaceUrl: true });
    }
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publicaciones.set(data),
      error: () => {}
    });
  }

  protected showLoginToast(message: string): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toastMessage.set(message);
    this.toastTimer = setTimeout(() => this.toastMessage.set(''), 3000);
  }

  protected downloadDoc(pub: ReglamentoDTO): void {
    this.pubService.downloadAsMarkdown(pub);
  }
}
