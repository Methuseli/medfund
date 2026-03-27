package com.medfund.tenancy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.medfund.tenancy.repository")
@EnableR2dbcAuditing
public class R2dbcConfig {
    // Tenancy service operates on the public schema directly (not tenant-scoped),
    // so we use the auto-configured ConnectionFactory from Spring Boot.
    // Other services (claims, contributions, etc.) will wrap it with TenantAwareConnectionFactory.
}
