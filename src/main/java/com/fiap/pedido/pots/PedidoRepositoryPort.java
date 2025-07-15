package com.fiap.pedido.pots;

import com.fiap.pedido.domain.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepositoryPort {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();
}