package com.georeference.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Acceptance Policy API")
                        .description("Description to Acceptance Policy API")
                        .version("v1.0")
                        .contact(new Contact())
                        .license(new License()))
                .externalDocs(new ExternalDocumentation()
                        .url(""));

    }
}
