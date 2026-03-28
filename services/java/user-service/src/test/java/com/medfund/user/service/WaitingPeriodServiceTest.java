package com.medfund.user.service;

import com.medfund.user.entity.WaitingPeriodRule;
import com.medfund.user.repository.WaitingPeriodRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaitingPeriodServiceTest {

    @Mock
    private WaitingPeriodRuleRepository waitingPeriodRuleRepository;

    @InjectMocks
    private WaitingPeriodService waitingPeriodService;

    @Test
    void findBySchemeId_returnsRules() {
        var schemeId = UUID.randomUUID();
        var rule1 = createTestRule(schemeId, "maternity", 270);
        var rule2 = createTestRule(schemeId, "dental", 90);

        when(waitingPeriodRuleRepository.findBySchemeId(schemeId)).thenReturn(Flux.just(rule1, rule2));

        StepVerifier.create(waitingPeriodService.findBySchemeId(schemeId))
            .expectNext(rule1)
            .expectNext(rule2)
            .verifyComplete();

        verify(waitingPeriodRuleRepository).findBySchemeId(schemeId);
    }

    @Test
    void isWaitingPeriodSatisfied_periodPassed_returnsTrue() {
        var schemeId = UUID.randomUUID();
        var rule = createTestRule(schemeId, "dental", 90);

        when(waitingPeriodRuleRepository.findBySchemeId(schemeId)).thenReturn(Flux.just(rule));

        // Enrollment date far in the past, so 90-day waiting period has passed
        LocalDate enrollmentDate = LocalDate.now().minusDays(365);

        StepVerifier.create(waitingPeriodService.isWaitingPeriodSatisfied(schemeId, "dental", enrollmentDate))
            .assertNext(result -> assertThat(result).isTrue())
            .verifyComplete();
    }

    @Test
    void isWaitingPeriodSatisfied_periodNotPassed_returnsFalse() {
        var schemeId = UUID.randomUUID();
        var rule = createTestRule(schemeId, "maternity", 270);

        when(waitingPeriodRuleRepository.findBySchemeId(schemeId)).thenReturn(Flux.just(rule));

        // Enrollment date recent, so 270-day waiting period has not passed
        LocalDate enrollmentDate = LocalDate.now().minusDays(10);

        StepVerifier.create(waitingPeriodService.isWaitingPeriodSatisfied(schemeId, "maternity", enrollmentDate))
            .assertNext(result -> assertThat(result).isFalse())
            .verifyComplete();
    }

    @Test
    void isWaitingPeriodSatisfied_noRule_returnsTrue() {
        var schemeId = UUID.randomUUID();

        // No rules for this scheme
        when(waitingPeriodRuleRepository.findBySchemeId(schemeId)).thenReturn(Flux.empty());

        StepVerifier.create(waitingPeriodService.isWaitingPeriodSatisfied(schemeId, "dental", LocalDate.now()))
            .assertNext(result -> assertThat(result).isTrue())
            .verifyComplete();
    }

    private WaitingPeriodRule createTestRule(UUID schemeId, String conditionType, int waitingDays) {
        var rule = new WaitingPeriodRule();
        rule.setId(UUID.randomUUID());
        rule.setSchemeId(schemeId);
        rule.setConditionType(conditionType);
        rule.setWaitingDays(waitingDays);
        rule.setDescription("Waiting period for " + conditionType);
        rule.setCreatedAt(Instant.now());
        return rule;
    }
}
