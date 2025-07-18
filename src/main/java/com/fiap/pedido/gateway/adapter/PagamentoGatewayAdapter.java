package com.fiap.pedido.gateway.adapter;


import com.fiap.pedido.dto.StatusPagamentoDTO;
import com.fiap.pedido.gateway.PagamentoGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class PagamentoGatewayAdapter implements PagamentoGateway {

    // Mock implementation simulating external payment processing
    @Override
    public StatusPagamentoDTO processarPagamento(String numeroCartao, Double valor) {
        log.info("Processando pagamento mock para cartão: {} valor: {}", numeroCartao, valor);

        // Simulate payment approved or declined randomly or by some logic
        boolean aprovado = valor <= 1000; // example rule: payments <= 1000 approved

        StatusPagamentoDTO status = new StatusPagamentoDTO();
        status.setPagamentoId(UUID.randomUUID().toString());
        status.setStatus(aprovado ? "APROVADO" : "RECUSADO");
        status.setDataPagamento(LocalDateTime.now());

        log.info("Pagamento mock processado com status: {}", status.getStatus());
        return status;
    }

    @Override
    public void estornarPagamento(String pagamentoId) {
        log.info("Estornando pagamento mock com id: {}", pagamentoId);
        // Mock estorno logic
    }
}