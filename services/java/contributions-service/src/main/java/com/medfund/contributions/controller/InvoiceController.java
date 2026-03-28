package com.medfund.contributions.controller;

import com.medfund.contributions.dto.InvoiceResponse;
import com.medfund.contributions.repository.InvoiceRepository;
import com.medfund.contributions.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices", description = "Invoice generation and management")
@SecurityRequirement(name = "bearer-jwt")
public class InvoiceController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceController(BillingService billingService, InvoiceRepository invoiceRepository) {
        this.billingService = billingService;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "List invoices by group")
    public Flux<InvoiceResponse> findByGroupId(@PathVariable UUID groupId) {
        return billingService.findInvoicesByGroupId(groupId).map(InvoiceResponse::from);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "List invoices by member")
    public Flux<InvoiceResponse> findByMemberId(@PathVariable UUID memberId) {
        return billingService.findInvoicesByMemberId(memberId).map(InvoiceResponse::from);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List invoices by status")
    public Flux<InvoiceResponse> findByStatus(@PathVariable String status) {
        return invoiceRepository.findByStatus(status).map(InvoiceResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice found"),
        @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public Mono<InvoiceResponse> findById(@PathVariable UUID id) {
        return billingService.findInvoiceById(id).map(InvoiceResponse::from);
    }
}
