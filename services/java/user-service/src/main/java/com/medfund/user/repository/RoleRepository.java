package com.medfund.user.repository;

import com.medfund.user.entity.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository extends R2dbcRepository<Role, UUID> {

    @Query("SELECT * FROM roles WHERE name = :name")
    Mono<Role> findByName(String name);

    @Query("SELECT * FROM roles ORDER BY name")
    Flux<Role> findAllOrderByName();

    @Query("SELECT * FROM roles WHERE is_system = :isSystem ORDER BY name")
    Flux<Role> findByIsSystem(Boolean isSystem);

    @Query("SELECT EXISTS(SELECT 1 FROM roles WHERE name = :name)")
    Mono<Boolean> existsByName(String name);
}
