import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './layout/header/header.component';
import { FooterComponent } from './layout/footer/footer.component';
import { ToastOutletComponent } from './components/toast-outlet/toast-outlet.component';
import { AuthOverlayComponent } from './components/auth-overlay/auth-overlay.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, FooterComponent, ToastOutletComponent, AuthOverlayComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
