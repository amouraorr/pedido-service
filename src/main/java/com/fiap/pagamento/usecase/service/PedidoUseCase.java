package com.fiap.pagamento.usecase.service;


import com.fiap.pagamento.dto.request.PedidoRequestDTO;
import com.fiap.pagamento.dto.response.PedidoResponseDTO;

import java.util.List;

public interface PedidoUseCase {
    PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequest);
    PedidoResponseDTO consultarPedido(Long id);
    List<PedidoResponseDTO> listarPedidos();
    PedidoResponseDTO atualizarStatus(Long id, String status);
}