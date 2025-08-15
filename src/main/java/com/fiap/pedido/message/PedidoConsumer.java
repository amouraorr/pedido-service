package com.fiap.pedido.message;

import com.fiap.pedido.adapter.ServicoExternoMockAdapter;
// CORREÇÃO: Removidas importações incorretas de classes internas do adapter
import com.fiap.pedido.dto.ClienteDTO;
import com.fiap.pedido.dto.ProdutoDTO;
import com.fiap.pedido.dto.StatusPagamentoDTO;
import com.fiap.pedido.dto.request.ItemPedidoRequestDTO;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.usecase.service.PedidoUseCaseImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka para processar pedidos recebidos do tópico 'novo-pedido'.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoConsumer {

    private final PedidoUseCaseImpl pedidoUseCase;
    private final ServicoExternoMockAdapter servicoExternoAdapter;

    @KafkaListener(topics = "novo-pedido", groupId = "pedido-service", containerFactory = "pedidoKafkaListenerContainerFactory")
    public void consumirPedido(PedidoRequestDTO pedidoRequestDTO) {
        log.info("=== INICIANDO PROCESSAMENTO DO PEDIDO KAFKA: {} ===", pedidoRequestDTO);

        try {

            if (pedidoRequestDTO.getDadosPagamento() == null ||
                    pedidoRequestDTO.getDadosPagamento().getNumeroCartao() == null) {
                log.error("❌ ERRO: Dados de pagamento inválidos no pedido: {}", pedidoRequestDTO);
                return;
            }

            log.info("📝 ETAPA 1: Criando pedido no banco com status ABERTO");
            var pedidoResponse = pedidoUseCase.criarPedido(pedidoRequestDTO);
            log.info("✅ Pedido criado com ID: {} e status: {}", pedidoResponse.getId(), pedidoResponse.getStatus());

            log.info("📝 ETAPA 2: Consultando cliente ID: {}", pedidoRequestDTO.getClienteId());
            ClienteDTO cliente = servicoExternoAdapter.consultarCliente(String.valueOf(pedidoRequestDTO.getClienteId()));
            log.info("✅ Cliente consultado: {}", cliente.getNome());

            log.info("📝 ETAPA 3: Verificando e reservando estoque");
            double valorTotal = 0.0;
            boolean todoEstoqueReservado = true;

            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                log.info("🔍 Consultando produto: {}", item.getProdutoId());
                ProdutoDTO produto = servicoExternoAdapter.consultarProduto(item.getProdutoId());
                log.info("📦 Produto encontrado: {} - Preço: {}", produto.getNome(), produto.getPreco());

                log.info("📋 Reservando estoque - Produto: {} Quantidade: {}", item.getProdutoId(), item.getQuantidade());
                boolean estoqueReservado = servicoExternoAdapter.reservarEstoque(item.getProdutoId(), item.getQuantidade());

                if (!estoqueReservado) {
                    log.error("❌ ESTOQUE INSUFICIENTE para produto: {}", item.getProdutoId());
                    todoEstoqueReservado = false;
                    break;
                }

                valorTotal += produto.getPreco() * item.getQuantidade();
                log.info("💰 Valor acumulado: {}", valorTotal);
            }

            if (!todoEstoqueReservado) {
                log.info("📝 ATUALIZANDO STATUS PARA: FECHADO_SEM_ESTOQUE");
                PedidoResponseDTO pedidoAtualizado = pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                log.info("✅ Status atualizado para: {}", pedidoAtualizado.getStatus());
                return;
            }

            log.info("📝 ETAPA 4: Processando pagamento - Cartão: {} Valor: {}",
                    pedidoRequestDTO.getDadosPagamento().getNumeroCartao(), valorTotal);

            StatusPagamentoDTO statusPagamento = servicoExternoAdapter.processarPagamento(
                    pedidoRequestDTO.getDadosPagamento().getNumeroCartao(), valorTotal);

            log.info("💳 Status do pagamento recebido: {}", statusPagamento.getStatus());

            if (!"APROVADO".equalsIgnoreCase(statusPagamento.getStatus())) {
                log.error("❌ PAGAMENTO RECUSADO para pedido ID: {}", pedidoResponse.getId());

                log.info("🔄 Iniciando rollback do estoque");
                for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                    boolean estornoOk = servicoExternoAdapter.estornarEstoque(item.getProdutoId(), item.getQuantidade());
                    log.info("🔄 Estorno estoque produto {}: {}", item.getProdutoId(), estornoOk ? "✅ SUCESSO" : "❌ FALHA");
                }

                log.info("📝 ATUALIZANDO STATUS PARA: FECHADO_SEM_CREDITO");
                PedidoResponseDTO pedidoAtualizado = pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_CREDITO");
                log.info("✅ Status atualizado para: {}", pedidoAtualizado.getStatus());
                return;
            }

            log.info("📝 ETAPA 5: Efetuando baixa definitiva no estoque");
            boolean todoBaixado = true;
            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                log.info("📦 Baixando estoque produto: {} quantidade: {}", item.getProdutoId(), item.getQuantidade());
                boolean estoqueBaixado = servicoExternoAdapter.baixarEstoque(item.getProdutoId(), item.getQuantidade());

                if (!estoqueBaixado) {
                    log.error("❌ FALHA ao baixar estoque para produto: {}", item.getProdutoId());
                    todoBaixado = false;
                    break;
                }
                log.info("✅ Estoque baixado com sucesso para produto: {}", item.getProdutoId());
            }

            if (!todoBaixado) {
                log.error("❌ Falha na baixa de estoque - Iniciando rollback completo");

                log.info("🔄 Estornando pagamento: {}", statusPagamento.getPagamentoId());
                servicoExternoAdapter.estornarPagamento(statusPagamento.getPagamentoId());

                for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                    servicoExternoAdapter.estornarEstoque(item.getProdutoId(), item.getQuantidade());
                }

                log.info("📝 ATUALIZANDO STATUS PARA: FECHADO_SEM_ESTOQUE");
                pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                return;
            }

            log.info("📝 ETAPA 6: FINALIZANDO PEDIDO COM SUCESSO");
            PedidoResponseDTO pedidoFinalizado = pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_COM_SUCESSO");
            log.info("🎉 === PEDIDO PROCESSADO COM SUCESSO === ID: {} Status Final: {}",
                    pedidoFinalizado.getId(), pedidoFinalizado.getStatus());

        } catch (Exception e) {
            log.error("💥 ERRO CRÍTICO ao processar pedido: {}", pedidoRequestDTO, e);
            log.error("Stack trace completo:", e);
        }
    }
}