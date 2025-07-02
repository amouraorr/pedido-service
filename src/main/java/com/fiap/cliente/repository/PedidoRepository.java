package com.fiap.cliente.repository;

import com.fiap.cliente.domain.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();
}
