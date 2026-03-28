package com.medfund.shared.scheduler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scheduled-jobs")
@Tag(name = "Scheduled Jobs", description = "Tenant-configurable scheduled job management")
@SecurityRequirement(name = "bearer-jwt")
public class ScheduledJobController {

    private final ScheduledJobService scheduledJobService;

    public ScheduledJobController(ScheduledJobService scheduledJobService) {
        this.scheduledJobService = scheduledJobService;
    }

    @GetMapping
    @Operation(summary = "List all scheduled job configurations")
    public Flux<ScheduledJobConfig> findAll() {
        return scheduledJobService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job config by ID")
    public Mono<ScheduledJobConfig> findById(@PathVariable UUID id) {
        return scheduledJobService.findById(id);
    }

    @GetMapping("/types")
    @Operation(summary = "List available job types")
    public Flux<JobTypeInfo> listJobTypes() {
        return Flux.fromArray(JobType.values())
            .map(jt -> new JobTypeInfo(jt.name(), jt.getDisplayName(), jt.getDescription()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new scheduled job config")
    public Mono<ScheduledJobConfig> create(
            @RequestParam String jobType,
            @RequestParam String name,
            @RequestParam String cronExpression,
            @RequestParam(required = false) String settings,
            Principal principal) {
        return scheduledJobService.create(jobType, name, cronExpression, settings, principal.getName());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job config (schedule, settings, enabled)")
    public Mono<ScheduledJobConfig> update(
            @PathVariable UUID id,
            @RequestParam(required = false) String cronExpression,
            @RequestParam(required = false) String settings,
            @RequestParam(required = false) Boolean isEnabled,
            Principal principal) {
        return scheduledJobService.update(id, cronExpression, settings, isEnabled, principal.getName());
    }

    @PostMapping("/{id}/enable")
    @Operation(summary = "Enable a scheduled job")
    public Mono<ScheduledJobConfig> enable(@PathVariable UUID id, Principal principal) {
        return scheduledJobService.enable(id, principal.getName());
    }

    @PostMapping("/{id}/disable")
    @Operation(summary = "Disable a scheduled job")
    public Mono<ScheduledJobConfig> disable(@PathVariable UUID id, Principal principal) {
        return scheduledJobService.disable(id, principal.getName());
    }

    @PostMapping("/seed-defaults")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Seed default job configs for the current tenant")
    public Mono<Void> seedDefaults(Principal principal) {
        return scheduledJobService.seedDefaults(principal.getName());
    }

    public record JobTypeInfo(String type, String displayName, String description) {}
}
