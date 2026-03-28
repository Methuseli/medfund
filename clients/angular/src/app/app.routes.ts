import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'claims', loadComponent: () => import('./pages/claims/claims.component').then(m => m.ClaimsComponent) },
      { path: 'contributions', loadComponent: () => import('./pages/contributions/contributions.component').then(m => m.ContributionsComponent) },
      { path: 'finance', loadComponent: () => import('./pages/finance/finance.component').then(m => m.FinanceComponent) },
      { path: 'members', loadComponent: () => import('./pages/members/members.component').then(m => m.MembersComponent) },
      { path: 'providers', loadComponent: () => import('./pages/providers/providers.component').then(m => m.ProvidersComponent) },
      { path: 'admin', loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent) },
    ],
  },
  { path: 'unauthorized', loadComponent: () => import('./pages/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent) },
  { path: '**', redirectTo: 'dashboard' },
];
