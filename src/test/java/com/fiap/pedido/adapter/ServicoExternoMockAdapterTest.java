package com.fiap.pedido.adapter;

import com.fiap.pedido.dto.ClienteDTO;
import com.fiap.pedido.dto.ProdutoDTO;
import com.fiap.pedido.dto.StatusPagamentoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServicoExternoMockAdapterTest {

    private ServicoExternoMockAdapter servicoExternoMockAdapter;

    @BeforeEach
    void setUp() {
        servicoExternoMockAdapter = new ServicoExternoMockAdapter();
    }

    @Test
    void deveConsultarClienteComSucesso() {
        String clienteId = "123";

        ClienteDTO resultado = servicoExternoMockAdapter.consultarCliente(clienteId);

        assertNotNull(resultado);
        assertEquals("123", resultado.getId());
        assertEquals("Cliente Mock", resultado.getNome());
        assertEquals("123.456.789-00", resultado.getCpf());
        assertEquals("1990-01-01", resultado.getDataNascimento());
        assertEquals("Rua Mock, 123, 00000-000", resultado.getEndereco());
    }

    @Test
    void deveConsultarClienteComIdNulo() {
        String clienteId = null;

        ClienteDTO resultado = servicoExternoMockAdapter.consultarCliente(clienteId);

        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertEquals("Cliente Mock", resultado.getNome());
        assertEquals("123.456.789-00", resultado.getCpf());
        assertEquals("1990-01-01", resultado.getDataNascimento());
        assertEquals("Rua Mock, 123, 00000-000", resultado.getEndereco());
    }

    @Test
    void deveConsultarProdutoComSucesso() {
        String produtoId = "PROD123";

        ProdutoDTO resultado = servicoExternoMockAdapter.consultarProduto(produtoId);

        assertNotNull(resultado);
        assertEquals("PROD123", resultado.getId());
        assertEquals("Produto Mock", resultado.getNome());
        assertEquals("PROD123", resultado.getSku());
        assertEquals(100.0, resultado.getPreco());
    }

    @Test
    void deveConsultarProdutoComIdNulo() {
        String produtoId = null;

        ProdutoDTO resultado = servicoExternoMockAdapter.consultarProduto(produtoId);

        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertEquals("Produto Mock", resultado.getNome());
        assertNull(resultado.getSku());
        assertEquals(100.0, resultado.getPreco());
    }

    @Test
    void deveReservarEstoqueComSucessoQuandoTemEstoqueDisponivel() {
        String produtoId = "SKU001";
        Integer quantidade = 2;

        boolean resultado = servicoExternoMockAdapter.reservarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveReservarEstoqueComSucessoQuandoQuantidadeMenorQueDisponivel() {
        String produtoId = "SKU002";
        Integer quantidade = 1;

        boolean resultado = servicoExternoMockAdapter.reservarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveFalharAoReservarEstoqueQuandoQuantidadeMaiorQueDisponivel() {
        String produtoId = "SKU003";
        Integer quantidade = 5;

        boolean resultado = servicoExternoMockAdapter.reservarEstoque(produtoId, quantidade);

        assertFalse(resultado);
    }

    @Test
    void deveFalharAoReservarEstoqueQuandoProdutoNaoExiste() {
        String produtoId = "SKU999";
        Integer quantidade = 1;

        boolean resultado = servicoExternoMockAdapter.reservarEstoque(produtoId, quantidade);

        assertFalse(resultado);
    }

    @Test
    void deveReservarEstoqueComQuantidadeZero() {
        String produtoId = "SKU001";
        Integer quantidade = 0;

        boolean resultado = servicoExternoMockAdapter.reservarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveBaixarEstoqueComSucessoQuandoTemEstoqueDisponivel() {
        String produtoId = "SKU001";
        Integer quantidade = 1;

        boolean resultado = servicoExternoMockAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveBaixarEstoqueEAtualizarEstoqueDisponivel() {
        String produtoId = "SKU001";
        Integer quantidade = 2;

        boolean resultado = servicoExternoMockAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
        boolean segundaOperacao = servicoExternoMockAdapter.baixarEstoque(produtoId, 1);
        assertFalse(segundaOperacao);
    }

    @Test
    void deveFalharAoBaixarEstoqueQuandoQuantidadeMaiorQueDisponivel() {
        String produtoId = "SKU002";
        Integer quantidade = 5;

        boolean resultado = servicoExternoMockAdapter.baixarEstoque(produtoId, quantidade);

        assertFalse(resultado);
    }

    @Test
    void deveFalharAoBaixarEstoqueQuandoProdutoNaoExiste() {
        String produtoId = "SKU999";
        Integer quantidade = 1;

        boolean resultado = servicoExternoMockAdapter.baixarEstoque(produtoId, quantidade);

        assertFalse(resultado);
    }

    @Test
    void deveBaixarEstoqueComQuantidadeZero() {
        String produtoId = "SKU001";
        Integer quantidade = 0;

        boolean resultado = servicoExternoMockAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarEstoqueComSucesso() {
        String produtoId = "SKU001";
        Integer quantidade = 3;

        boolean resultado = servicoExternoMockAdapter.estornarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarEstoqueParaProdutoInexistente() {
        String produtoId = "SKU999";
        Integer quantidade = 5;

        boolean resultado = servicoExternoMockAdapter.estornarEstoque(produtoId, quantidade);

        assertTrue(resultado);
    }

    @Test
    void deveEstornarEstoqueEAumentarEstoqueDisponivel() {
        String produtoId = "SKU003";
        Integer quantidadeBaixa = 2;
        Integer quantidadeEstorno = 1;

        servicoExternoMockAdapter.baixarEstoque(produtoId, quantidadeBaixa);
        boolean resultado = servicoExternoMockAdapter.estornarEstoque(produtoId, quantidadeEstorno);

        assertTrue(resultado);
        boolean novaReserva = servicoExternoMockAdapter.reservarEstoque(produtoId, 1);
        assertTrue(novaReserva);
    }

    @Test
    void deveProcessarPagamentoComStatusAprovadoParaValorMenorOuIgualMil() {
        String numeroCartao = "1234567890123456";
        Double valorTotal = 500.0;

        StatusPagamentoDTO resultado = servicoExternoMockAdapter.processarPagamento(numeroCartao, valorTotal);

        assertNotNull(resultado);
        assertNotNull(resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComStatusAprovadoParaValorExatamenteMil() {
        String numeroCartao = "1234567890123456";
        Double valorTotal = 1000.0;

        StatusPagamentoDTO resultado = servicoExternoMockAdapter.processarPagamento(numeroCartao, valorTotal);

        assertNotNull(resultado);
        assertNotNull(resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComStatusRecusadoParaValorMaiorQueMil() {
        String numeroCartao = "1234567890123456";
        Double valorTotal = 1500.0;

        StatusPagamentoDTO resultado = servicoExternoMockAdapter.processarPagamento(numeroCartao, valorTotal);

        assertNotNull(resultado);
        assertNotNull(resultado.getPagamentoId());
        assertEquals("RECUSADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComStatusRecusadoParaCartaoInvalido() {
        String numeroCartao = "0000000000000000";
        Double valorTotal = 100.0;

        StatusPagamentoDTO resultado = servicoExternoMockAdapter.processarPagamento(numeroCartao, valorTotal);

        assertNotNull(resultado);
        assertNotNull(resultado.getPagamentoId());
        assertEquals("RECUSADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComValorZero() {
        String numeroCartao = "1234567890123456";
        Double valorTotal = 0.0;

        StatusPagamentoDTO resultado = servicoExternoMockAdapter.processarPagamento(numeroCartao, valorTotal);

        assertNotNull(resultado);
        assertNotNull(resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveProcessarPagamentoComValorNegativo() {
        String numeroCartao = "1234567890123456";
        Double valorTotal = -100.0;

        StatusPagamentoDTO resultado = servicoExternoMockAdapter.processarPagamento(numeroCartao, valorTotal);

        assertNotNull(resultado);
        assertNotNull(resultado.getPagamentoId());
        assertEquals("APROVADO", resultado.getStatus());
    }

    @Test
    void deveEstornarPagamentoSemRetorno() {
        String pagamentoId = "PAGAMENTO123";

        assertDoesNotThrow(() -> servicoExternoMockAdapter.estornarPagamento(pagamentoId));
    }

    @Test
    void deveEstornarPagamentoComIdNulo() {
        String pagamentoId = null;

        assertDoesNotThrow(() -> servicoExternoMockAdapter.estornarPagamento(pagamentoId));
    }

    @Test
    void deveEstornarPagamentoComIdVazio() {
        String pagamentoId = "";

        assertDoesNotThrow(() -> servicoExternoMockAdapter.estornarPagamento(pagamentoId));
    }
}