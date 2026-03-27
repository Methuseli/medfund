package com.medfund.tenancy.repository;

import com.medfund.tenancy.entity.Plan;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface PlanRepository extends R2dbcRepository<Plan, UUID> {

    @Query("SELECT * FROM public.plans WHERE is_active = true ORDER BY price ASC")
    Flux<Plan> findAllActive();
}
