import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="layout">
      <nav class="sidebar">
        <div class="sidebar-header">
          <h2>MedFund</h2>
        </div>
        <ul class="nav-links">
          <li><a routerLink="/dashboard" routerLinkActive="active">Dashboard</a></li>
          <li><a routerLink="/claims" routerLinkActive="active">Claims</a></li>
          <li><a routerLink="/contributions" routerLinkActive="active">Contributions</a></li>
          <li><a routerLink="/finance" routerLinkActive="active">Finance</a></li>
          <li><a routerLink="/providers" routerLinkActive="active">Providers</a></li>
          <li><a routerLink="/members" routerLinkActive="active">Members</a></li>
          <li><a routerLink="/admin" routerLinkActive="active">Admin</a></li>
        </ul>
        <div class="sidebar-footer">
          <button (click)="logout()">Logout</button>
        </div>
      </nav>
      <main class="content">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [`
    .layout { display: flex; height: 100vh; }
    .sidebar { width: 240px; background: #1a1a2e; color: #fff; display: flex; flex-direction: column; }
    .sidebar-header { padding: 20px; border-bottom: 1px solid #333; }
    .sidebar-header h2 { margin: 0; font-size: 1.4rem; }
    .nav-links { list-style: none; padding: 0; margin: 0; flex: 1; }
    .nav-links li a { display: block; padding: 12px 20px; color: #ccc; text-decoration: none; }
    .nav-links li a:hover, .nav-links li a.active { background: #16213e; color: #fff; }
    .sidebar-footer { padding: 20px; border-top: 1px solid #333; }
    .sidebar-footer button { width: 100%; padding: 8px; background: #e94560; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
    .content { flex: 1; padding: 24px; overflow-y: auto; background: #f5f5f5; }
  `],
})
export class LayoutComponent {
  constructor(private keycloak: KeycloakService) {}

  logout(): void {
    this.keycloak.logout();
  }
}
