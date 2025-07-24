package com.fiap.pedido.usecase.service;

import com.fiap.pedido.dto.StatusPagamentoDTO;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.request.ItemPedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.adapter.ServicoExternoAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessarPedidoUseCaseImpl implements ProcessarPedidoUseCase {

    private final PedidoUseCase pedidoUseCase;
    private final ServicoExternoAdapter servicoExternoAdapter;

    @Override
    public void processarPedido(PedidoRequestDTO pedidoRequestDTO) {
        PedidoResponseDTO pedidoResponse = null;
        try {
            // 1. Criar pedido com status ABERTO
            pedidoResponse = pedidoUseCase.criarPedido(pedidoRequestDTO);

            // 2. Reservar estoque para cada item
            boolean estoqueReservado = true;
            for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                boolean reservado = servicoExternoAdapter.reservarEstoque(item.getProdutoId(), item.getQuantidade());
                if (!reservado) {
                    estoqueReservado = false;
                    break;
                }
            }

            if (!estoqueReservado) {
                log.error("Estoque insuficiente para pedido: {}", pedidoRequestDTO);
                pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_ESTOQUE");
                return;
            }

            // 3. Calcular valor total do pedido
            double valorTotal = pedidoRequestDTO.getItens().stream()
                    .mapToDouble(item -> servicoExternoAdapter.consultarProduto(item.getProdutoId()).getPreco() * item.getQuantidade())
                    .sum();

            // 4. Processar pagamento
            StatusPagamentoDTO statusPagamento = servicoExternoAdapter.processarPagamento(
                    pedidoRequestDTO.getDadosPagamento().getNumeroCartao(), valorTotal);

            log.info("Status do pagamento recebido: {}", statusPagamento.getStatus());

            if (!"APROVADO".equalsIgnoreCase(statusPagamento.getStatus())) {
                log.error("Pagamento recusado para pedido: {}", pedidoRequestDTO);

                // Estornar estoque reservado
                for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                    boolean estornoOk = servicoExternoAdapter.estornarEstoque(item.getProdutoId(), item.getQuantidade());
                    log.info("Estorno de estoque para produto {}: {}", item.getProdutoId(), estornoOk ? "SUCESSO" : "FALHA");
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
            log.error("Erro ao processar pedido recebido: {}", pedidoRequestDTO, e);
            if (pedidoResponse != null) {
                pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "ERRO_PROCESSAMENTO");
            }
        }
    }
}