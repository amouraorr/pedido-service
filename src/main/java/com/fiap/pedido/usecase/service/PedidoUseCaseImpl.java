package com.fiap.pedido.usecase.service;

import com.fiap.pedido.adapter.EstoqueServiceClient;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.enuns.StatusPedido;
import com.fiap.pedido.mapper.PedidoMapper;
import com.fiap.pedido.pots.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoUseCaseImpl implements PedidoUseCase {

    private final PedidoRepositoryPort pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final EstoqueServiceClient estoqueServiceClient;
    
    @Override
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO) {
        try {
            log.info("Criando pedido: {}", pedidoRequestDTO);
            Pedido pedido = pedidoMapper.toDomain(pedidoRequestDTO);
            pedido.setStatus(StatusPedido.ABERTO);
            pedido.setDataCriacao(java.time.LocalDateTime.now());
            Pedido salvo = pedidoRepository.save(pedido);
            PedidoResponseDTO response = pedidoMapper.toResponse(salvo);
            log.info("Pedido criado com sucesso: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Erro ao criar pedido: {}", pedidoRequestDTO, e);
            throw new RuntimeException("Erro ao criar pedido", e);
        }
    }

    @Override
    public PedidoResponseDTO consultarPedido(Long id) {
        try {
            log.info("Consultando pedido id: {}", id);
            return pedidoRepository.findById(id)
                    .map(pedidoMapper::toResponse)
                    .orElseThrow(() -> {
                        log.warn("Pedido não encontrado para id: {}", id);
                        return new RuntimeException("Pedido não encontrado");
                    });
        } catch (Exception e) {
            log.error("Erro ao consultar pedido id: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos() {
        try {
            log.info("Listando todos os pedidos");
            List<PedidoResponseDTO> pedidos = pedidoRepository.findAll()
                    .stream()
                    .map(pedidoMapper::toResponse)
                    .collect(Collectors.toList());
            log.info("Total de pedidos listados: {}", pedidos.size());
            return pedidos;
        } catch (Exception e) {
            log.error("Erro ao listar pedidos", e);
            throw e;
        }
    }

    @Override
    public PedidoResponseDTO atualizarStatus(Long id, String status) {
        try {
            log.info("Atualizando status do pedido id: {} para status: {}", id, status);
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Pedido não encontrado para id: {}", id);
                        return new RuntimeException("Pedido não encontrado");
                    });
            pedido.setStatus(StatusPedido.valueOf(status));
            Pedido atualizado = pedidoRepository.save(pedido);
            PedidoResponseDTO response = pedidoMapper.toResponse(atualizado);
            log.info("Status atualizado com sucesso: {}", response);
            return response;
        } catch (IllegalArgumentException e) {
            log.error("Status inválido informado: {}", status, e);
            throw new RuntimeException("Status inválido: " + status, e);
        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido id: {}", id, e);
            throw e;
        }
    }
}
