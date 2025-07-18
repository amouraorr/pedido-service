package com.fiap.pedido.gateway;

import com.fiap.pedido.dto.StatusPagamentoDTO;

public interface PagamentoGateway {
    StatusPagamentoDTO processarPagamento(String numeroCartao, Double valor);
    void estornarPagamento(String pagamentoId);
}