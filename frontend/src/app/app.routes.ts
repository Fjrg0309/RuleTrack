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
import { PublicacionesComponent } from './pages/publicaciones/publicaciones.component';
import { AjustesComponent } from './pages/ajustes/ajustes.component';
import { OrganizacionComponent } from './pages/organizacion/organizacion.component';
import { PerfilComponent } from './pages/perfil/perfil.component';
import { AjustesPublicacionComponent } from './pages/ajustes-publicacion/ajustes-publicacion.component';

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
  { path: 'publicaciones', component: PublicacionesComponent },
  { path: 'ajustes', component: AjustesComponent },
  { path: 'organizacion', component: OrganizacionComponent },
  { path: 'perfil', component: PerfilComponent },
  { path: 'ajustes-publicacion', component: AjustesPublicacionComponent },
];

