package com.medfund.user.repository;

import com.medfund.user.entity.StaffUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StaffUserRepository extends R2dbcRepository<StaffUser, UUID> {

    @Query("SELECT * FROM public.staff_users ORDER BY created_at DESC")
    Flux<StaffUser> findAllOrderByCreatedAtDesc();

    @Query("SELECT * FROM public.staff_users WHERE email = :email")
    Mono<StaffUser> findByEmail(String email);

    @Query("SELECT * FROM public.staff_users WHERE status = :status ORDER BY created_at DESC")
    Flux<StaffUser> findByStatus(String status);

    @Query("SELECT * FROM public.staff_users WHERE realm_role = :realmRole ORDER BY created_at DESC")
    Flux<StaffUser> findByRealmRole(String realmRole);

    @Query("SELECT EXISTS(SELECT 1 FROM public.staff_users WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);

    @Query("""
        SELECT * FROM public.staff_users
        WHERE lower(first_name) LIKE lower(concat('%', :query, '%'))
           OR lower(last_name)  LIKE lower(concat('%', :query, '%'))
           OR lower(email)      LIKE lower(concat('%', :query, '%'))
        ORDER BY created_at DESC
        """)
    Flux<StaffUser> search(String query);
}
