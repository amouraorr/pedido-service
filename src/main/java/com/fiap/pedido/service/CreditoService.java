package com.fiap.pedido.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import java.math.BigDecimal;

@Slf4j
@Service
public class CreditoService {

    @Value("${app.services.credito.url:http://localhost:8085}")
    private String creditoServiceUrl;

    private final RestTemplate restTemplate;

    public CreditoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Verifica se o cliente tem crédito suficiente
     * @param clienteId ID do cliente
     * @param valor Valor a ser verificado
     * @return true se o cliente tem crédito suficiente
     */
    public boolean verificarCredito(Long clienteId, BigDecimal valor) {
        try {
            log.info("💳 Verificando crédito para cliente ID: {} - Valor: {}", clienteId, valor);

            String url = creditoServiceUrl + "/credito/verificar/" + clienteId + "?valor=" + valor;
            Boolean temCredito = restTemplate.getForObject(url, Boolean.class);

            log.info("💰 Resultado verificação crédito - Cliente {}: {}", clienteId,
                    temCredito ? "CRÉDITO APROVADO" : "CRÉDITO NEGADO");

            return Boolean.TRUE.equals(temCredito);

        } catch (ResourceAccessException e) {
            log.error("❌ Erro ao conectar com serviço de crédito: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao verificar crédito: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Debita valor do crédito do cliente
     * @param clienteId ID do cliente
     * @param valor Valor a ser debitado
     * @return true se o débito foi bem-sucedido
     */
    public boolean debitarCredito(Long clienteId, BigDecimal valor) {
        try {
            log.info("💸 Debitando crédito do cliente ID: {} - Valor: {}", clienteId, valor);

            String url = creditoServiceUrl + "/credito/debitar/" + clienteId + "?valor=" + valor;
            Boolean debitado = restTemplate.postForObject(url, null, Boolean.class);

            log.info("✅ Resultado débito crédito - Cliente {}: {}", clienteId,
                    debitado ? "DÉBITO EFETUADO" : "FALHA NO DÉBITO");

            return Boolean.TRUE.equals(debitado);

        } catch (Exception e) {
            log.error("❌ Erro ao debitar crédito: {}", e.getMessage(), e);
            return false;
        }
    }
}