import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PublicacionesService, ReglamentoDTO } from '../../services/publicaciones.service';

@Component({
  selector: 'app-publicaciones',
  templateUrl: './publicaciones.component.html',
  styleUrl: './publicaciones.component.scss',
})
export class PublicacionesComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private pubService = inject(PublicacionesService);

  protected publications = signal<ReglamentoDTO[]>([]);
  protected copiedId: number | null = null;

  protected readonly pageSize = 8;
  protected currentPage = signal(0);

  protected totalPages = computed(() =>
    Math.ceil(this.publications().length / this.pageSize)
  );

  protected pagedPublications = computed(() => {
    const start = this.currentPage() * this.pageSize;
    return this.publications().slice(start, start + this.pageSize);
  });

  ngOnInit(): void {
    this.pubService.getVisibles().subscribe({
      next: (data) => this.publications.set(data),
      error: () => {}
    });
  }

  protected prevPage(): void {
    if (this.currentPage() > 0) this.currentPage.set(this.currentPage() - 1);
  }

  protected nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) this.currentPage.set(this.currentPage() + 1);
  }

  protected copyUrl(pub: ReglamentoDTO): void {
    const url = `${window.location.origin}/view/${pub.id}`;
    navigator.clipboard.writeText(url).catch(() => {});
    this.copiedId = pub.id;
    setTimeout(() => (this.copiedId = null), 1500);
  }

  protected getUrl(pub: ReglamentoDTO): string {
    return `${window.location.origin}/view/${pub.id}`;
  }

  protected editPublication(pub: ReglamentoDTO): void {
    this.router.navigate(['/ajustes-publicacion'], { queryParams: { id: pub.id } });
  }
}
