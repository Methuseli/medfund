package com.medfund.tenancy.service;

import com.medfund.shared.audit.AuditEvent;
import com.medfund.shared.audit.AuditPublisher;
import com.medfund.tenancy.dto.CreateTenantRequest;
import com.medfund.tenancy.dto.UpdateTenantRequest;
import com.medfund.tenancy.entity.Tenant;
import com.medfund.tenancy.entity.TenantCurrencyConfig;
import com.medfund.tenancy.exception.TenantNotFoundException;
import com.medfund.tenancy.exception.TenantSlugConflictException;
import com.medfund.tenancy.repository.TenantCurrencyConfigRepository;
import com.medfund.tenancy.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class TenantService {

    private static final Logger log = LoggerFactory.getLogger(TenantService.class);

    private final TenantRepository tenantRepository;
    private final TenantCurrencyConfigRepository currencyConfigRepository;
    private final SchemaProvisioningService schemaProvisioning;
    private final KeycloakRealmService keycloakRealmService;
    private final AuditPublisher auditPublisher;
    private final TenantEventPublisher eventPublisher;

    public TenantService(
            TenantRepository tenantRepository,
            TenantCurrencyConfigRepository currencyConfigRepository,
            SchemaProvisioningService schemaProvisioning,
            KeycloakRealmService keycloakRealmService,
            AuditPublisher auditPublisher,
            TenantEventPublisher eventPublisher) {
        this.tenantRepository = tenantRepository;
        this.currencyConfigRepository = currencyConfigRepository;
        this.schemaProvisioning = schemaProvisioning;
        this.keycloakRealmService = keycloakRealmService;
        this.auditPublisher = auditPublisher;
        this.eventPublisher = eventPublisher;
    }

    public Flux<Tenant> findAll() {
        return tenantRepository.findAllOrderByCreatedAtDesc();
    }

    public Mono<Tenant> findById(UUID id) {
        return tenantRepository.findById(id)
                .switchIfEmpty(Mono.error(new TenantNotFoundException(id)));
    }

    public Mono<Tenant> findBySlug(String slug) {
        return tenantRepository.findBySlug(slug)
                .switchIfEmpty(Mono.error(new TenantNotFoundException(slug)));
    }

    public Mono<Tenant> findByDomain(String domain) {
        return tenantRepository.findByDomain(domain);
    }

    public Flux<Tenant> findByStatus(String status) {
        return tenantRepository.findByStatus(status);
    }

    @Transactional
    public Mono<Tenant> create(CreateTenantRequest request, String actorId) {
        return tenantRepository.existsBySlug(request.slug())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.<Tenant>error(new TenantSlugConflictException(request.slug()));
                    }

                    UUID tenantId = UUID.randomUUID();
                    String schemaName = "tenant_" + tenantId.toString().replace("-", "");
                    String realmName = "medfund-" + request.slug();

                    Tenant tenant = new Tenant();
                    tenant.setId(tenantId);
                    tenant.setName(request.name());
                    tenant.setSlug(request.slug());
                    tenant.setDomain(request.domain());
                    tenant.setSchemaName(schemaName);
                    tenant.setPlanId(request.planId());
                    tenant.setStatus("active");
                    tenant.setSettings("{}");
                    tenant.setBranding("{}");
                    tenant.setContactEmail(request.contactEmail());
                    tenant.setCountryCode(request.countryCode());
                    tenant.setTimezone(request.timezone());
                    tenant.setMembershipModel(request.membershipModel());
                    tenant.setKeycloakRealm(realmName);
                    tenant.setCreatedAt(Instant.now());
                    tenant.setUpdatedAt(Instant.now());

                    return tenantRepository.save(tenant)
                            .flatMap(saved -> schemaProvisioning.provisionSchema(schemaName)
                                    .then(keycloakRealmService.createRealm(realmName, saved))
                                    .then(createDefaultCurrencyConfig(saved.getId(), request.defaultCurrencyCode()))
                                    .then(publishAuditEvent(saved, null, actorId, "CREATE"))
                                    .then(eventPublisher.publishTenantProvisioned(saved))
                                    .thenReturn(saved));
                });
    }

    @Transactional
    public Mono<Tenant> update(UUID id, UpdateTenantRequest request, String actorId) {
        return tenantRepository.findById(id)
                .switchIfEmpty(Mono.error(new TenantNotFoundException(id)))
                .flatMap(existing -> {
                    Tenant old = copyTenant(existing);

                    if (request.name() != null) existing.setName(request.name());
                    if (request.domain() != null) existing.setDomain(request.domain());
                    if (request.planId() != null) existing.setPlanId(request.planId());
                    if (request.contactEmail() != null) existing.setContactEmail(request.contactEmail());
                    if (request.timezone() != null) existing.setTimezone(request.timezone());
                    if (request.membershipModel() != null) existing.setMembershipModel(request.membershipModel());
                    if (request.settings() != null) existing.setSettings(request.settings());
                    if (request.branding() != null) existing.setBranding(request.branding());
                    existing.setUpdatedAt(Instant.now());

                    return tenantRepository.save(existing)
                            .flatMap(saved -> publishAuditEvent(saved, old, actorId, "UPDATE")
                                    .thenReturn(saved));
                });
    }

    @Transactional
    public Mono<Tenant> suspend(UUID id, String actorId) {
        return tenantRepository.findById(id)
                .switchIfEmpty(Mono.error(new TenantNotFoundException(id)))
                .flatMap(tenant -> {
                    Tenant old = copyTenant(tenant);
                    tenant.setStatus("suspended");
                    tenant.setUpdatedAt(Instant.now());
                    return tenantRepository.save(tenant)
                            .flatMap(saved -> publishAuditEvent(saved, old, actorId, "UPDATE")
                                    .then(eventPublisher.publishTenantSuspended(saved))
                                    .thenReturn(saved));
                });
    }

    @Transactional
    public Mono<Tenant> activate(UUID id, String actorId) {
        return tenantRepository.findById(id)
                .switchIfEmpty(Mono.error(new TenantNotFoundException(id)))
                .flatMap(tenant -> {
                    Tenant old = copyTenant(tenant);
                    tenant.setStatus("active");
                    tenant.setUpdatedAt(Instant.now());
                    return tenantRepository.save(tenant)
                            .flatMap(saved -> publishAuditEvent(saved, old, actorId, "UPDATE")
                                    .thenReturn(saved));
                });
    }

    private Mono<Void> createDefaultCurrencyConfig(UUID tenantId, String currencyCode) {
        TenantCurrencyConfig config = new TenantCurrencyConfig();
        config.setId(UUID.randomUUID());
        config.setTenantId(tenantId);
        config.setCurrencyCode(currencyCode);
        config.setIsDefault(true);
        config.setIsActive(true);
        return currencyConfigRepository.save(config).then();
    }

    private Mono<Void> publishAuditEvent(Tenant current, Tenant previous, String actorId, String action) {
        var event = AuditEvent.create(
                "platform",
                "Tenant",
                current.getId().toString(),
                action,
                actorId,
                null,
                previous != null ? Map.of("name", previous.getName(), "status", previous.getStatus()) : null,
                Map.of("name", current.getName(), "status", current.getStatus()),
                new String[]{"name", "status", "settings", "branding"},
                UUID.randomUUID().toString()
        );
        return auditPublisher.publish(event);
    }

    private Tenant copyTenant(Tenant source) {
        Tenant copy = new Tenant();
        copy.setId(source.getId());
        copy.setName(source.getName());
        copy.setSlug(source.getSlug());
        copy.setDomain(source.getDomain());
        copy.setSchemaName(source.getSchemaName());
        copy.setPlanId(source.getPlanId());
        copy.setStatus(source.getStatus());
        copy.setSettings(source.getSettings());
        copy.setBranding(source.getBranding());
        copy.setContactEmail(source.getContactEmail());
        copy.setCountryCode(source.getCountryCode());
        copy.setTimezone(source.getTimezone());
        copy.setMembershipModel(source.getMembershipModel());
        copy.setKeycloakRealm(source.getKeycloakRealm());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }
}
