package com.fiap.pedido.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Service
public class EstoqueService {

    @Value("${app.services.estoque.url:http://localhost:8084}")
    private String estoqueServiceUrl;

    private final RestTemplate restTemplate;

    public EstoqueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Verifica se há estoque disponível para um produto
     * @param produtoId ID do produto
     * @param quantidade Quantidade desejada
     * @return true se há estoque suficiente, false caso contrário
     */
    public boolean verificarEstoque(Long produtoId, Integer quantidade) {
        try {
            log.info("🔍 Verificando estoque para produto ID: {} - Quantidade: {}", produtoId, quantidade);

            String url = estoqueServiceUrl + "/estoque/verificar/" + produtoId + "?quantidade=" + quantidade;
            Boolean temEstoque = restTemplate.getForObject(url, Boolean.class);

            log.info("📦 Resultado verificação estoque - Produto {}: {}", produtoId,
                    temEstoque ? "DISPONÍVEL" : "INDISPONÍVEL");

            return Boolean.TRUE.equals(temEstoque);

        } catch (ResourceAccessException e) {
            log.error("❌ Erro ao conectar com serviço de estoque: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao verificar estoque: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Reserva estoque para um produto
     * @param produtoId ID do produto
     * @param quantidade Quantidade a reservar
     * @return true se a reserva foi bem-sucedida
     */
    public boolean reservarEstoque(Long produtoId, Integer quantidade) {
        try {
            log.info("🔒 Reservando estoque para produto ID: {} - Quantidade: {}", produtoId, quantidade);

            String url = estoqueServiceUrl + "/estoque/reservar/" + produtoId + "?quantidade=" + quantidade;
            Boolean reservado = restTemplate.postForObject(url, null, Boolean.class);

            log.info("✅ Resultado reserva estoque - Produto {}: {}", produtoId,
                    reservado ? "RESERVADO" : "FALHA NA RESERVA");

            return Boolean.TRUE.equals(reservado);

        } catch (Exception e) {
            log.error("❌ Erro ao reservar estoque: {}", e.getMessage(), e);
            return false;
        }
    }
}