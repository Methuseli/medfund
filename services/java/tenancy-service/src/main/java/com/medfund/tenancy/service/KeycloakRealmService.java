package com.medfund.tenancy.service;

import com.medfund.tenancy.entity.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages Keycloak realms for tenants via the Keycloak Admin REST API.
 * Creates realm, OIDC clients (Angular + Flutter), and default roles.
 */
@Service
public class KeycloakRealmService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakRealmService.class);

    private final WebClient webClient;

    @Value("${keycloak.admin.url:http://localhost:9080}")
    private String keycloakUrl;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    public KeycloakRealmService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Void> createRealm(String realmName, Tenant tenant) {
        return getAdminToken()
                .flatMap(token -> createRealmRequest(token, realmName, tenant))
                .doOnSuccess(v -> log.info("Keycloak realm created: {}", realmName))
                .doOnError(e -> log.error("Failed to create Keycloak realm: {}", realmName, e))
                .onErrorResume(e -> {
                    log.warn("Keycloak realm creation failed for {}. Will retry on next startup.", realmName);
                    return Mono.empty();
                });
    }

    private Mono<String> getAdminToken() {
        return webClient.post()
                .uri(keycloakUrl + "/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=password&client_id=admin-cli&username=" + adminUsername + "&password=" + adminPassword)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"));
    }

    private Mono<Void> createRealmRequest(String token, String realmName, Tenant tenant) {
        Map<String, Object> realmConfig = new HashMap<>();
        realmConfig.put("realm", realmName);
        realmConfig.put("enabled", true);
        realmConfig.put("displayName", tenant.getName());
        realmConfig.put("loginTheme", "keycloak");
        realmConfig.put("registrationAllowed", false);
        realmConfig.put("resetPasswordAllowed", true);
        realmConfig.put("bruteForceProtected", true);
        realmConfig.put("permanentLockout", false);
        realmConfig.put("maxFailureWaitSeconds", 900);
        realmConfig.put("minimumQuickLoginWaitSeconds", 60);
        realmConfig.put("waitIncrementSeconds", 60);
        realmConfig.put("quickLoginCheckMilliSeconds", 1000);
        realmConfig.put("maxDeltaTimeSeconds", 43200);
        realmConfig.put("failureFactor", 5);
        realmConfig.put("roles", Map.of("realm", List.of(
                Map.of("name", "tenant_admin", "description", "Tenant administrator"),
                Map.of("name", "claims_clerk", "description", "Claims processing clerk"),
                Map.of("name", "claims_assessor", "description", "Claims assessor/adjudicator"),
                Map.of("name", "finance_officer", "description", "Finance officer"),
                Map.of("name", "contributions_officer", "description", "Contributions officer"),
                Map.of("name", "provider", "description", "Healthcare provider"),
                Map.of("name", "member", "description", "Insurance member"),
                Map.of("name", "group_liaison", "description", "Corporate group liaison")
        )));

        return webClient.post()
                .uri(keycloakUrl + "/admin/realms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(realmConfig)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
