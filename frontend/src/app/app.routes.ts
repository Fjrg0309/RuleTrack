import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { HomeOrganizerComponent } from './pages/home-organizer/home-organizer.component';
import { UploadComponent } from './pages/upload/upload.component';
import { ConvertingComponent } from './pages/converting/converting.component';
import { PreviewComponent } from './pages/preview/preview.component';
import { CorrectingComponent } from './pages/correcting/correcting.component';
import { CorrectedComponent } from './pages/corrected/corrected.component';
import { LinkCreatedComponent } from './pages/link-created/link-created.component';
import { PublicacionesComponent } from './pages/publicaciones/publicaciones.component';
import { AjustesComponent } from './pages/ajustes/ajustes.component';
import { OrganizacionComponent } from './pages/organizacion/organizacion.component';
import { PerfilComponent } from './pages/perfil/perfil.component';
import { AjustesPublicacionComponent } from './pages/ajustes-publicacion/ajustes-publicacion.component';
import { ViewComponent } from './pages/view/view.component';
import { OrgMembersComponent } from './pages/org-members/org-members.component';
import { AuthModalService, AuthView } from './services/auth-modal.service';
import { AuthService } from './services/auth.service';

function authRedirectGuard(view: AuthView) {
  return () => {
    const authModal = inject(AuthModalService);
    const auth = inject(AuthService);
    const router = inject(Router);
    const user = auth.currentUser();
    const target = user?.rol === 'ORGANIZADOR' ? '/organizer' : '/';
    authModal.open(view);
    return router.parseUrl(target);
  };
}

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'organizer', component: HomeOrganizerComponent },
  { path: 'upload', component: UploadComponent },
  { path: 'converting', component: ConvertingComponent },
  { path: 'preview', component: PreviewComponent },
  { path: 'correcting', component: CorrectingComponent },
  { path: 'corrected', component: CorrectedComponent },
  { path: 'link-created', component: LinkCreatedComponent },
  { path: 'login', canActivate: [authRedirectGuard('login')], component: HomeComponent },
  { path: 'registro', canActivate: [authRedirectGuard('registro')], component: HomeComponent },
  { path: 'publicaciones', component: PublicacionesComponent },
  { path: 'ajustes', component: AjustesComponent },
  { path: 'organizacion', component: OrganizacionComponent },
  { path: 'perfil', component: PerfilComponent },
  { path: 'ajustes-publicacion', component: AjustesPublicacionComponent },
  { path: 'view/:id', component: ViewComponent },
  { path: 'miembros-organizacion', component: OrgMembersComponent },
];

