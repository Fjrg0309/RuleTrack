import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardComponent } from '../../components/card/card.component';

@Component({
  selector: 'app-home-organizer',
  imports: [RouterLink, CardComponent],
  templateUrl: './home-organizer.component.html',
  styleUrl: './home-organizer.component.scss',
})
export class HomeOrganizerComponent {}
