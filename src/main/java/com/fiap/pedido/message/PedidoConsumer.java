package com.fiap.pedido.message;

import com.fiap.pedido.adapter.ServicoExternoMockAdapter;
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
            if (pedidoRequestDTO.getDadosPagamento() == null) {
                log.error("Dados de pagamento estão nulos no pedido recebido: {}", pedidoRequestDTO);
                return;
            }

            var pedidoResponse = pedidoUseCase.criarPedido(pedidoRequestDTO);
            log.info("Pedido criado com sucesso: {}", pedidoResponse);

            ClienteDTO cliente = servicoExternoAdapter.consultarCliente(String.valueOf(pedidoRequestDTO.getClienteId()));
            log.info("Cliente consultado: {}", cliente);

            double valorTotal = 0.0;
            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                ProdutoDTO produto = servicoExternoAdapter.consultarProduto(item.getProdutoId());
                log.info("Produto consultado: {}", produto);

                boolean estoqueReservado = servicoExternoAdapter.reservarEstoque(item.getProdutoId(), item.getQuantidade());
                if (!estoqueReservado) {
                    log.error("Estoque insuficiente para produto: {}", item.getProdutoId());
                    pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                    return;
                }
                valorTotal += produto.getPreco() * item.getQuantidade();
            }

            StatusPagamentoDTO statusPagamento = servicoExternoAdapter.processarPagamento(
                    pedidoRequestDTO.getDadosPagamento().getNumeroCartao(), valorTotal);

            log.info("Status do pagamento recebido: {}", statusPagamento.getStatus());

            if (!"APROVADO".equalsIgnoreCase(statusPagamento.getStatus())) {
                log.error("Pagamento recusado para pedido: {}", pedidoRequestDTO);

                for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                    boolean estornoOk = servicoExternoAdapter.estornarEstoque(item.getProdutoId(), item.getQuantidade());
                    log.info("Estorno de estoque para produto {}: {}", item.getProdutoId(), estornoOk ? "SUCESSO" : "FALHA");
                }

                PedidoResponseDTO pedidoAtualizado = pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_CREDITO");
                log.info("Status do pedido atualizado para: {}", pedidoAtualizado.getStatus());

                return;
            }

            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                boolean estoqueBaixado = servicoExternoAdapter.baixarEstoque(item.getProdutoId(), item.getQuantidade());
                if (!estoqueBaixado) {
                    log.error("Falha ao baixar estoque para produto: {}", item.getProdutoId());
                    servicoExternoAdapter.estornarPagamento(statusPagamento.getPagamentoId());
                    for (ItemPedidoRequestDTO i : pedidoRequestDTO.getItens()) {
                        servicoExternoAdapter.estornarEstoque(i.getProdutoId(), i.getQuantidade());
                    }
                    pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                    return;
                }
            }

            pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_COM_SUCESSO");
            log.info("Pedido processado com sucesso para pedido: {}", pedidoRequestDTO);

        } catch (Exception e) {
            log.error("Erro ao processar pedido recebido do Kafka: {}", pedidoRequestDTO, e);
        }
    }
}