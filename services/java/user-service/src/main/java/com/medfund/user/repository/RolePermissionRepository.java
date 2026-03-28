package com.medfund.user.repository;

import com.medfund.user.entity.RolePermission;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RolePermissionRepository extends R2dbcRepository<RolePermission, UUID> {

    @Query("SELECT * FROM role_permissions WHERE role_id = :roleId")
    Flux<RolePermission> findByRoleId(UUID roleId);

    @Query("DELETE FROM role_permissions WHERE role_id = :roleId")
    Mono<Void> deleteByRoleId(UUID roleId);
}
