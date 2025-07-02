package com.fiap.cliente.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PÓS GRADUAÇÃO - FIAP 2025 - PROJETO HOSPITALAR - SERVIÇO DE AGENDAMENTO")
                        .version("1.0.0")
                        .description("Projeto hospitalar acessível a médicos, enfermeiros e pacientes."));
    }
}