package com.fiap.pedido.adapter;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.entity.PedidoEntity;
import com.fiap.pedido.mapper.PedidoMapper;
import com.fiap.pedido.pots.PedidoRepositoryPort;
import com.fiap.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public Pedido save(Pedido pedido) {
        try {
            log.debug("Salvando pedido: {}", pedido);
            PedidoEntity entity = pedidoMapper.toEntity(pedido);
            PedidoEntity saved = pedidoRepository.save(entity);
            Pedido result = pedidoMapper.toDomain(saved);
            log.debug("Pedido salvo com sucesso: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Erro ao salvar pedido: {}", pedido, e);
            throw e;
        }
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        try {
            log.debug("Buscando pedido por id: {}", id);
            Optional<Pedido> pedido = pedidoRepository.findByIdWithItens(id)
                    .map(pedidoMapper::toDomain);
            if (pedido.isPresent()) {
                log.debug("Pedido encontrado: {}", pedido.get());
            } else {
                log.warn("Pedido não encontrado para id: {}", id);
            }
            return pedido;
        } catch (Exception e) {
            log.error("Erro ao buscar pedido por id: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<Pedido> findAll() {
        try {
            log.debug("Buscando todos os pedidos");
            List<Pedido> pedidos = pedidoRepository.findAll()
                    .stream()
                    .map(pedidoMapper::toDomain)
                    .collect(Collectors.toList());
            log.debug("Pedidos encontrados: {}", pedidos.size());
            return pedidos;
        } catch (Exception e) {
            log.error("Erro ao buscar todos os pedidos", e);
            throw e;
        }
    }

    @Override
    public Pedido atualizarStatus(Long id, String status) {
        return null;
    }
}