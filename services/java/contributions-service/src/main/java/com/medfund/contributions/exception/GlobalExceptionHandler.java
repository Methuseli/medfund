package com.medfund.contributions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({SchemeNotFoundException.class, ContributionNotFoundException.class, InvoiceNotFoundException.class})
    public Mono<ProblemDetail> handleNotFound(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://medfund.healthcare/errors/not-found"));
        problem.setTitle("Resource Not Found");
        return Mono.just(problem);
    }

    @ExceptionHandler(DuplicateSchemeException.class)
    public Mono<ProblemDetail> handleConflict(DuplicateSchemeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://medfund.healthcare/errors/duplicate-scheme"));
        problem.setTitle("Duplicate Scheme");
        return Mono.just(problem);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ProblemDetail> handleValidation(WebExchangeBindException ex) {
        String details = ex.getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining("; "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, details);
        problem.setType(URI.create("https://medfund.healthcare/errors/validation-failed"));
        problem.setTitle("Validation Failed");
        return Mono.just(problem);
    }
}
