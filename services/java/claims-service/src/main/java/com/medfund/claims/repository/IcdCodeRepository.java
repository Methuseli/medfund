package com.medfund.claims.repository;

import com.medfund.claims.entity.IcdCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IcdCodeRepository extends R2dbcRepository<IcdCode, UUID> {

    @Query("SELECT * FROM icd_codes WHERE code = :code")
    Mono<IcdCode> findByCode(String code);

    @Query("SELECT * FROM icd_codes WHERE LOWER(code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY code")
    Flux<IcdCode> searchByCodeOrDescription(String query);

    @Query("SELECT * FROM icd_codes WHERE category = :category")
    Flux<IcdCode> findByCategory(String category);
}
