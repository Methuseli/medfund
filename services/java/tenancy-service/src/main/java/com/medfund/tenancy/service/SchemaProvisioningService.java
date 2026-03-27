package com.medfund.tenancy.service;

import io.r2dbc.spi.ConnectionFactory;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Provisions a new PostgreSQL schema for a tenant and runs Flyway migrations.
 * Schema creation uses R2DBC; Flyway uses JDBC (blocking, run on boundedElastic).
 */
@Service
public class SchemaProvisioningService {

    private static final Logger log = LoggerFactory.getLogger(SchemaProvisioningService.class);

    private final ConnectionFactory connectionFactory;

    @Value("${spring.flyway.url}")
    private String flywayJdbcUrl;

    @Value("${spring.flyway.user}")
    private String flywayUser;

    @Value("${spring.flyway.password}")
    private String flywayPassword;

    public SchemaProvisioningService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Mono<Void> provisionSchema(String schemaName) {
        return createSchema(schemaName)
                .then(runMigrations(schemaName));
    }

    private Mono<Void> createSchema(String schemaName) {
        return Mono.from(connectionFactory.create())
                .flatMap(conn ->
                        Mono.from(conn.createStatement("CREATE SCHEMA IF NOT EXISTS " + schemaName).execute())
                                .doOnSuccess(r -> log.info("Created schema: {}", schemaName))
                                .doFinally(s -> conn.close())
                                .then()
                );
    }

    private Mono<Void> runMigrations(String schemaName) {
        return Mono.fromRunnable(() -> {
            Flyway flyway = Flyway.configure()
                    .dataSource(flywayJdbcUrl, flywayUser, flywayPassword)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
            log.info("Flyway migrations complete for schema: {}", schemaName);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
