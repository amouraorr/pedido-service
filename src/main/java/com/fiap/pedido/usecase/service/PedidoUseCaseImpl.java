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
    public PedidoResponseDTO criarPedido(PedidoRequestDTO request) {
        log.info("🆕 Iniciando criação de pedido para cliente: {}", request.getClienteId());

        Pedido pedido = mapper.toDomain(request);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setDataCriacao(LocalDateTime.now());

        log.info("📝 Dados do pedido mapeados - Status: {} Data: {}",
                pedido.getStatus(), pedido.getDataCriacao());

        Pedido pedidoSalvo = pedidoRepositoryPort.save(pedido);
        log.info("💾 Pedido salvo no banco - ID: {} Status: {}",
                pedidoSalvo.getId(), pedidoSalvo.getStatus());

        Pedido pedidoProcessado = processarPedido(pedidoSalvo);

        PedidoResponseDTO response = mapper.toResponse(pedidoProcessado);
        log.info("✅ Pedido processado - ID: {} Status Final: {}",
                pedidoProcessado.getId(), pedidoProcessado.getStatus());

        return response;
    }

    @Override
    public PedidoResponseDTO consultarPedido(Long id) {
        log.info("🔍 Consultando pedido por ID: {}", id);
        Pedido pedido = pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Pedido não encontrado com ID: {}", id);
                    return new RuntimeException("Pedido não encontrado");
                });

        log.info("✅ Pedido encontrado - ID: {} Status: {}", pedido.getId(), pedido.getStatus());
        return mapper.toResponse(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos() {
        log.info("📋 Listando todos os pedidos");
        List<Pedido> pedidos = pedidoRepositoryPort.findAll();
        log.info("📊 Encontrados {} pedidos", pedidos.size());
        return mapper.toResponseList(pedidos);
    }

    @Override
    public PedidoResponseDTO atualizarStatus(Long id, String status) {
        log.info("🔄 Atualizando status do pedido ID: {} para: {}", id, status);

        Pedido pedido = pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Pedido não encontrado para atualização - ID: {}", id);
                    return new RuntimeException("Pedido não encontrado");
                });

        StatusPedido novoStatus = StatusPedido.valueOf(status.toUpperCase());
        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepositoryPort.save(pedido);

        log.info("✅ Status atualizado com sucesso - ID: {} Novo Status: {}", id, novoStatus);
        return mapper.toResponse(pedidoAtualizado);
    }

    /**
     * Processa o pedido aplicando as regras de negócio
     */
    private Pedido processarPedido(Pedido pedido) {
        log.info("⚙️ Iniciando processamento do pedido ID: {}", pedido.getId());

        // 1. Verificar estoque
        if (!verificarEstoqueDisponivel(pedido)) {
            log.warn("❌ Pedido rejeitado por falta de estoque - ID: {}", pedido.getId());
            pedido.setStatus(StatusPedido.FECHADO_SEM_ESTOQUE);
            return pedidoRepositoryPort.save(pedido);
        }

        // 2. Verificar crédito do cliente
        if (!verificarCreditoCliente(pedido)) {
            log.warn("❌ Pedido rejeitado por crédito insuficiente - ID: {}", pedido.getId());
            pedido.setStatus(StatusPedido.FECHADO_SEM_CREDITO);
            return pedidoRepositoryPort.save(pedido);
        }

        // 3. Pedido aprovado
        log.info("✅ Pedido aprovado e processado com sucesso - ID: {}", pedido.getId());
        pedido.setStatus(StatusPedido.FECHADO_COM_SUCESSO);

        return pedidoRepositoryPort.save(pedido);
    }

    /**
     * Simula verificação de estoque dos produtos
     */
    private boolean verificarEstoqueDisponivel(Pedido pedido) {
        log.info("📦 Verificando estoque para {} itens do pedido ID: {}",
                pedido.getItens().size(), pedido.getId());

        for (ItemPedido item : pedido.getItens()) {
            log.debug("🔍 Verificando produto: {} - Quantidade solicitada: {}",
                    item.getProdutoId(), item.getQuantidade());

            int estoqueDisponivel = calcularEstoqueSimulado(item.getProdutoId());
            boolean temEstoque = estoqueDisponivel >= item.getQuantidade();

            log.info("📦 {} - Solicitado: {} | Disponível: {} | Status: {}",
                    item.getProdutoId(),
                    item.getQuantidade(),
                    estoqueDisponivel,
                    temEstoque ? "✅ OK" : "❌ SEM ESTOQUE");

            if (!temEstoque) {
                log.warn("❌ Estoque insuficiente para produto: {} (solicitado: {})",
                        item.getProdutoId(), item.getQuantidade());
                return false;
            }
        }

        log.info("✅ Estoque validado com sucesso para pedido ID: {}", pedido.getId());
        return true;
    }

    /**
     * Simula verificação de crédito do cliente
     */
    private boolean verificarCreditoCliente(Pedido pedido) {
        log.info("💳 Verificando crédito para cliente ID: {} do pedido ID: {}",
                pedido.getClienteId(), pedido.getId());

        BigDecimal valorTotal = calcularValorTotal(pedido);
        log.info("💰 Valor total do pedido ID: {} = R$ {}", pedido.getId(), valorTotal);

        BigDecimal limiteCreditoDisponivel = calcularLimiteCreditoSimulado(pedido.getClienteId());
        log.info("💳 Limite de crédito disponível para cliente {}: R$ {}",
                pedido.getClienteId(), limiteCreditoDisponivel);

        boolean creditoSuficiente = limiteCreditoDisponivel.compareTo(valorTotal) >= 0;

        log.info("💳 Cliente {} - Valor: R$ {} | Limite: R$ {} | Status: {}",
                pedido.getClienteId(),
                valorTotal,
                limiteCreditoDisponivel,
                creditoSuficiente ? "✅ CRÉDITO OK" : "❌ CRÉDITO INSUFICIENTE");

        if (!creditoSuficiente) {
            log.warn("❌ Crédito insuficiente para cliente: {} (necessário: R$ {}, disponível: R$ {})",
                    pedido.getClienteId(), valorTotal, limiteCreditoDisponivel);
            return false;
        }

        log.info("✅ Crédito validado com sucesso para pedido ID: {}", pedido.getId());
        return true;
    }

    /**
     * Simula o cálculo de estoque disponível baseado no produto ID
     */
    private int calcularEstoqueSimulado(String produtoId) {

        return switch (produtoId.toUpperCase()) {
            case "SKU001" -> 3;
            case "SKU002" -> 15;
            case "SKU003" -> 8;
            case "SKU004" -> 20;
            case "SKU005" -> 1;
            default -> 10;
        };
    }

    /**
     * Simula o cálculo de limite de crédito baseado no cliente ID
     */
    private BigDecimal calcularLimiteCreditoSimulado(Long clienteId) {

        return switch (clienteId.intValue()) {
            case 1 -> new BigDecimal("500.00");
            case 2 -> new BigDecimal("1000.00");
            case 3 -> new BigDecimal("100.00");
            case 4 -> new BigDecimal("50.00");
            default -> new BigDecimal("300.00");
        };
    }

    /**
     * Calcula o valor total do pedido
     */
    private BigDecimal calcularValorTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .map(item -> {

                    BigDecimal preco = item.getPrecoUnitario() != null
                            ? BigDecimal.valueOf(item.getPrecoUnitario())
                            : BigDecimal.valueOf(100.0);

                    return preco.multiply(BigDecimal.valueOf(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}