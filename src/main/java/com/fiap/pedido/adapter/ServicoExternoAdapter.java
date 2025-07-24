package com.fiap.pedido.adapter;

import com.fiap.pedido.dto.StatusPagamentoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServicoExternoAdapter {

    public ServicoExternoMockAdapter.ClienteDTO consultarCliente(String clienteId) {
        log.info("Mock consultar cliente id: {}", clienteId);
        // Mock returning a client DTO
        ServicoExternoMockAdapter.ClienteDTO cliente = new ServicoExternoMockAdapter.ClienteDTO();
        cliente.setId(String.valueOf(Long.valueOf(clienteId)));
        cliente.setNome("Cliente Mock");
        return cliente;
    }

    public ServicoExternoMockAdapter.ProdutoDTO consultarProduto(String produtoId) {
        log.info("Mock consultar produto id: {}", produtoId);
        ServicoExternoMockAdapter.ProdutoDTO produto = new ServicoExternoMockAdapter.ProdutoDTO();
        produto.setId(String.valueOf(Long.valueOf(produtoId)));
        produto.setNome("Produto Mock");
        produto.setPreco(100.0);
        return produto;
    }

    public boolean reservarEstoque(String produtoId, int quantidade) {
        log.info("Mock reservar estoque produtoId: {}, quantidade: {}", produtoId, quantidade);
        return true; // Always succeed in mock
    }

    public boolean estornarEstoque(String produtoId, int quantidade) {
        log.info("Mock estornar estoque produtoId: {}, quantidade: {}", produtoId, quantidade);
        return true;
    }

    public boolean baixarEstoque(String produtoId, int quantidade) {
        log.info("Mock baixar estoque produtoId: {}, quantidade: {}", produtoId, quantidade);
        return true;
    }

    // ADICIONE ESTE MÉTODO:
    public StatusPagamentoDTO processarPagamento(String numeroCartao, double valor) {
        log.info("Mock processar pagamento para cartão: {}, valor: {}", numeroCartao, valor);
        StatusPagamentoDTO dto = new StatusPagamentoDTO();
        dto.setPagamentoId("MOCK123");
        dto.setStatus("APROVADO"); // ou "RECUSADO" para simular falha
        return dto;
    }

    // Opcional: mock para estornar pagamento
    public boolean estornarPagamento(String pagamentoId) {
        log.info("Mock estornar pagamento para pagamentoId: {}", pagamentoId);
        return true;
    }
}
