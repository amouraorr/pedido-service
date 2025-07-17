package com.fiap.pedido.adapter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock adapter para simular chamadas REST a microsserviços externos.
 */
@Slf4j
@Component
public class ServicoExternoMockAdapter {

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
        produto.setSku("SKU-MOCK-" + produtoId);
        produto.setPreco(100.0);
        return produto;
    }

    public boolean reservarEstoque(String produtoId, Integer quantidade) {
        log.info("Mock: reservando estoque para produto {} quantidade {}", produtoId, quantidade);
        // Simula sucesso na reserva de estoque
        return true;
    }

    public boolean baixarEstoque(String produtoId, Integer quantidade) {
        log.info("Mock: baixando estoque para produto {} quantidade {}", produtoId, quantidade);
        // Simula sucesso na baixa de estoque
        return true;
    }

    public void estornarEstoque(String produtoId, Integer quantidade) {
        log.info("Mock: estornando estoque para produto {} quantidade {}", produtoId, quantidade);
        // Simula estorno de estoque
    }

    public StatusPagamentoDTO processarPagamento(String numeroCartao, Double valorTotal) {
        log.info("Mock: processando pagamento com cartão {} e valor {}", numeroCartao, valorTotal);
        StatusPagamentoDTO status = new StatusPagamentoDTO();
        status.setPagamentoId(UUID.randomUUID());
        // Simula aprovação se valor <= 1000, recusado caso contrário (exemplo)
        if (valorTotal <= 1000) {
            status.setStatus("APROVADO");
        } else {
            status.setStatus("RECUSADO");
        }
        return status;
    }

    public void estornarPagamento(UUID pagamentoId) {
        log.info("Mock: estornando pagamento com id {}", pagamentoId);
        // Simula estorno de pagamento
    }

    @Getter
    @Setter
    @ToString
    public static class ClienteDTO {
        private String id;
        private String nome;
        private String cpf;
        private String dataNascimento;
        private String endereco;
    }

    @Getter
    @Setter
    @ToString
    public static class ProdutoDTO {
        private String id;
        private String nome;
        private String sku;
        private Double preco;
    }

    @Getter
    @Setter
    @ToString
    public static class StatusPagamentoDTO {
        private UUID pagamentoId;
        private String status;
    }
}