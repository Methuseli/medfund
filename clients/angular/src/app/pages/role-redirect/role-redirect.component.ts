import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

const TENANT_ROLES = [
  'tenant_admin',
  'claims_clerk',
  'finance_officer',
  'member',
  'provider',
];

@Component({
  selector: 'app-role-redirect',
  standalone: true,
  templateUrl: './role-redirect.component.html',
  styleUrl: './role-redirect.component.scss',
})
export class RoleRedirectComponent implements OnInit {
  constructor(
    private keycloak: KeycloakService,
    private router: Router,
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      const roles = this.keycloak.getUserRoles(true);

      console.log('[RoleRedirect] User roles from Keycloak:', roles);

      if (roles.includes('super_admin')) {
        this.router.navigate(['/platform/dashboard'], { replaceUrl: true });
        return;
      }

      const isTenantUser = roles.some((r) => TENANT_ROLES.includes(r));

      if (isTenantUser) {
        this.router.navigate(['/tenant/dashboard'], { replaceUrl: true });
        return;
      }

      // No recognized role — default to platform for authenticated users, otherwise unauthorized
      if (roles.length > 0) {
        console.log('[RoleRedirect] No recognized role, defaulting to platform dashboard. Roles:', roles);
        this.router.navigate(['/platform/dashboard'], { replaceUrl: true });
      } else {
        this.router.navigate(['/unauthorized'], { replaceUrl: true });
      }
    } catch (err) {
      console.error('[RoleRedirect] Error getting roles:', err);
      this.router.navigate(['/unauthorized'], { replaceUrl: true });
    }
  }
}
