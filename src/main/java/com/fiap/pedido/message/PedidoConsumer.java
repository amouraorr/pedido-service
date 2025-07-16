package com.fiap.pedido.message;

import com.fiap.pedido.adapter.ServicoExternoMockAdapter;
import com.fiap.pedido.adapter.ServicoExternoMockAdapter.ClienteDTO;
import com.fiap.pedido.adapter.ServicoExternoMockAdapter.ProdutoDTO;
import com.fiap.pedido.adapter.ServicoExternoMockAdapter.StatusPagamentoDTO;
import com.fiap.pedido.dto.request.ItemPedidoRequestDTO;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.usecase.service.PedidoUseCaseImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
        log.info("Recebido pedido do Kafka: {}", pedidoRequestDTO);
        try {
            // 1. Criar pedido no banco com status ABERTO e data atual
            var pedidoResponse = pedidoUseCase.criarPedido(pedidoRequestDTO);
            log.info("Pedido criado com sucesso: {}", pedidoResponse);

            // 2. Consultar cliente
            ClienteDTO cliente = servicoExternoAdapter.consultarCliente(pedidoRequestDTO.getClienteId());
            log.info("Cliente consultado: {}", cliente);

            // 3. Para cada item, consultar produto e reservar/baixar estoque
            double valorTotal = 0.0;
            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                ProdutoDTO produto = servicoExternoAdapter.consultarProduto(item.getProdutoId());
                log.info("Produto consultado: {}", produto);

                boolean estoqueReservado = servicoExternoAdapter.reservarEstoque(item.getProdutoId(), item.getQuantidade());
                if (!estoqueReservado) {
                    log.error("Estoque insuficiente para produto: {}", item.getProdutoId());
                    // Atualizar status do pedido para FECHADO_SEM_ESTOQUE
                    pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                    return;
                }
                valorTotal += produto.getPreco() * item.getQuantidade();
            }

            // 4. Processar pagamento
            StatusPagamentoDTO statusPagamento = servicoExternoAdapter.processarPagamento(
                    pedidoRequestDTO.getDadosPagamento().getNumeroCartao(), valorTotal);
            if (!"APROVADO".equalsIgnoreCase(statusPagamento.getStatus())) {
                log.error("Pagamento não aprovado para pedido: {}", pedidoRequestDTO);
                // Estornar estoque reservado
                for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                    servicoExternoAdapter.estornarEstoque(item.getProdutoId(), item.getQuantidade());
                }
                // Atualizar status do pedido para FECHADO_SEM_CREDITO
                pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_CREDITO");
                return;
            }

            // 5. Baixar estoque efetivamente após pagamento aprovado
            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                boolean estoqueBaixado = servicoExternoAdapter.baixarEstoque(item.getProdutoId(), item.getQuantidade());
                if (!estoqueBaixado) {
                    log.error("Falha ao baixar estoque para produto: {}", item.getProdutoId());
                    // Estornar pagamento
                    servicoExternoAdapter.estornarPagamento(statusPagamento.getPagamentoId());
                    // Estornar estoque reservado
                    for (ItemPedidoRequestDTO i : pedidoRequestDTO.getItens()) {
                        servicoExternoAdapter.estornarEstoque(i.getProdutoId(), i.getQuantidade());
                    }
                    // Atualizar status do pedido para FECHADO_SEM_ESTOQUE
                    pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                    return;
                }
            }

            // 6. Atualizar status do pedido para FECHADO_COM_SUCESSO
            pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_COM_SUCESSO");
            log.info("Pedido processado com sucesso para pedido: {}", pedidoRequestDTO);

        } catch (Exception e) {
            log.error("Erro ao processar pedido recebido do Kafka: {}", pedidoRequestDTO, e);
            // TODO: lógica de retry ou envio para dead letter queue
        }
    }
}