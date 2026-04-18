package com.medfund.user.service;

import com.medfund.shared.audit.AuditEvent;
import com.medfund.shared.audit.AuditPublisher;
import com.medfund.user.dto.CreateStaffUserRequest;
import com.medfund.user.dto.UpdateStaffUserRequest;
import com.medfund.user.entity.StaffUser;
import com.medfund.user.repository.StaffUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StaffUserService {

    private static final Logger log = LoggerFactory.getLogger(StaffUserService.class);
    private static final String PLATFORM_REALM = "medfund-platform";
    private static final String PLATFORM_TENANT = "platform";

    private final StaffUserRepository repository;
    private final KeycloakSyncService keycloakSyncService;
    private final AuditPublisher auditPublisher;

    public StaffUserService(StaffUserRepository repository,
                            KeycloakSyncService keycloakSyncService,
                            AuditPublisher auditPublisher) {
        this.repository = repository;
        this.keycloakSyncService = keycloakSyncService;
        this.auditPublisher = auditPublisher;
    }

    public Flux<StaffUser> findAll() {
        return repository.findAllOrderByCreatedAtDesc();
    }

    public Mono<StaffUser> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Staff user not found: " + id)));
    }

    public Flux<StaffUser> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Flux<StaffUser> findByRole(String role) {
        return repository.findByRealmRole(role);
    }

    public Flux<StaffUser> search(String query) {
        return repository.search(query);
    }

    public Mono<StaffUser> create(CreateStaffUserRequest request, String actorId) {
        return repository.existsByEmail(request.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException(
                                "A staff user with email " + request.email() + " already exists"));
                    }
                    var user = new StaffUser();
                    user.setId(UUID.randomUUID());
                    user.setFirstName(request.firstName());
                    user.setLastName(request.lastName());
                    user.setEmail(request.email());
                    user.setPhone(request.phone());
                    user.setJobTitle(request.jobTitle());
                    user.setDepartment(request.department());
                    user.setRealmRole(request.realmRole());
                    user.setStatus("active");
                    user.setCreatedAt(Instant.now());
                    user.setUpdatedAt(Instant.now());
                    if (actorId != null) {
                        user.setCreatedBy(UUID.fromString(actorId));
                        user.setUpdatedBy(UUID.fromString(actorId));
                    }
                    return repository.save(user);
                })
                .flatMap(saved -> keycloakSyncService
                        .createUser(PLATFORM_REALM, saved.getEmail(),
                                saved.getFirstName(), saved.getLastName(),
                                List.of(saved.getRealmRole()))
                        .flatMap(keycloakId -> {
                            saved.setKeycloakUserId(keycloakId);
                            return repository.save(saved);
                        })
                        .defaultIfEmpty(saved)
                )
                .flatMap(saved -> {
                    var event = AuditEvent.create(
                            PLATFORM_TENANT, "StaffUser", saved.getId().toString(),
                            "CREATE", actorId, null,
                            null,
                            Map.of("email", saved.getEmail(), "realmRole", saved.getRealmRole()),
                            new String[]{"email", "realmRole", "status"},
                            UUID.randomUUID().toString());
                    return auditPublisher.publish(event).thenReturn(saved);
                });
    }

    public Mono<StaffUser> update(UUID id, UpdateStaffUserRequest request, String actorId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Staff user not found: " + id)))
                .flatMap(user -> {
                    String oldRole = user.getRealmRole();

                    if (request.firstName() != null) user.setFirstName(request.firstName());
                    if (request.lastName() != null) user.setLastName(request.lastName());
                    if (request.phone() != null) user.setPhone(request.phone());
                    if (request.jobTitle() != null) user.setJobTitle(request.jobTitle());
                    if (request.department() != null) user.setDepartment(request.department());
                    if (request.realmRole() != null) user.setRealmRole(request.realmRole());
                    user.setUpdatedAt(Instant.now());
                    if (actorId != null) user.setUpdatedBy(UUID.fromString(actorId));

                    return repository.save(user)
                            .flatMap(saved -> {
                                if (request.realmRole() != null
                                        && !request.realmRole().equals(oldRole)
                                        && saved.getKeycloakUserId() != null) {
                                    return keycloakSyncService
                                            .removeRealmRole(PLATFORM_REALM, saved.getKeycloakUserId(), oldRole)
                                            .then(keycloakSyncService.assignRealmRoles(
                                                    PLATFORM_REALM, saved.getKeycloakUserId(),
                                                    List.of(saved.getRealmRole())))
                                            .thenReturn(saved);
                                }
                                return Mono.just(saved);
                            })
                            .flatMap(saved -> {
                                var event = AuditEvent.create(
                                        PLATFORM_TENANT, "StaffUser", saved.getId().toString(),
                                        "UPDATE", actorId, null, null, null,
                                        null, UUID.randomUUID().toString());
                                return auditPublisher.publish(event).thenReturn(saved);
                            });
                });
    }

    public Mono<StaffUser> suspend(UUID id, String actorId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Staff user not found: " + id)))
                .flatMap(user -> {
                    user.setStatus("suspended");
                    user.setUpdatedAt(Instant.now());
                    if (actorId != null) user.setUpdatedBy(UUID.fromString(actorId));
                    return repository.save(user)
                            .flatMap(saved -> {
                                if (saved.getKeycloakUserId() != null) {
                                    return keycloakSyncService
                                            .disableUser(PLATFORM_REALM, saved.getKeycloakUserId())
                                            .thenReturn(saved);
                                }
                                return Mono.just(saved);
                            })
                            .flatMap(saved -> {
                                var event = AuditEvent.create(
                                        PLATFORM_TENANT, "StaffUser", saved.getId().toString(),
                                        "SUSPEND", actorId, null, null, null,
                                        null, UUID.randomUUID().toString());
                                return auditPublisher.publish(event).thenReturn(saved);
                            });
                });
    }

    public Mono<StaffUser> activate(UUID id, String actorId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Staff user not found: " + id)))
                .flatMap(user -> {
                    user.setStatus("active");
                    user.setUpdatedAt(Instant.now());
                    if (actorId != null) user.setUpdatedBy(UUID.fromString(actorId));
                    return repository.save(user)
                            .flatMap(saved -> {
                                if (saved.getKeycloakUserId() != null) {
                                    return keycloakSyncService
                                            .enableUser(PLATFORM_REALM, saved.getKeycloakUserId())
                                            .thenReturn(saved);
                                }
                                return Mono.just(saved);
                            })
                            .flatMap(saved -> {
                                var event = AuditEvent.create(
                                        PLATFORM_TENANT, "StaffUser", saved.getId().toString(),
                                        "ACTIVATE", actorId, null, null, null,
                                        null, UUID.randomUUID().toString());
                                return auditPublisher.publish(event).thenReturn(saved);
                            });
                });
    }

    public Mono<Void> delete(UUID id, String actorId) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Staff user not found: " + id)))
                .flatMap(user -> {
                    Mono<Void> keycloakCleanup = user.getKeycloakUserId() != null
                            ? keycloakSyncService.disableUser(PLATFORM_REALM, user.getKeycloakUserId())
                            : Mono.empty();
                    return keycloakCleanup
                            .then(repository.deleteById(id))
                            .then(Mono.defer(() -> {
                                var event = AuditEvent.create(
                                        PLATFORM_TENANT, "StaffUser", id.toString(),
                                        "DELETE", actorId, null,
                                        Map.of("email", user.getEmail()),
                                        null, null, UUID.randomUUID().toString());
                                return auditPublisher.publish(event);
                            }));
                });
    }
}
