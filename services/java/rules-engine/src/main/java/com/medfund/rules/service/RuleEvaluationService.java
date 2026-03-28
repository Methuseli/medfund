package com.medfund.rules.service;

import com.medfund.rules.engine.TenantRuleEngine;
import com.medfund.rules.fact.ClaimFact;
import com.medfund.rules.fact.DependantFact;
import com.medfund.rules.fact.FamilyFact;
import com.medfund.rules.fact.MemberFact;
import com.medfund.rules.fact.ProviderFact;
import com.medfund.rules.fact.RuleResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * Higher-level service that wraps {@link TenantRuleEngine} with reactive {@link Mono}
 * support and provides convenience methods for claims adjudication.
 * <p>
 * All evaluation methods schedule the blocking Drools execution on
 * {@link Schedulers#boundedElastic()} to avoid blocking the event loop.
 */
@Service
public class RuleEvaluationService {

    private final TenantRuleEngine tenantRuleEngine;

    public RuleEvaluationService(TenantRuleEngine tenantRuleEngine) {
        this.tenantRuleEngine = tenantRuleEngine;
    }

    /**
     * Evaluate a claim against the tenant's rules.
     *
     * @param tenantId the tenant identifier
     * @param claim    the claim fact
     * @param member   the member fact
     * @param provider the provider fact
     * @return a Mono emitting the list of rule results
     */
    public Mono<List<RuleResult>> evaluateClaim(String tenantId, ClaimFact claim,
                                                 MemberFact member, ProviderFact provider) {
        return Mono.fromCallable(() -> tenantRuleEngine.evaluate(tenantId, claim, member, provider))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Evaluate a claim that includes a dependant against the tenant's rules.
     *
     * @param tenantId  the tenant identifier
     * @param claim     the claim fact
     * @param member    the member fact
     * @param dependant the dependant fact
     * @param provider  the provider fact
     * @return a Mono emitting the list of rule results
     */
    public Mono<List<RuleResult>> evaluateClaimWithDependant(String tenantId, ClaimFact claim,
                                                              MemberFact member,
                                                              DependantFact dependant,
                                                              ProviderFact provider) {
        return Mono.fromCallable(() ->
                        tenantRuleEngine.evaluate(tenantId, claim, member, dependant, provider))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Evaluate a claim that includes family (pooled benefits) against the tenant's rules.
     *
     * @param tenantId the tenant identifier
     * @param claim    the claim fact
     * @param member   the member fact
     * @param family   the family fact for pooled benefit checks
     * @param provider the provider fact
     * @return a Mono emitting the list of rule results
     */
    public Mono<List<RuleResult>> evaluateClaimWithFamily(String tenantId, ClaimFact claim,
                                                           MemberFact member, FamilyFact family,
                                                           ProviderFact provider) {
        return Mono.fromCallable(() ->
                        tenantRuleEngine.evaluate(tenantId, claim, member, family, provider))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
