package com.medfund.user.repository;

import com.medfund.user.entity.Provider;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProviderRepository extends R2dbcRepository<Provider, UUID> {

    @Query("SELECT * FROM providers WHERE practice_number = :practiceNumber")
    Mono<Provider> findByPracticeNumber(String practiceNumber);

    @Query("SELECT * FROM providers WHERE ahfoz_number = :ahfozNumber")
    Mono<Provider> findByAhfozNumber(String ahfozNumber);

    @Query("SELECT * FROM providers WHERE status = :status ORDER BY name")
    Flux<Provider> findByStatus(String status);

    @Query("SELECT * FROM providers WHERE keycloak_user_id = :keycloakUserId")
    Mono<Provider> findByKeycloakUserId(String keycloakUserId);

    @Query("SELECT * FROM providers WHERE specialty = :specialty AND status = 'active' ORDER BY name")
    Flux<Provider> findBySpecialty(String specialty);

    @Query("SELECT * FROM providers WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) OR practice_number LIKE CONCAT('%', :query, '%') ORDER BY name")
    Flux<Provider> search(String query);

    @Query("SELECT * FROM providers ORDER BY created_at DESC")
    Flux<Provider> findAllOrderByCreatedAtDesc();

    @Query("SELECT EXISTS(SELECT 1 FROM providers WHERE practice_number = :practiceNumber)")
    Mono<Boolean> existsByPracticeNumber(String practiceNumber);
}
