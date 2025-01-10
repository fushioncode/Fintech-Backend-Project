package com.fintech.bepc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SwaggerConfig {

    private io.swagger.v3.oas.models.security.SecurityScheme createBearerAuthScheme() {
        return new io.swagger.v3.oas.models.security.SecurityScheme()
                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER);
    }

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${openapi.service.localUrl}") String localUrl,
            @Value("${openapi.service.devUrl}") String devUrl,
            @Value("${openapi.service.prodUrl}") String prodUrl,
            @Value("${openapi.service.title}") String title,
            @Value("${openapi.service.version}") String version,
            @Value("${openapi.service.description}") String description
    ) {

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", createBearerAuthScheme()))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title(title)
                        .description(description)
                        .version("1.0")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Backyard Technologies")
                                .email("info@backyard.ng")
                                .url("https://backyard.ng"))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("API License")
                                .url("https://backyard.ng")))
                .addServersItem(new Server().url(localUrl).description("Local development server"))
                .addServersItem(new Server().url(devUrl).description("Dev development server"))
                .addServersItem(new Server().url(prodUrl).description("Prod development server"))
                .addTagsItem(new Tag().name("Authorization Service").description("Operations related to Fintech Backend"));
    }

}
