package com.medfund.user.service;

import com.medfund.shared.audit.AuditEvent;
import com.medfund.shared.audit.AuditPublisher;
import com.medfund.shared.tenant.TenantContext;
import com.medfund.user.dto.CreateDependantRequest;
import com.medfund.user.entity.Dependant;
import com.medfund.user.exception.DependantNotFoundException;
import com.medfund.user.repository.DependantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class DependantService {

    private final DependantRepository dependantRepository;
    private final AuditPublisher auditPublisher;

    public DependantService(DependantRepository dependantRepository, AuditPublisher auditPublisher) {
        this.dependantRepository = dependantRepository;
        this.auditPublisher = auditPublisher;
    }

    public Flux<Dependant> findByMemberId(UUID memberId) {
        return dependantRepository.findByMemberId(memberId);
    }

    public Mono<Dependant> findById(UUID id) {
        return dependantRepository.findById(id)
            .switchIfEmpty(Mono.error(new DependantNotFoundException(id)));
    }

    @Transactional
    public Mono<Dependant> create(CreateDependantRequest request, String actorId) {
        var dependant = new Dependant();
        dependant.setId(UUID.randomUUID());
        dependant.setMemberId(request.memberId());
        dependant.setFirstName(request.firstName());
        dependant.setLastName(request.lastName());
        dependant.setDateOfBirth(request.dateOfBirth());
        dependant.setGender(request.gender());
        dependant.setRelationship(request.relationship());
        dependant.setNationalId(request.nationalId());
        dependant.setStatus("active");
        dependant.setCreatedAt(Instant.now());
        dependant.setUpdatedAt(Instant.now());
        dependant.setCreatedBy(UUID.fromString(actorId));
        dependant.setUpdatedBy(UUID.fromString(actorId));

        return dependantRepository.save(dependant)
            .flatMap(saved -> Mono.deferContextual(ctx -> {
                String tenantId = TenantContext.get(ctx);
                var event = AuditEvent.create(
                    tenantId != null ? tenantId : "unknown", "Dependant", saved.getId().toString(),
                    "CREATE", actorId, null, null,
                    Map.of("firstName", saved.getFirstName(), "lastName", saved.getLastName(),
                        "relationship", saved.getRelationship(), "memberId", saved.getMemberId().toString()),
                    new String[]{"firstName", "lastName", "relationship"},
                    UUID.randomUUID().toString()
                );
                return auditPublisher.publish(event).thenReturn(saved);
            }));
    }

    @Transactional
    public Mono<Dependant> remove(UUID id, String actorId) {
        return dependantRepository.findById(id)
            .switchIfEmpty(Mono.error(new DependantNotFoundException(id)))
            .flatMap(existing -> {
                existing.setStatus("removed");
                existing.setUpdatedAt(Instant.now());
                existing.setUpdatedBy(UUID.fromString(actorId));
                return dependantRepository.save(existing);
            })
            .flatMap(saved -> Mono.deferContextual(ctx -> {
                String tenantId = TenantContext.get(ctx);
                var event = AuditEvent.create(
                    tenantId != null ? tenantId : "unknown", "Dependant", saved.getId().toString(),
                    "UPDATE", actorId, null,
                    Map.of("status", "active"),
                    Map.of("status", "removed"),
                    new String[]{"status"},
                    UUID.randomUUID().toString()
                );
                return auditPublisher.publish(event).thenReturn(saved);
            }));
    }
}
