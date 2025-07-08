package com.fiap.pagamento.adapter;

import com.fiap.pagamento.domain.Pedido;
import com.fiap.pagamento.pots.PedidoRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final Map<Long, Pedido> pedidos = new HashMap<>();

    @Override
    public Pedido save(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public List<Pedido> findAll() {
        return new ArrayList<>(pedidos.values());
    }
}