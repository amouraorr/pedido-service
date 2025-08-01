package com.fiap.pedido.adapter;

import com.fiap.pedido.dto.ClienteDTO;
import com.fiap.pedido.dto.ProdutoDTO;
import com.fiap.pedido.dto.StatusPagamentoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServicoExternoAdapterTest {

    @InjectMocks
    private ServicoExternoAdapter servicoExternoAdapter;

    @Mock
    private Logger log;

    @BeforeEach
    void setUp() {
        servicoExternoAdapter = new ServicoExternoAdapter();
    }

    @Test
    void deveConsultarClienteComSucesso() {
        String clienteId = "123";

        ClienteDTO resultado = servicoExternoAdapter.consultarCliente(clienteId);

        assertNotNull(resultado);
        assertEquals("123", resultado.getId());
        assertEquals("Cliente Mock", resultado.getNome());
        assertEquals("123.456.789-00", resultado.getCpf());
        assertEquals("1990-01-01", resultado.getDataNascimento());
        assertEquals("Rua Mock, 123, 00000-000", resultado.getEndereco());
    }

    @Test
    void deveConsultarClienteComIdDiferente() {
        String clienteId = "456";

        ClienteDTO resultado = servicoExternoAdapter.consultarCliente(clienteId);

        assertNotNull(resultado);
        assertEquals("456", resultado.getId());
        assertEquals("Cliente Mock", resultado.getNome());
        assertEquals("123.456.789-00", resultado.getCpf());
        assertEquals("1990-01-01", resultado.getDataNascimento());
        assertEquals("Rua Mock, 123, 00000-000", resultado.getEndereco());
    }

    @Test
    void deveConsultarProdutoComSucesso() {
        String produtoId = "PROD123";

        ProdutoDTO resultado = servicoExternoAdapter.consultarProduto(produtoId);

        assertNotNull(resultado);
        assertEquals("PROD123", resultado.getId());
        assertEquals("Produto Mock", resultado.getNome());
        assertEquals("PROD123", resultado.getSku());
        assertEquals(100.0, resultado.getPreco());
    }

    @Test
    void deveConsultarProdutoComIdDiferente() {
        String produtoId = "PROD456";

        ProdutoDTO resultado = servicoExternoAdapter.consultarProduto(produtoId);

        assertNotNull(resultado);
        assertEquals("PROD456", resultado.getId());
        assertEquals("Produto Mock", resultado.getNome());
        assertEquals("PROD456", resultado.getSku());
        assertEquals(100.0, resultado.getPreco());
    }

    @Test
    void deveReservarEstoqueComSucesso() {
        String produtoId = "PROD123";
        int quantidade = 5;

        boolean resultado = servicoExternoAdapter.reservarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveReservarEstoqueComQuantidadeZero() {
        String produtoId = "PROD123";
        int quantidade = 0;

        boolean resultado = servicoExternoAdapter.reservarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveReservarEstoqueComQuantidadeNegativa() {
        String produtoId = "PROD123";
        int quantidade = -1;

        boolean resultado = servicoExternoAdapter.reservarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarEstoqueComSucesso() {
        String produtoId = "PROD123";
        int quantidade = 3;

        boolean resultado = servicoExternoAdapter.estornarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarEstoqueComQuantidadeZero() {
        String produtoId = "PROD123";
        int quantidade = 0;

        boolean resultado = servicoExternoAdapter.estornarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarEstoqueComQuantidadeNegativa() {
        String produtoId = "PROD123";
        int quantidade = -1;

        boolean resultado = servicoExternoAdapter.estornarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveBaixarEstoqueComSucesso() {
        String produtoId = "PROD123";
        int quantidade = 2;

        boolean resultado = servicoExternoAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveBaixarEstoqueComQuantidadeZero() {
        String produtoId = "PROD123";
        int quantidade = 0;

        boolean resultado = servicoExternoAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveBaixarEstoqueComQuantidadeNegativa() {
        String produtoId = "PROD123";
        int quantidade = -1;

        boolean resultado = servicoExternoAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveProcessarPagamentoComSucesso() {
        String numeroCartao = "1234567890123456";
        double valor = 250.50;

        StatusPagamentoDTO resultado = servicoExternoAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("MOCK123", resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComValorZero() {
        String numeroCartao = "1234567890123456";
        double valor = 0.0;

        StatusPagamentoDTO resultado = servicoExternoAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("MOCK123", resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComValorNegativo() {
        String numeroCartao = "1234567890123456";
        double valor = -100.0;

        StatusPagamentoDTO resultado = servicoExternoAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("MOCK123", resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComCartaoNulo() {
        String numeroCartao = null;
        double valor = 150.0;

        StatusPagamentoDTO resultado = servicoExternoAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("MOCK123", resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveEstornarPagamentoComSucesso() {
        String pagamentoId = "PAG123";

        boolean resultado = servicoExternoAdapter.estornarPagamento(pagamentoId);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarPagamentoComIdNulo() {
        String pagamentoId = null;

        boolean resultado = servicoExternoAdapter.estornarPagamento(pagamentoId);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarPagamentoComIdVazio() {
        String pagamentoId = "";

        boolean resultado = servicoExternoAdapter.estornarPagamento(pagamentoId);

        assertTrue(resultado);
    }
}