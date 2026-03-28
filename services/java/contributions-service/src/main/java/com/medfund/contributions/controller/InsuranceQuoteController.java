package com.medfund.contributions.controller;

import com.medfund.contributions.dto.InsuranceQuoteRequest;
import com.medfund.contributions.dto.InsuranceQuoteResponse;
import com.medfund.contributions.service.InsuranceQuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/quotes")
@Tag(name = "Insurance Quotes", description = "Self-service insurance quotation for prospective members")
public class InsuranceQuoteController {

    private final InsuranceQuoteService quoteService;

    public InsuranceQuoteController(InsuranceQuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Get an insurance quote",
        description = "Prospective members can request an automatic price quote. No authentication required. " +
                      "System calculates monthly premiums based on scheme age group pricing for the member and their dependants.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Quote generated"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Scheme not found")
    })
    public Mono<InsuranceQuoteResponse> generateQuote(@Valid @RequestBody InsuranceQuoteRequest request) {
        return quoteService.generateQuote(request);
    }

    @GetMapping("/{quoteNumber}")
    @Operation(summary = "Retrieve a saved quote by number",
        description = "Look up a previously generated quote by its reference number")
    public Mono<InsuranceQuoteResponse> getQuote(@PathVariable String quoteNumber) {
        return quoteService.getByQuoteNumber(quoteNumber);
    }
}
