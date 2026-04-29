import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink, CardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  protected auth = inject(AuthService);
  private router = inject(Router);
  protected toastMessage = signal('');
  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    if (this.auth.currentUser()?.role === 'organizer') {
      this.router.navigate(['/organizer'], { replaceUrl: true });
    }
  }

  protected showLoginToast(message: string): void {
    if (this.toastTimer) clearTimeout(this.toastTimer);
    this.toastMessage.set(message);
    this.toastTimer = setTimeout(() => this.toastMessage.set(''), 3000);
  }
}
