package com.fiap.pagamento.config.swagger;

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
                        .title("PÓS GRADUAÇÃO - FIAP 2025 - SERVIÇO DE PEDIDO")
                        .version("1.0.0")
                        .description("Microsserviço responsável pelo recebimento, processamento e gerenciamento de pedidos de clientes. Controla o ciclo de vida do pedido, desde a criação até o fechamento, integrando-se com os serviços de estoque e pagamento para garantir o processamento correto de cada solicitação."));
    }
}