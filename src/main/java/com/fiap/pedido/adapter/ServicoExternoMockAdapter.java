package com.fiap.pedido.adapter;

import com.fiap.pedido.dto.ClienteDTO;
import com.fiap.pedido.dto.ProdutoDTO;
import com.fiap.pedido.dto.StatusPagamentoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ServicoExternoMockAdapter {

    private final Map<String, Integer> estoqueDisponivel = new HashMap<>();

    public ServicoExternoMockAdapter() {
        estoqueDisponivel.put("SKU001", 2);
        estoqueDisponivel.put("SKU002", 2);
        estoqueDisponivel.put("SKU003", 2);
    }

    public ClienteDTO consultarCliente(String clienteId) {
        log.info("Mock: consultando cliente com id {}", clienteId);
        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(clienteId);
        cliente.setNome("Cliente Mock");
        cliente.setCpf("123.456.789-00");
        cliente.setDataNascimento("1990-01-01");
        cliente.setEndereco("Rua Mock, 123, 00000-000");
        return cliente;
    }

    public ProdutoDTO consultarProduto(String produtoId) {
        log.info("Mock: consultando produto com id {}", produtoId);
        ProdutoDTO produto = new ProdutoDTO();
        produto.setId(produtoId);
        produto.setNome("Produto Mock");
        produto.setSku(produtoId);
        produto.setPreco(100.0);
        return produto;
    }

    public boolean reservarEstoque(String produtoId, Integer quantidade) {
        log.info("Mock: reservando estoque para produto {} quantidade {}", produtoId, quantidade);
        Integer disponivel = estoqueDisponivel.getOrDefault(produtoId, 0);
        if (quantidade <= disponivel) {
            log.info("Estoque reservado para produto {} quantidade {}", produtoId, quantidade);
            return true;
        } else {
            log.warn("Falha ao reservar estoque para produto {}. Quantidade solicitada: {}, disponível: {}", produtoId, quantidade, disponivel);
            return false;
        }
    }

    public boolean baixarEstoque(String produtoId, Integer quantidade) {
        log.info("Mock: baixando estoque para produto {} quantidade {}", produtoId, quantidade);
        Integer disponivel = estoqueDisponivel.getOrDefault(produtoId, 0);
        if (quantidade <= disponivel) {
            estoqueDisponivel.put(produtoId, disponivel - quantidade);
            log.info("Estoque baixado para produto {}. Quantidade: {}. Estoque restante: {}", produtoId, quantidade, estoqueDisponivel.get(produtoId));
            return true;
        } else {
            log.warn("Falha ao baixar estoque para produto {}. Quantidade solicitada: {}, disponível: {}", produtoId, quantidade, disponivel);
            return false;
        }
    }

    public boolean estornarEstoque(String produtoId, Integer quantidade) {
        log.info("Mock: estornando estoque para produto {} quantidade {}", produtoId, quantidade);
        Integer disponivel = estoqueDisponivel.getOrDefault(produtoId, 0);
        estoqueDisponivel.put(produtoId, disponivel + quantidade);
        log.info("Estoque estornado para produto {}. Quantidade: {}. Estoque atualizado: {}", produtoId, quantidade, estoqueDisponivel.get(produtoId));
        return true;
    }

    public StatusPagamentoDTO processarPagamento(String numeroCartao, Double valorTotal) {
        log.info("Mock: processando pagamento com cartão {} e valor {}", numeroCartao, valorTotal);
        StatusPagamentoDTO status = new StatusPagamentoDTO();
        status.setPagamentoId(UUID.randomUUID().toString());

        if ("0000000000000000".equals(numeroCartao)) {
            status.setStatus("RECUSADO");
        } else if (valorTotal <= 1000) {
            status.setStatus("APROVADO");
        } else {
            status.setStatus("RECUSADO");
        }
        return status;
    }

    public void estornarPagamento(String pagamentoId) {
        log.info("Mock: estornando pagamento com id {}", pagamentoId);
    }
}