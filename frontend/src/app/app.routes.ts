import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { HomeOrganizerComponent } from './pages/home-organizer/home-organizer.component';
import { UploadComponent } from './pages/upload/upload.component';
import { ConvertingComponent } from './pages/converting/converting.component';
import { PreviewComponent } from './pages/preview/preview.component';
import { CorrectingComponent } from './pages/correcting/correcting.component';
import { CorrectedComponent } from './pages/corrected/corrected.component';
import { LinkCreatedComponent } from './pages/link-created/link-created.component';
import { LoginComponent } from './pages/login/login.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'organizer', component: HomeOrganizerComponent },
  { path: 'upload', component: UploadComponent },
  { path: 'converting', component: ConvertingComponent },
  { path: 'preview', component: PreviewComponent },
  { path: 'correcting', component: CorrectingComponent },
  { path: 'corrected', component: CorrectedComponent },
  { path: 'link-created', component: LinkCreatedComponent },
  { path: 'login', component: LoginComponent },
];
