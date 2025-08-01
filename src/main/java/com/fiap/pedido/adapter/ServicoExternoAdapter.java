package com.fiap.pedido.adapter;

import com.fiap.pedido.dto.ClienteDTO;
import com.fiap.pedido.dto.ProdutoDTO;
import com.fiap.pedido.dto.StatusPagamentoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServicoExternoAdapter {

    public ClienteDTO consultarCliente(String clienteId) {
        log.info("Mock consultar cliente id: {}", clienteId);
        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(clienteId);
        cliente.setNome("Cliente Mock");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento("1990-01-01");
        cliente.setEndereco("Rua Mock, 123, 00000-000");
        return cliente;
    }

    public ProdutoDTO consultarProduto(String produtoId) {
        log.info("Mock consultar produto id: {}", produtoId);
        ProdutoDTO produto = new ProdutoDTO();
        produto.setId(produtoId);
        produto.setNome("Produto Mock");
        produto.setSku(produtoId);
        produto.setPreco(100.0);
        return produto;
    }

    public boolean reservarEstoque(String produtoId, int quantidade) {
        log.info("Mock reservar estoque produtoId: {}, quantidade: {}", produtoId, quantidade);
        return true;
    }

    public boolean estornarEstoque(String produtoId, int quantidade) {
        log.info("Mock estornar estoque produtoId: {}, quantidade: {}", produtoId, quantidade);
        return true;
    }

    public boolean baixarEstoque(String produtoId, int quantidade) {
        log.info("Mock baixar estoque produtoId: {}, quantidade: {}", produtoId, quantidade);
        return true;
    }

    public StatusPagamentoDTO processarPagamento(String numeroCartao, double valor) {
        log.info("Mock processar pagamento para cartão: {}, valor: {}", numeroCartao, valor);
        StatusPagamentoDTO dto = new StatusPagamentoDTO();
        dto.setPagamentoId("MOCK123");
        dto.setStatus("APROVADO");
        return dto;
    }

    public boolean estornarPagamento(String pagamentoId) {
        log.info("Mock estornar pagamento para pagamentoId: {}", pagamentoId);
        return true;
    }
}