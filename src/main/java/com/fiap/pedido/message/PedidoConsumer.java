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
            // Verificação para evitar NullPointerException ao acessar dadosPagamento
            if (pedidoRequestDTO.getDadosPagamento() == null) {
                log.error("Dados de pagamento estão nulos no pedido recebido: {}", pedidoRequestDTO);
                // Opcional: atualizar status do pedido para indicar erro ou rejeição
                return; // interrompe o processamento deste pedido
            }

            // 1. Criar pedido no banco com status ABERTO e data atual
            var pedidoResponse = pedidoUseCase.criarPedido(pedidoRequestDTO);
            log.info("Pedido criado com sucesso: {}", pedidoResponse);

            // 2. Consultar cliente (agora usando String clienteId)
            ClienteDTO cliente = servicoExternoAdapter.consultarCliente(String.valueOf(pedidoRequestDTO.getClienteId()));
            log.info("Cliente consultado: {}", cliente);

            // 3. Para cada item, consultar produto e reservar estoque
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

            log.info("Status do pagamento recebido: {}", statusPagamento.getStatus());

            if (!"APROVADO".equalsIgnoreCase(statusPagamento.getStatus())) {
                log.error("Pagamento recusado para pedido: {}", pedidoRequestDTO);

                // Estornar estoque reservado
                for (ItemPedidoRequestDTO item : pedidoRequestDTO.getItens()) {
                    boolean estornoOk = servicoExternoAdapter.estornarEstoque(item.getProdutoId(), item.getQuantidade());
                    log.info("Estorno de estoque para produto {}: {}", item.getProdutoId(), estornoOk ? "SUCESSO" : "FALHA");
                }

                // Atualizar status do pedido para FECHADO_SEM_CREDITO
                PedidoResponseDTO pedidoAtualizado = pedidoUseCase.atualizarStatus(pedidoResponse.getId(), "FECHADO_SEM_CREDITO");
                log.info("Status do pedido atualizado para: {}", pedidoAtualizado.getStatus());

                return; // Interrompe o processamento para evitar sobrescrita do status
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
        }
    }
}