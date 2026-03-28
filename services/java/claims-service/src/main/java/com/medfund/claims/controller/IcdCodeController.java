package com.medfund.claims.controller;

import com.medfund.claims.dto.IcdCodeResponse;
import com.medfund.claims.entity.DiagnosisProcedureMapping;
import com.medfund.claims.repository.DiagnosisProcedureMappingRepository;
import com.medfund.claims.repository.IcdCodeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/icd-codes")
@Tag(name = "ICD-10 Codes", description = "ICD-10 diagnosis code registry and diagnosis-procedure mapping")
@SecurityRequirement(name = "bearer-jwt")
public class IcdCodeController {

    private final IcdCodeRepository icdCodeRepository;
    private final DiagnosisProcedureMappingRepository diagnosisProcedureMappingRepository;

    public IcdCodeController(IcdCodeRepository icdCodeRepository,
                              DiagnosisProcedureMappingRepository diagnosisProcedureMappingRepository) {
        this.icdCodeRepository = icdCodeRepository;
        this.diagnosisProcedureMappingRepository = diagnosisProcedureMappingRepository;
    }

    @GetMapping("/search")
    @Operation(summary = "Search ICD-10 codes by code or description")
    public Flux<IcdCodeResponse> search(@RequestParam String q) {
        return icdCodeRepository.searchByCodeOrDescription(q).map(IcdCodeResponse::from);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get ICD-10 code by code value")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "ICD code found"),
        @ApiResponse(responseCode = "404", description = "ICD code not found")
    })
    public Mono<IcdCodeResponse> findByCode(@PathVariable String code) {
        return icdCodeRepository.findByCode(code).map(IcdCodeResponse::from);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "List ICD-10 codes by category")
    public Flux<IcdCodeResponse> findByCategory(@PathVariable String category) {
        return icdCodeRepository.findByCategory(category).map(IcdCodeResponse::from);
    }

    @GetMapping("/mappings/{icdCode}")
    @Operation(summary = "Get diagnosis-procedure mappings for an ICD code")
    public Flux<DiagnosisProcedureMapping> findMappings(@PathVariable String icdCode) {
        return diagnosisProcedureMappingRepository.findByIcdCode(icdCode);
    }
}
