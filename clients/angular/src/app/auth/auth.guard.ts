import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

export const authGuard: CanActivateFn = async () => {
  const keycloak = inject(KeycloakService);
  const router = inject(Router);

  const isAuthenticated = await keycloak.isLoggedIn();
  if (!isAuthenticated) {
    await keycloak.login();
    return false;
  }
  return true;
};

export const roleGuard = (requiredRoles: string[]): CanActivateFn => {
  return async () => {
    const keycloak = inject(KeycloakService);
    const router = inject(Router);

    const isAuthenticated = await keycloak.isLoggedIn();
    if (!isAuthenticated) {
      await keycloak.login();
      return false;
    }

    const userRoles = keycloak.getUserRoles();
    const hasRole = requiredRoles.some((role) => userRoles.includes(role));
    if (!hasRole) {
      router.navigate(['/unauthorized']);
      return false;
    }
    return true;
  };
};
