package com.medfund.shared.scheduler;

import com.medfund.shared.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JobDispatcher {

    private static final Logger log = LoggerFactory.getLogger(JobDispatcher.class);

    private final ScheduledJobRepository jobRepository;
    private final Map<JobType, JobExecutor> executors;

    public JobDispatcher(ScheduledJobRepository jobRepository, List<JobExecutor> executorList) {
        this.jobRepository = jobRepository;
        this.executors = executorList.stream()
            .collect(Collectors.toMap(JobExecutor::getJobType, Function.identity()));
        log.info("JobDispatcher initialized with {} executors: {}", executors.size(),
            executors.keySet().stream().map(Enum::name).collect(Collectors.joining(", ")));
    }

    /**
     * Polls every 5 minutes for due jobs across all tenants.
     * Each service only has executors for its own job types, so jobs without
     * a matching executor in this service are silently skipped.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void dispatch() {
        try {
            jobRepository.findDueJobs(Instant.now())
                .flatMap(config -> {
                    JobType jobType;
                    try {
                        jobType = JobType.valueOf(config.getJobType());
                    } catch (IllegalArgumentException e) {
                        log.debug("Unknown job type: {}, skipping", config.getJobType());
                        return Mono.empty();
                    }

                    JobExecutor executor = executors.get(jobType);
                    if (executor == null) {
                        // This service doesn't handle this job type — skip silently
                        return Mono.empty();
                    }

                    log.info("Executing job: type={}, name={}, configId={}",
                        config.getJobType(), config.getName(), config.getId());

                    return Mono.deferContextual(ctx -> {
                        String tenantId = TenantContext.get(ctx);
                        return executor.execute(tenantId != null ? tenantId : "unknown", config.getSettings())
                            .then(updateExecutionTime(config))
                            .doOnSuccess(v -> log.info("Job completed: type={}, configId={}",
                                config.getJobType(), config.getId()))
                            .doOnError(e -> log.error("Job failed: type={}, configId={}, error={}",
                                config.getJobType(), config.getId(), e.getMessage()));
                    }).onErrorResume(e -> {
                        log.error("Job execution error for {}: {}", config.getJobType(), e.getMessage());
                        return updateExecutionTime(config); // still advance to prevent retry storm
                    });
                })
                .then()
                .block();
        } catch (Exception e) {
            log.error("JobDispatcher cycle failed: {}", e.getMessage(), e);
        }
    }

    private Mono<Void> updateExecutionTime(ScheduledJobConfig config) {
        config.setLastExecutedAt(Instant.now());
        config.setNextExecutionAt(calculateNextExecution(config.getCronExpression()));
        return jobRepository.save(config).then();
    }

    /**
     * Calculate next execution time from a cron expression.
     */
    public static Instant calculateNextExecution(String cronExpression) {
        try {
            CronExpression cron = CronExpression.parse(cronExpression);
            LocalDateTime next = cron.next(LocalDateTime.now());
            return next != null ? next.toInstant(ZoneOffset.UTC) : null;
        } catch (Exception e) {
            // Fallback: 24 hours from now
            return Instant.now().plusSeconds(86400);
        }
    }
}
