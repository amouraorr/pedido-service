package com.fiap.cliente.service;

import com.fiap.cliente.dto.PedidoDTO;

import java.util.List;

public interface PedidoService {
    PedidoDTO criarPedido(PedidoDTO pedidoDTO);
    PedidoDTO consultarPedido(Long id);
    List<PedidoDTO> listarPedidos();
    PedidoDTO atualizarStatus(Long id, String status);
}