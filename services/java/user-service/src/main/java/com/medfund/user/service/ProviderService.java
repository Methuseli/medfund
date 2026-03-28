package com.medfund.user.service;

import com.medfund.shared.audit.AuditEvent;
import com.medfund.shared.audit.AuditPublisher;
import com.medfund.shared.tenant.TenantContext;
import com.medfund.user.dto.CreateProviderRequest;
import com.medfund.user.dto.UpdateProviderRequest;
import com.medfund.user.entity.Provider;
import com.medfund.user.exception.ProviderNotFoundException;
import com.medfund.user.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProviderService {

    private static final Logger log = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;
    private final AuditPublisher auditPublisher;
    private final UserEventPublisher eventPublisher;
    private final KeycloakSyncService keycloakSyncService;

    public ProviderService(ProviderRepository providerRepository,
                           AuditPublisher auditPublisher,
                           UserEventPublisher eventPublisher,
                           KeycloakSyncService keycloakSyncService) {
        this.providerRepository = providerRepository;
        this.auditPublisher = auditPublisher;
        this.eventPublisher = eventPublisher;
        this.keycloakSyncService = keycloakSyncService;
    }

    public Flux<Provider> findAll() {
        return providerRepository.findAllOrderByCreatedAtDesc();
    }

    public Mono<Provider> findById(UUID id) {
        return providerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ProviderNotFoundException(id)));
    }

    public Flux<Provider> findByStatus(String status) {
        return providerRepository.findByStatus(status);
    }

    public Flux<Provider> findBySpecialty(String specialty) {
        return providerRepository.findBySpecialty(specialty);
    }

    public Flux<Provider> search(String query) {
        return providerRepository.search(query);
    }

    public Mono<Provider> findByAhfozNumber(String ahfozNumber) {
        return providerRepository.findByAhfozNumber(ahfozNumber);
    }

    @Transactional
    public Mono<Provider> onboard(CreateProviderRequest request, String actorId) {
        var provider = new Provider();
        provider.setId(UUID.randomUUID());
        provider.setName(request.name());
        provider.setPracticeNumber(request.practiceNumber());
        provider.setAhfozNumber(request.ahfozNumber());
        provider.setSpecialty(request.specialty());
        provider.setEmail(request.email());
        provider.setPhone(request.phone());
        provider.setAddress(request.address());
        provider.setBankingDetails(request.bankingDetails());
        provider.setStatus("pending_verification");
        provider.setCreatedAt(Instant.now());
        provider.setUpdatedAt(Instant.now());
        provider.setCreatedBy(UUID.fromString(actorId));
        provider.setUpdatedBy(UUID.fromString(actorId));

        return providerRepository.save(provider)
            .flatMap(saved -> Mono.deferContextual(ctx -> {
                String tenantId = TenantContext.get(ctx);
                String realm = "tenant-" + tenantId;

                Mono<Void> keycloakSync = Mono.empty();
                if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
                    keycloakSync = keycloakSyncService.createUser(
                        realm, saved.getEmail(), saved.getName(), "",
                        List.of("provider")
                    ).flatMap(keycloakUserId -> {
                        saved.setKeycloakUserId(keycloakUserId);
                        return providerRepository.save(saved).then();
                    }).onErrorResume(e -> {
                        log.warn("Keycloak sync failed for provider {}: {}", saved.getName(), e.getMessage());
                        return Mono.empty();
                    });
                }

                return keycloakSync
                    .then(publishAudit(tenantId, saved, null, actorId, "CREATE"))
                    .then(eventPublisher.publishProviderOnboarded(saved.getId().toString(), saved.getName()))
                    .thenReturn(saved);
            }));
    }

    @Transactional
    public Mono<Provider> verifyAhfoz(UUID id, String actorId) {
        return providerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ProviderNotFoundException(id)))
            .flatMap(existing -> {
                var previous = copyProvider(existing);
                existing.setStatus("active");
                existing.setUpdatedAt(Instant.now());
                existing.setUpdatedBy(UUID.fromString(actorId));

                return providerRepository.save(existing)
                    .flatMap(saved -> Mono.deferContextual(ctx -> {
                        String tenantId = TenantContext.get(ctx);
                        return publishAudit(tenantId, saved, previous, actorId, "UPDATE")
                            .thenReturn(saved);
                    }));
            });
    }

    @Transactional
    public Mono<Provider> update(UUID id, UpdateProviderRequest request, String actorId) {
        return providerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ProviderNotFoundException(id)))
            .flatMap(existing -> {
                var previous = copyProvider(existing);

                if (request.name() != null) existing.setName(request.name());
                if (request.practiceNumber() != null) existing.setPracticeNumber(request.practiceNumber());
                if (request.ahfozNumber() != null) existing.setAhfozNumber(request.ahfozNumber());
                if (request.specialty() != null) existing.setSpecialty(request.specialty());
                if (request.email() != null) existing.setEmail(request.email());
                if (request.phone() != null) existing.setPhone(request.phone());
                if (request.address() != null) existing.setAddress(request.address());
                if (request.bankingDetails() != null) existing.setBankingDetails(request.bankingDetails());
                existing.setUpdatedAt(Instant.now());
                existing.setUpdatedBy(UUID.fromString(actorId));

                return providerRepository.save(existing)
                    .flatMap(saved -> Mono.deferContextual(ctx -> {
                        String tenantId = TenantContext.get(ctx);
                        return publishAudit(tenantId, saved, previous, actorId, "UPDATE")
                            .thenReturn(saved);
                    }));
            });
    }

    @Transactional
    public Mono<Provider> suspend(UUID id, String actorId) {
        return providerRepository.findById(id)
            .switchIfEmpty(Mono.error(new ProviderNotFoundException(id)))
            .flatMap(existing -> {
                var previous = copyProvider(existing);
                existing.setStatus("suspended");
                existing.setUpdatedAt(Instant.now());
                existing.setUpdatedBy(UUID.fromString(actorId));

                return providerRepository.save(existing)
                    .flatMap(saved -> Mono.deferContextual(ctx -> {
                        String tenantId = TenantContext.get(ctx);
                        Mono<Void> keycloakDisable = Mono.empty();
                        if (saved.getKeycloakUserId() != null) {
                            keycloakDisable = keycloakSyncService.disableUser(
                                "tenant-" + tenantId, saved.getKeycloakUserId());
                        }
                        return keycloakDisable
                            .then(publishAudit(tenantId, saved, previous, actorId, "UPDATE"))
                            .thenReturn(saved);
                    }));
            });
    }

    private Mono<Void> publishAudit(String tenantId, Provider current, Provider previous, String actorId, String action) {
        var event = AuditEvent.create(
            tenantId != null ? tenantId : "unknown",
            "Provider",
            current.getId().toString(),
            action,
            actorId,
            null,
            previous != null ? Map.of("name", previous.getName(), "status", previous.getStatus()) : null,
            Map.of("name", current.getName(), "status", current.getStatus(),
                "ahfozNumber", current.getAhfozNumber() != null ? current.getAhfozNumber() : ""),
            new String[]{"name", "status", "ahfozNumber", "bankingDetails"},
            UUID.randomUUID().toString()
        );
        return auditPublisher.publish(event);
    }

    private Provider copyProvider(Provider source) {
        var copy = new Provider();
        copy.setId(source.getId());
        copy.setName(source.getName());
        copy.setPracticeNumber(source.getPracticeNumber());
        copy.setAhfozNumber(source.getAhfozNumber());
        copy.setSpecialty(source.getSpecialty());
        copy.setEmail(source.getEmail());
        copy.setPhone(source.getPhone());
        copy.setAddress(source.getAddress());
        copy.setBankingDetails(source.getBankingDetails());
        copy.setKeycloakUserId(source.getKeycloakUserId());
        copy.setStatus(source.getStatus());
        return copy;
    }
}
