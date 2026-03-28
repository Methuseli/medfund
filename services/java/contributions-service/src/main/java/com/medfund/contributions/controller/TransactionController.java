package com.medfund.contributions.controller;

import com.medfund.contributions.dto.RecordTransactionRequest;
import com.medfund.contributions.dto.TransactionResponse;
import com.medfund.contributions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Payment transaction recording and lookup")
@SecurityRequirement(name = "bearer-jwt")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List all transactions")
    public Flux<TransactionResponse> findAll() {
        return transactionService.findAll().map(TransactionResponse::from);
    }

    @GetMapping("/contribution/{contributionId}")
    @Operation(summary = "List transactions by contribution")
    public Flux<TransactionResponse> findByContributionId(@PathVariable UUID contributionId) {
        return transactionService.findByContributionId(contributionId).map(TransactionResponse::from);
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "List transactions by invoice")
    public Flux<TransactionResponse> findByInvoiceId(@PathVariable UUID invoiceId) {
        return transactionService.findByInvoiceId(invoiceId).map(TransactionResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a transaction")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transaction recorded"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public Mono<TransactionResponse> record(@Valid @RequestBody RecordTransactionRequest request,
                                            Principal principal) {
        return transactionService.record(request, principal.getName()).map(TransactionResponse::from);
    }
}
