package com.medfund.user.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medfund.shared.scheduler.JobExecutor;
import com.medfund.shared.scheduler.JobType;
import com.medfund.user.service.DependantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AgeProcessingExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(AgeProcessingExecutor.class);
    private final DependantService dependantService;
    private final ObjectMapper objectMapper;

    public AgeProcessingExecutor(DependantService dependantService, ObjectMapper objectMapper) {
        this.dependantService = dependantService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType getJobType() { return JobType.AGE_PROCESSING; }

    @Override
    public Mono<Void> execute(String tenantId, String settings) {
        log.info("Executing age processing for tenant: {}", tenantId);
        try {
            JsonNode config = objectMapper.readTree(settings);
            int maxDependantAge = config.has("maxDependantAge") ? config.get("maxDependantAge").asInt() : 21;
            int maxStudentAge = config.has("maxStudentAge") ? config.get("maxStudentAge").asInt() : 26;
            log.info("Age limits: dependant={}, student={} for tenant: {}", maxDependantAge, maxStudentAge, tenantId);
            // For now uses the service's default logic — can be enhanced to pass age limits
            return dependantService.flagOverAgeDependants();
        } catch (Exception e) {
            log.error("Failed to parse age processing settings: {}", e.getMessage());
            return dependantService.flagOverAgeDependants();
        }
    }
}
