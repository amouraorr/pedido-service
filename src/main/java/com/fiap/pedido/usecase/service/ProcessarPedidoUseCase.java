package com.fiap.pedido.usecase.service;

import com.fiap.pedido.dto.request.PedidoRequestDTO;

public interface ProcessarPedidoUseCase {
    void processarPedido(PedidoRequestDTO pedidoRequestDTO);
}
