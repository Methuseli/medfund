package com.medfund.rules.service;

import com.medfund.rules.model.RuleCategory;
import com.medfund.rules.model.RuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RuleTemplateServiceTest {

    private RuleTemplateService service;

    @BeforeEach
    void setUp() {
        service = new RuleTemplateService();
    }

    @Test
    void getDefaultRules_returnsNonEmpty() {
        List<RuleDefinition> rules = service.getDefaultRules();

        assertThat(rules).isNotEmpty();
    }

    @Test
    void getDefaultRules_coversAllCategories() {
        List<RuleDefinition> rules = service.getDefaultRules();

        Set<String> categories = rules.stream()
                .map(RuleDefinition::getCategory)
                .collect(Collectors.toSet());

        Set<String> expectedCategories = Arrays.stream(RuleCategory.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        assertThat(categories).containsAll(expectedCategories);
    }

    @Test
    void getDefaultRules_allHaveValidStructure() {
        List<RuleDefinition> rules = service.getDefaultRules();

        assertThat(rules).allSatisfy(rule -> {
            assertThat(rule.getName()).isNotNull().isNotBlank();
            assertThat(rule.getCategory()).isNotNull().isNotBlank();
            assertThat(rule.getConditions()).isNotNull();
            assertThat(rule.getAction()).isNotNull();
        });
    }
}
