package com.fiap.pedido.usecase.service;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.domain.ItemPedido;
import com.fiap.pedido.enuns.StatusPedido;
import com.fiap.pedido.mapper.PedidoMapper;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.pots.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoUseCaseImpl implements PedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final PedidoMapper mapper;

    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequest) {
        log.info("🆕 Iniciando criação de pedido para cliente: {}", pedidoRequest.getClienteId());

        // 1. Mapear e criar pedido inicial com status ABERTO
        Pedido pedido = mapper.toDomain(pedidoRequest);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setDataCriacao(LocalDateTime.now());

        log.info("📝 Dados do pedido mapeados - Status: {} Data: {}",
                pedido.getStatus(), pedido.getDataCriacao());

        // 2. Salvar pedido inicial no banco
        Pedido pedidoSalvo = pedidoRepositoryPort.save(pedido);
        log.info("💾 Pedido salvo no banco - ID: {} Status: {}",
                pedidoSalvo.getId(), pedidoSalvo.getStatus());

        // 3. Processar pedido (validações básicas)
        Pedido pedidoProcessado = processarPedido(pedidoSalvo);

        log.info("✅ Pedido processado - ID: {} Status Final: {}",
                pedidoProcessado.getId(), pedidoProcessado.getStatus());

        return mapper.toResponse(pedidoProcessado);
    }

    @Override
    public PedidoResponseDTO consultarPedido(Long id) {
        log.info("🔍 Consultando pedido ID: {}", id);

        Pedido pedido = pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));

        log.info("✅ Pedido encontrado - ID: {} Status: {}", pedido.getId(), pedido.getStatus());
        return mapper.toResponse(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos() {
        log.info("📋 Listando todos os pedidos");

        List<Pedido> pedidos = pedidoRepositoryPort.findAll();
        log.info("✅ Encontrados {} pedidos", pedidos.size());

        return pedidos.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PedidoResponseDTO atualizarStatus(Long id, String status) {
        log.info("🔄 Atualizando status do pedido ID: {} para: {}", id, status);

        Pedido pedidoAtualizado = pedidoRepositoryPort.atualizarStatus(id, status);
        log.info("✅ Status atualizado - ID: {} Novo Status: {}", id, status);

        return mapper.toResponse(pedidoAtualizado);
    }

    /**
     * Processa o pedido com validações básicas
     * @param pedido Pedido a ser processado
     * @return Pedido com status atualizado
     */
    private Pedido processarPedido(Pedido pedido) {
        log.info("⚙️ Iniciando processamento do pedido ID: {}", pedido.getId());

        try {
            // 1. Validar itens do pedido
            if (!validarItensPedido(pedido)) {
                log.warn("❌ Itens inválidos para pedido ID: {}", pedido.getId());
                pedido.setStatus(StatusPedido.FECHADO_SEM_ESTOQUE);
                return pedidoRepositoryPort.save(pedido);
            }

            // 2. Calcular valor total do pedido
            BigDecimal valorTotal = calcularValorTotal(pedido);
            log.info("💰 Valor total do pedido ID: {} = R$ {}", pedido.getId(), valorTotal);

            // 3. Validar valor mínimo (exemplo de regra de negócio)
            if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("❌ Valor inválido para pedido ID: {} - Valor: R$ {}",
                        pedido.getId(), valorTotal);
                pedido.setStatus(StatusPedido.FECHADO_SEM_ESTOQUE);
                return pedidoRepositoryPort.save(pedido);
            }

            // 4. Marcar como processado com sucesso
            log.info("✅ Pedido aprovado e processado com sucesso - ID: {}", pedido.getId());
            pedido.setStatus(StatusPedido.FECHADO_COM_SUCESSO);

            return pedidoRepositoryPort.save(pedido);

        } catch (Exception e) {
            log.error("❌ Erro inesperado no processamento do pedido ID: {}", pedido.getId(), e);
            pedido.setStatus(StatusPedido.FECHADO_SEM_ESTOQUE);
            return pedidoRepositoryPort.save(pedido);
        }
    }

    /**
     * Valida se os itens do pedido são válidos
     * @param pedido Pedido a ser validado
     * @return true se os itens são válidos
     */
    private boolean validarItensPedido(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            log.warn("❌ Pedido sem itens - ID: {}", pedido.getId());
            return false;
        }

        log.info("📦 Validando {} itens do pedido ID: {}",
                pedido.getItens().size(), pedido.getId());

        for (ItemPedido item : pedido.getItens()) {
            if (item.getQuantidade() <= 0 || item.getProdutoId() == null) {
                log.warn("❌ Item inválido - Produto: {} Quantidade: {}",
                        item.getProdutoId(), item.getQuantidade());
                return false;
            }
        }

        log.info("✅ Itens validados com sucesso para pedido ID: {}", pedido.getId());
        return true;
    }

    /**
     * Calcula o valor total do pedido
     * @param pedido Pedido para calcular valor
     * @return Valor total do pedido
     */
    private BigDecimal calcularValorTotal(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(item -> {
                    // Converter Double para BigDecimal adequadamente
                    BigDecimal preco = item.getPrecoUnitario() != null ?
                            BigDecimal.valueOf(item.getPrecoUnitario()) : BigDecimal.valueOf(100.0);
                    return preco.multiply(BigDecimal.valueOf(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("💵 Cálculo valor total - Pedido ID: {} = R$ {}", pedido.getId(), total);
        return total;
    }
}