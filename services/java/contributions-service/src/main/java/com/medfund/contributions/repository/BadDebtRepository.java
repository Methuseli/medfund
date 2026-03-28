package com.medfund.contributions.repository;

import com.medfund.contributions.entity.BadDebt;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BadDebtRepository extends R2dbcRepository<BadDebt, UUID> {

    @Query("SELECT * FROM bad_debts WHERE member_id = :memberId ORDER BY created_at DESC")
    Flux<BadDebt> findByMemberId(UUID memberId);

    @Query("SELECT * FROM bad_debts WHERE group_id = :groupId ORDER BY created_at DESC")
    Flux<BadDebt> findByGroupId(UUID groupId);

    @Query("SELECT * FROM bad_debts WHERE status = :status ORDER BY created_at DESC")
    Flux<BadDebt> findByStatus(String status);

    @Query("SELECT * FROM bad_debts ORDER BY created_at DESC")
    Flux<BadDebt> findAllOrderByCreatedAtDesc();
}
