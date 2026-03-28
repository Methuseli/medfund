package com.medfund.contributions.repository;

import com.medfund.contributions.entity.Contribution;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface ContributionRepository extends R2dbcRepository<Contribution, UUID> {

    @Query("SELECT * FROM contributions WHERE member_id = :memberId ORDER BY period_start DESC")
    Flux<Contribution> findByMemberId(UUID memberId);

    @Query("SELECT * FROM contributions WHERE group_id = :groupId ORDER BY period_start DESC")
    Flux<Contribution> findByGroupId(UUID groupId);

    @Query("SELECT * FROM contributions WHERE scheme_id = :schemeId ORDER BY period_start DESC")
    Flux<Contribution> findBySchemeId(UUID schemeId);

    @Query("SELECT * FROM contributions WHERE status = :status ORDER BY created_at DESC")
    Flux<Contribution> findByStatus(String status);

    @Query("SELECT * FROM contributions WHERE group_id = :groupId AND period_start = :start AND period_end = :end")
    Flux<Contribution> findByGroupIdAndPeriodStartAndPeriodEnd(UUID groupId, LocalDate start, LocalDate end);

    @Query("SELECT * FROM contributions WHERE member_id = :memberId AND status = :status")
    Flux<Contribution> findByMemberIdAndStatus(UUID memberId, String status);
}
