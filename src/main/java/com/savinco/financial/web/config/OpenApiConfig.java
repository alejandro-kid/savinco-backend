package com.savinco.financial.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Savinco Financial API")
                        .version("1.0.0")
                        .description("API for managing financial data by country with automatic currency conversion to USD")
                        .contact(new Contact()
                                .name("Savinco Development Team")
                                .email("dev@savinco.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://savinco.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.savinco.com")
                                .description("Production server")
                ));
    }
}

