package com.fiap.pedido;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.fiap.pedido")
@EntityScan(basePackages = "com.fiap.pedido.entity")
@EnableJpaRepositories(basePackages = "com.fiap.pedido.repository")
public class PedidoServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PedidoServiceApplication.class, args);
	}
}