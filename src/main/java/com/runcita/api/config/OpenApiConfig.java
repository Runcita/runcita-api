package com.runcita.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Runcita API")
                        .description("The REST API of Runcita")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT"))
                        .contact(new Contact()
                                .email("runcitaapp@gmail.com")
                                .name("Runcita"))
                        .version("1.0.0"));
    }
}
