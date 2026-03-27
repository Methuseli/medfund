package com.medfund.tenancy.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.medfund.tenancy.repository")
@EnableR2dbcAuditing
public class R2dbcConfig {

    /**
     * Tenancy service operates on the public schema directly (not tenant-scoped),
     * so we don't wrap the connection factory with TenantAwareConnectionFactory here.
     * Other services (claims, contributions, etc.) will use the tenant-aware wrapper.
     */
    @Bean
    public ConnectionFactory connectionFactory(ConnectionFactory connectionFactory) {
        return connectionFactory;
    }
}
