import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';

@Component({
  selector: 'app-home-organizer',
  imports: [RouterLink, CardComponent],
  templateUrl: './home-organizer.component.html',
  styleUrl: './home-organizer.component.scss',
})
export class HomeOrganizerComponent implements OnInit {
  protected auth = inject(AuthService);
  private pubService = inject(PublicacionesService);

  protected publicaciones = signal<ReglamentoDTO[]>([]);

  ngOnInit(): void {
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publicaciones.set(data),
      error: () => {}
    });
  }

  protected downloadDoc(pub: ReglamentoDTO): void {
    this.pubService.downloadAsMarkdown(pub);
  }
}
