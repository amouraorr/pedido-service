package com.fiap.pedido.usecase.service;


import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;

import java.util.List;

public interface PedidoUseCase {
    PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequest);
    PedidoResponseDTO consultarPedido(Long id);
    List<PedidoResponseDTO> listarPedidos();
    PedidoResponseDTO atualizarStatus(Long id, String status);
}