package com.fiap.pagamento.service;

import com.fiap.pagamento.dto.request.PedidoRequestDTO;
import com.fiap.pagamento.dto.response.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoDTO);
    PedidoResponseDTO consultarPedido(Long id);
    List<PedidoResponseDTO> listarPedidos();
    PedidoResponseDTO atualizarStatus(Long id, String status);
}
