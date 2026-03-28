package com.medfund.user.service;

import com.medfund.user.entity.WaitingPeriodRule;
import com.medfund.user.repository.WaitingPeriodRuleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class WaitingPeriodService {

    private final WaitingPeriodRuleRepository waitingPeriodRuleRepository;

    public WaitingPeriodService(WaitingPeriodRuleRepository waitingPeriodRuleRepository) {
        this.waitingPeriodRuleRepository = waitingPeriodRuleRepository;
    }

    public Flux<WaitingPeriodRule> findBySchemeId(UUID schemeId) {
        return waitingPeriodRuleRepository.findBySchemeId(schemeId);
    }

    public Mono<Boolean> isWaitingPeriodSatisfied(UUID schemeId, String conditionType, LocalDate enrollmentDate) {
        return waitingPeriodRuleRepository.findBySchemeId(schemeId)
            .filter(rule -> rule.getConditionType().equals(conditionType))
            .next()
            .map(rule -> {
                LocalDate eligibleDate = enrollmentDate.plusDays(rule.getWaitingDays());
                return !LocalDate.now().isBefore(eligibleDate);
            })
            .defaultIfEmpty(true);
    }
}
