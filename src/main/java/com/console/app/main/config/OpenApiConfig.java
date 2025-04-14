package com.console.app.main.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customopenapi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Console App API")
                        .version("1.0")
                        .description("API Documentation")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Console App Documentation")
                        .url("https://example.com/docs"));
    }
}
