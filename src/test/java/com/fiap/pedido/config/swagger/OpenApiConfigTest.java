package com.fiap.pedido.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OpenApiConfig.class)
class OpenApiConfigIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OpenAPI openAPI;

    @Test
    void deveTerBeanOpenAPINoContexto() {
        assertTrue(applicationContext.containsBean("customOpenAPI"));
    }

    @Test
    void deveTerOpenAPIInjetado() {
        assertNotNull(openAPI);
    }

    @Test
    void deveCarregarConfiguracaoCompleta() {
        assertNotNull(openAPI.getInfo());
        assertEquals("PÓS GRADUAÇÃO - FIAP 2025 - SERVIÇO DE PEDIDO", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getDescription());
    }
}