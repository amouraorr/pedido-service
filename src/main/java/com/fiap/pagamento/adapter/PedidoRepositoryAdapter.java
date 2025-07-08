package com.fiap.pagamento.adapter;

import com.fiap.pagamento.domain.Pedido;
import com.fiap.pagamento.entity.PedidoEntity;
import com.fiap.pagamento.mapper.PedidoMapper;
import com.fiap.pagamento.pots.PedidoRepositoryPort;
import com.fiap.pagamento.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = pedidoMapper.toEntity(pedido);
        PedidoEntity saved = pedidoRepository.save(entity);
        return pedidoMapper.toDomain(saved);
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toDomain);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toDomain)
                .collect(Collectors.toList());
    }
}