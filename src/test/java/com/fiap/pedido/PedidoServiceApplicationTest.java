package com.fiap.pedido;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PedidoServiceApplicationTest {

    @Test
    void deveIniciarAplicacaoComMain() {
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

            springApplicationMock.when(() -> SpringApplication.run(PedidoServiceApplication.class, new String[]{}))
                    .thenReturn(mockContext);

            PedidoServiceApplication.main(new String[]{});

            springApplicationMock.verify(() -> SpringApplication.run(PedidoServiceApplication.class, new String[]{}));
        }
    }

    @Test
    void deveIniciarAplicacaoComArgumentos() {
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            String[] args = {"--spring.profiles.active=test"};

            springApplicationMock.when(() -> SpringApplication.run(PedidoServiceApplication.class, args))
                    .thenReturn(mockContext);

            PedidoServiceApplication.main(args);

            springApplicationMock.verify(() -> SpringApplication.run(PedidoServiceApplication.class, args));
        }
    }

    @Test
    void deveVerificarAnotacoesDaClasse() {
        Class<PedidoServiceApplication> clazz = PedidoServiceApplication.class;

        assertTrue(clazz.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
        assertTrue(clazz.isAnnotationPresent(org.springframework.boot.autoconfigure.domain.EntityScan.class));
        assertTrue(clazz.isAnnotationPresent(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class));
        assertTrue(clazz.isAnnotationPresent(org.springframework.cloud.openfeign.EnableFeignClients.class));
    }

    @Test
    void deveVerificarPackagesScanDaAnotacaoSpringBootApplication() {
        org.springframework.boot.autoconfigure.SpringBootApplication annotation =
                PedidoServiceApplication.class.getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);

        String[] scanBasePackages = annotation.scanBasePackages();
        assertEquals(1, scanBasePackages.length);
        assertEquals("com.fiap.pedido", scanBasePackages[0]);
    }

    @Test
    void deveVerificarPackagesDaAnotacaoEntityScan() {
        org.springframework.boot.autoconfigure.domain.EntityScan annotation =
                PedidoServiceApplication.class.getAnnotation(org.springframework.boot.autoconfigure.domain.EntityScan.class);

        String[] basePackages = annotation.basePackages();
        assertEquals(1, basePackages.length);
        assertEquals("com.fiap.pedido.entity", basePackages[0]);
    }

    @Test
    void deveVerificarPackagesDaAnotacaoEnableJpaRepositories() {
        org.springframework.data.jpa.repository.config.EnableJpaRepositories annotation =
                PedidoServiceApplication.class.getAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class);

        String[] basePackages = annotation.basePackages();
        assertEquals(1, basePackages.length);
        assertEquals("com.fiap.pedido.repository", basePackages[0]);
    }

    @Test
    void deveVerificarPackagesDaAnotacaoEnableFeignClients() {
        org.springframework.cloud.openfeign.EnableFeignClients annotation =
                PedidoServiceApplication.class.getAnnotation(org.springframework.cloud.openfeign.EnableFeignClients.class);

        String[] basePackages = annotation.basePackages();
        assertEquals(2, basePackages.length);
        assertEquals("com.fiap.pedido.adapter", basePackages[0]);
        assertEquals("com.fiap.pedido.gateway", basePackages[1]);
    }
}