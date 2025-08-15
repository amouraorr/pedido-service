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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoUseCaseImpl implements PedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final PedidoMapper pedidoMapper;

    @Override
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO) {
        try {
            log.info("🆕 Iniciando criação de pedido para cliente: {}", pedidoRequestDTO.getClienteId());

            // 1. Mapear DTO para domínio
            Pedido pedido = pedidoMapper.toDomain(pedidoRequestDTO);
            pedido.setStatus(StatusPedido.ABERTO);
            pedido.setDataCriacao(LocalDateTime.now());

            log.info("📝 Dados do pedido mapeados - Status: {} Data: {}",
                    pedido.getStatus(), pedido.getDataCriacao());

            // 2. Salvar pedido inicial no banco
            Pedido pedidoSalvo = pedidoRepositoryPort.save(pedido);
            log.info("💾 Pedido salvo no banco - ID: {} Status: {}",
                    pedidoSalvo.getId(), pedidoSalvo.getStatus());

            // 3. Processar pedido (verificar estoque e crédito)
            Pedido pedidoProcessado = processarPedido(pedidoSalvo);

            // 4. Salvar pedido processado
            Pedido pedidoFinal = pedidoRepositoryPort.save(pedidoProcessado);
            log.info("✅ Pedido processado - ID: {} Status Final: {}",
                    pedidoFinal.getId(), pedidoFinal.getStatus());

            return pedidoMapper.toResponse(pedidoFinal);

        } catch (Exception e) {
            log.error("❌ Erro ao criar pedido: {}", pedidoRequestDTO, e);
            throw new RuntimeException("Erro ao processar pedido", e);
        }
    }

    private Pedido processarPedido(Pedido pedido) {
        log.info("⚙️ Iniciando processamento do pedido ID: {}", pedido.getId());

        try {
            // 1. Verificar estoque
            if (!verificarEstoque(pedido)) {
                pedido.setStatus(StatusPedido.FECHADO_SEM_ESTOQUE);
                log.warn("❌ Pedido rejeitado por falta de estoque - ID: {}", pedido.getId());
                return pedido;
            }

            // 2. Se passou na verificação de estoque, validar itens
            log.info("📦 Validando {} itens do pedido ID: {}", pedido.getItens().size(), pedido.getId());
            validarItens(pedido.getItens());
            log.info("✅ Itens validados com sucesso para pedido ID: {}", pedido.getId());

            // 3. Calcular valor total
            BigDecimal valorTotal = calcularValorTotal(pedido);
            log.info("💰 Valor total do pedido ID: {} = R$ {}", pedido.getId(), valorTotal);

            // 4. Aprovar pedido
            pedido.setStatus(StatusPedido.FECHADO_COM_SUCESSO);
            log.info("✅ Pedido aprovado e processado com sucesso - ID: {}", pedido.getId());

            return pedido;

        } catch (Exception e) {
            log.error("❌ Erro durante processamento do pedido ID: {}", pedido.getId(), e);
            pedido.setStatus(StatusPedido.FECHADO_SEM_ESTOQUE);
            return pedido;
        }
    }

    /**
     * Simula verificação de estoque
     * Regras de simulação:
     * - SKU001: Estoque limitado a 3 unidades
     * - SKU002: Estoque limitado a 2 unidades
     * - SKU003: Sem estoque (sempre falha)
     * - Outros: Estoque OK
     */
    private boolean verificarEstoque(Pedido pedido) {
        log.info("📦 Verificando estoque para {} itens do pedido ID: {}",
                pedido.getItens().size(), pedido.getId());

        for (ItemPedido item : pedido.getItens()) {
            String produtoId = item.getProdutoId();
            int quantidade = item.getQuantidade();

            log.debug("🔍 Verificando produto: {} - Quantidade solicitada: {}", produtoId, quantidade);

            // Simular regras de estoque
            boolean temEstoque = switch (produtoId) {
                case "SKU001" -> {
                    boolean disponivel = quantidade <= 3;
                    log.info("📦 SKU001 - Solicitado: {} | Disponível: 3 | Status: {}",
                            quantidade, disponivel ? "✅ OK" : "❌ SEM ESTOQUE");
                    yield disponivel;
                }
                case "SKU002" -> {
                    boolean disponivel = quantidade <= 2;
                    log.info("📦 SKU002 - Solicitado: {} | Disponível: 2 | Status: {}",
                            quantidade, disponivel ? "✅ OK" : "❌ SEM ESTOQUE");
                    yield disponivel;
                }
                case "SKU003" -> {
                    log.info("📦 SKU003 - ❌ PRODUTO SEM ESTOQUE");
                    yield false;
                }
                default -> {
                    log.info("📦 {} - ✅ ESTOQUE OK (produto genérico)", produtoId);
                    yield true;
                }
            };

            if (!temEstoque) {
                log.warn("❌ Estoque insuficiente para produto: {} (solicitado: {})", produtoId, quantidade);
                return false;
            }
        }

        log.info("✅ Estoque verificado com sucesso para todos os itens do pedido ID: {}", pedido.getId());
        return true;
    }

    private void validarItens(List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter pelo menos um item");
        }

        for (ItemPedido item : itens) {
            if (item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero");
            }
            if (item.getProdutoId() == null || item.getProdutoId().trim().isEmpty()) {
                throw new IllegalArgumentException("Produto ID é obrigatório");
            }
        }
    }

    /**
     * Calcula o valor total do pedido
     * @param pedido Pedido para calcular valor
     * @return Valor total
     */
    private BigDecimal calcularValorTotal(Pedido pedido) {
        log.debug("💵 Calculando valor total do pedido ID: {}", pedido.getId());

        BigDecimal valorTotal = pedido.getItens().stream()
                .map(item -> {
                    // Usar preço do item se disponível, senão usar preço padrão
                    BigDecimal preco = item.getPrecoUnitario() != null ?
                            BigDecimal.valueOf(item.getPrecoUnitario()) :
                            BigDecimal.valueOf(100.0);

                    BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(item.getQuantidade()));
                    log.debug("💵 Item {} - Preço: R$ {} x Qtd: {} = R$ {}",
                            item.getProdutoId(), preco, item.getQuantidade(), subtotal);

                    return subtotal;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("💵 Cálculo valor total - Pedido ID: {} = R$ {}", pedido.getId(), valorTotal);
        return valorTotal;
    }

    @Override
    public PedidoResponseDTO consultarPedido(Long id) {
        log.info("🔍 Buscando pedido por ID: {}", id);

        Optional<Pedido> pedidoOpt = pedidoRepositoryPort.findById(id);
        if (pedidoOpt.isEmpty()) {
            log.warn("❌ Pedido não encontrado com ID: {}", id);
            throw new RuntimeException("Pedido não encontrado com ID: " + id);
        }

        Pedido pedido = pedidoOpt.get();
        log.info("✅ Pedido encontrado - ID: {} Status: {}", pedido.getId(), pedido.getStatus());
        return pedidoMapper.toResponse(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos() {
        log.info("📋 Listando todos os pedidos");
        List<Pedido> pedidos = pedidoRepositoryPort.findAll();
        log.info("📋 Total de {} pedidos encontrados", pedidos.size());
        return pedidos.stream()
                .map(pedidoMapper::toResponse)
                .toList();
    }

    @Override
    public PedidoResponseDTO atualizarStatus(Long pedidoId, String novoStatus) {
        log.info("🔄 Atualizando status do pedido ID: {} para: {}", pedidoId, novoStatus);

        try {
            // Atualizar status usando o método do repositório
            Pedido pedidoAtualizado = pedidoRepositoryPort.atualizarStatus(pedidoId, novoStatus);
            log.info("✅ Status atualizado com sucesso - Pedido ID: {} Status: {}",
                    pedidoAtualizado.getId(), pedidoAtualizado.getStatus());

            return pedidoMapper.toResponse(pedidoAtualizado);
        } catch (Exception e) {
            log.error("❌ Erro ao atualizar status do pedido ID: {}", pedidoId, e);
            throw new RuntimeException("Erro ao atualizar status do pedido", e);
        }
    }
}