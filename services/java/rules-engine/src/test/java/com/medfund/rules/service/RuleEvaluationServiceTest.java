package com.medfund.rules.service;

import com.medfund.rules.engine.TenantRuleEngine;
import com.medfund.rules.fact.ClaimFact;
import com.medfund.rules.fact.DependantFact;
import com.medfund.rules.fact.FamilyFact;
import com.medfund.rules.fact.MemberFact;
import com.medfund.rules.fact.ProviderFact;
import com.medfund.rules.fact.RuleResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleEvaluationServiceTest {

    @Mock
    private TenantRuleEngine tenantRuleEngine;

    @InjectMocks
    private RuleEvaluationService ruleEvaluationService;

    @Test
    void evaluateClaim_delegatesToEngine() {
        var claim = new ClaimFact();
        claim.setClaimId("CLM-1");
        var member = new MemberFact();
        member.setMemberId("MBR-1");
        var provider = new ProviderFact();
        provider.setProviderId("PRV-1");

        var expected = List.of(new RuleResult("REJECT", "R01", "Claim rejected"));
        when(tenantRuleEngine.evaluate(eq("tenant-1"), any(), any(), any()))
                .thenReturn(expected);

        StepVerifier.create(ruleEvaluationService.evaluateClaim("tenant-1", claim, member, provider))
                .assertNext(results -> {
                    assertThat(results).hasSize(1);
                    assertThat(results.get(0).getType()).isEqualTo("REJECT");
                    assertThat(results.get(0).getCode()).isEqualTo("R01");
                    assertThat(results.get(0).getMessage()).isEqualTo("Claim rejected");
                })
                .verifyComplete();

        verify(tenantRuleEngine).evaluate(eq("tenant-1"), any(), any(), any());
    }

    @Test
    void evaluateClaimWithDependant_delegatesToEngine() {
        var claim = new ClaimFact();
        claim.setClaimId("CLM-2");
        var member = new MemberFact();
        member.setMemberId("MBR-2");
        var dependant = new DependantFact();
        dependant.setDependantId("DEP-1");
        var provider = new ProviderFact();
        provider.setProviderId("PRV-2");

        var expected = List.of(new RuleResult("APPROVE", "A01", "Dependant claim approved"));
        when(tenantRuleEngine.evaluate(eq("tenant-1"), any(), any(), any(), any()))
                .thenReturn(expected);

        StepVerifier.create(ruleEvaluationService.evaluateClaimWithDependant(
                "tenant-1", claim, member, dependant, provider))
                .assertNext(results -> {
                    assertThat(results).hasSize(1);
                    assertThat(results.get(0).getType()).isEqualTo("APPROVE");
                    assertThat(results.get(0).getCode()).isEqualTo("A01");
                })
                .verifyComplete();

        verify(tenantRuleEngine).evaluate(eq("tenant-1"), any(), any(), any(), any());
    }

    @Test
    void evaluateClaimWithFamily_delegatesToEngine() {
        var claim = new ClaimFact();
        claim.setClaimId("CLM-3");
        var member = new MemberFact();
        member.setMemberId("MBR-3");
        var family = new FamilyFact();
        var provider = new ProviderFact();
        provider.setProviderId("PRV-3");

        var expected = List.of(new RuleResult("APPROVE", "A02", "Family claim approved"));
        when(tenantRuleEngine.evaluate(eq("tenant-1"), any(), any(), any(), any()))
                .thenReturn(expected);

        StepVerifier.create(ruleEvaluationService.evaluateClaimWithFamily(
                "tenant-1", claim, member, family, provider))
                .assertNext(results -> {
                    assertThat(results).hasSize(1);
                    assertThat(results.get(0).getType()).isEqualTo("APPROVE");
                    assertThat(results.get(0).getCode()).isEqualTo("A02");
                })
                .verifyComplete();

        verify(tenantRuleEngine).evaluate(eq("tenant-1"), any(), any(), any(), any());
    }
}
