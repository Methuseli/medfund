package com.medfund.user.repository;

import com.medfund.user.entity.UserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRoleRepository extends R2dbcRepository<UserRole, UUID> {

    @Query("SELECT * FROM user_roles WHERE user_id = :userId")
    Flux<UserRole> findByUserId(UUID userId);

    @Query("SELECT * FROM user_roles WHERE role_id = :roleId")
    Flux<UserRole> findByRoleId(UUID roleId);

    @Query("SELECT EXISTS(SELECT 1 FROM user_roles WHERE user_id = :userId AND role_id = :roleId)")
    Mono<Boolean> existsByUserIdAndRoleId(UUID userId, UUID roleId);

    @Query("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
    Mono<Void> deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
