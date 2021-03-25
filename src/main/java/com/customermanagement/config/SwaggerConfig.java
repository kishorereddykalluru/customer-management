package com.customermanagement.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes the application usage of Swagger to expose API documentation for customers of the service.
 */
@Configuration
public class SwaggerConfig {
    /**
     * Builds Swagger API Documentation containing title and description for the service.
     *
     * @return APIInfo instance
     */
    @Bean
    public OpenAPI customerApi(){
        return new OpenAPI().info(new Info()
                .title("Customer Management Service")
                .description("Customer Management service allows user to look for list customers")
                .version("1.0")
                .contact(new Contact()
                    .name("Kishore")
                        .url("abc")
                        .email("kishorereddykalluru@gmail.com")))
           .externalDocs(new ExternalDocumentation()
           .description("Technical Reference")
           .url("xyz"));
    }
}
