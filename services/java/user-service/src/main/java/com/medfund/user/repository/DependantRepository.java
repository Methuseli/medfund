package com.medfund.user.repository;

import com.medfund.user.entity.Dependant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DependantRepository extends R2dbcRepository<Dependant, UUID> {

    @Query("SELECT * FROM dependants WHERE member_id = :memberId ORDER BY first_name")
    Flux<Dependant> findByMemberId(UUID memberId);

    @Query("SELECT * FROM dependants WHERE member_id = :memberId AND status = :status")
    Flux<Dependant> findByMemberIdAndStatus(UUID memberId, String status);
}
