package com.fiap.pedido.adapter;

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
}