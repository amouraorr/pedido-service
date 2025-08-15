package com.fiap.pedido.adapter;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.entity.PedidoEntity;
import com.fiap.pedido.enuns.StatusPedido;
import com.fiap.pedido.mapper.PedidoMapper;
import com.fiap.pedido.pots.PedidoRepositoryPort;
import com.fiap.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public Pedido save(Pedido pedido) {
        log.info("💾 Salvando pedido no banco - ID: {} Status: {}", pedido.getId(), pedido.getStatus());
        try {
            PedidoEntity entity = pedidoMapper.toEntity(pedido);

            if (entity.getItens() != null) {
                entity.getItens().forEach(item -> item.setPedido(entity));
            }

            PedidoEntity salvo = pedidoRepository.save(entity);
            log.info("✅ Pedido salvo com sucesso - ID: {}", salvo.getId());
            return pedidoMapper.toDomain(salvo);
        } catch (Exception e) {
            log.error("❌ Erro ao salvar pedido", e);
            throw e;
        }
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        log.info("🔍 Buscando pedido por ID: {}", id);
        try {
            Optional<PedidoEntity> entity = pedidoRepository.findByIdWithItens(id);
            if (entity.isPresent()) {
                log.info("✅ Pedido encontrado - ID: {} Status: {}", id, entity.get().getStatus());
                return Optional.of(pedidoMapper.toDomain(entity.get()));
            } else {
                log.warn("⚠️ Pedido não encontrado - ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("❌ Erro ao buscar pedido ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<Pedido> findAll() {
        log.info("📋 Buscando todos os pedidos");
        try {
            List<PedidoEntity> entities = pedidoRepository.findAll();
            log.info("✅ Encontrados {} pedidos", entities.size());
            return entities.stream()
                    .map(pedidoMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("❌ Erro ao buscar todos os pedidos", e);
            throw e;
        }
    }

    @Override
    public Pedido atualizarStatus(Long id, String status) {
        log.info("🔄 Atualizando status do pedido ID: {} para: {}", id, status);
        try {
            PedidoEntity entity = pedidoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("❌ Pedido não encontrado para atualização - ID: {}", id);
                        return new RuntimeException("Pedido não encontrado - ID: " + id);
                    });

            StatusPedido novoStatus;
            try {
                novoStatus = StatusPedido.valueOf(status);
                log.info("✅ Status válido: {}", status);
            } catch (IllegalArgumentException e) {
                log.error("❌ Status inválido: {}", status);
                throw new RuntimeException("Status inválido: " + status, e);
            }

            StatusPedido statusAnterior = entity.getStatus();
            entity.setStatus(novoStatus);

            PedidoEntity atualizado = pedidoRepository.save(entity);
            log.info("✅ Status atualizado com sucesso - ID: {} | {} → {}",
                    id, statusAnterior, atualizado.getStatus());

            return pedidoMapper.toDomain(atualizado);
        } catch (Exception e) {
            log.error("❌ Erro ao atualizar status do pedido ID: {}", id, e);
            throw e;
        }
    }
}