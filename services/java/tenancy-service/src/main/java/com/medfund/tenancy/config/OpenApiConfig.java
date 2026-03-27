package com.medfund.tenancy.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MedFund Tenancy Service API",
                version = "1.0.0",
                description = "Tenant lifecycle management, plan management, and schema provisioning. Super admin only.",
                contact = @Contact(name = "MedFund Platform", url = "https://medfund.healthcare")
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local development"),
                @Server(url = "https://api.medfund.healthcare", description = "Production (via API Gateway)")
        }
)
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Keycloak JWT token from the medfund-platform realm"
)
public class OpenApiConfig {
}
