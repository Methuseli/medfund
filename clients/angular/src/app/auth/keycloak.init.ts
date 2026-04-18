import { KeycloakService } from 'keycloak-angular';
import { environment } from '../../environments/environment';

const GATEWAY_URL = environment.apiBaseUrl.replace('/api/v1', '');

export function initializeKeycloak(keycloak: KeycloakService): () => Promise<boolean> {
  return () =>
    keycloak
      .init({
        config: {
          url: environment.keycloak.url,
          realm: environment.keycloak.realm,
          clientId: environment.keycloak.clientId,
        },
        initOptions: {
          onLoad: 'login-required',
          pkceMethod: 'S256',
          checkLoginIframe: false,
        },
        enableBearerInterceptor: false,
        bearerExcludedUrls: [],
      })
      .then(async (authenticated) => {
        if (authenticated) {
          await establishSession(keycloak);
          // Refresh the HTTP-only cookie whenever the Keycloak token is renewed
          keycloak.getKeycloakInstance().onTokenExpired = async () => {
            try {
              await keycloak.updateToken(30);
              await establishSession(keycloak);
            } catch {
              keycloak.login();
            }
          };
        }
        return authenticated;
      });
}

async function establishSession(keycloak: KeycloakService): Promise<void> {
  const token = await keycloak.getToken();
  await fetch(`${GATEWAY_URL}/auth/session`, {
    method: 'POST',
    credentials: 'include',
    headers: { Authorization: `Bearer ${token}` },
  });
}
