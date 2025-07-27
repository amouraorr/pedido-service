package com.fiap.pedido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.fiap.pedido")
@EntityScan(basePackages = "com.fiap.pedido.entity")
@EnableJpaRepositories(basePackages = "com.fiap.pedido.repository")
@EnableFeignClients(basePackages = "com.fiap.pedido.gateway")
public class PedidoServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PedidoServiceApplication.class, args);
	}
}