package com.fiap.pedido.gateway.adapter;

import com.fiap.pedido.dto.StatusPagamentoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagamentoGatewayAdapterTest {

    @InjectMocks
    private PagamentoGatewayAdapter pagamentoGatewayAdapter;

    @Test
    void deveProcessarPagamentoComSucessoParaValorMenorOuIgualMil() {
        String numeroCartao = "1234567890123456";
        Double valor = 500.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
        assertTrue(resultado.getDataPagamento().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void deveProcessarPagamentoComSucessoParaValorExatamenteMil() {
        String numeroCartao = "1234567890123456";
        Double valor = 1000.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
    }

    @Test
    void deveRecusarPagamentoParaValorMaiorQueMil() {
        String numeroCartao = "1234567890123456";
        Double valor = 1500.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("RECUSADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
    }

    @Test
    void deveRecusarPagamentoParaValorMuitoAlto() {
        String numeroCartao = "9999999999999999";
        Double valor = 10000.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("RECUSADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
    }

    @Test
    void deveProcessarPagamentoParaValorZero() {
        String numeroCartao = "1111111111111111";
        Double valor = 0.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
    }

    @Test
    void deveGerarPagamentoIdUnicoParaCadaTransacao() {
        String numeroCartao = "1234567890123456";
        Double valor = 100.0;

        StatusPagamentoDTO resultado1 = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);
        StatusPagamentoDTO resultado2 = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado1.getPagamentoId());
        assertNotNull(resultado2.getPagamentoId());
        assertNotEquals(resultado1.getPagamentoId(), resultado2.getPagamentoId());
    }

    @Test
    void deveEstornarPagamentoSemErro() {
        String pagamentoId = "12345678-1234-1234-1234-123456789012";

        assertDoesNotThrow(() -> {
            pagamentoGatewayAdapter.estornarPagamento(pagamentoId);
        });
    }

    @Test
    void deveEstornarPagamentoComIdNulo() {
        String pagamentoId = null;

        assertDoesNotThrow(() -> {
            pagamentoGatewayAdapter.estornarPagamento(pagamentoId);
        });
    }

    @Test
    void deveEstornarPagamentoComIdVazio() {
        String pagamentoId = "";

        assertDoesNotThrow(() -> {
            pagamentoGatewayAdapter.estornarPagamento(pagamentoId);
        });
    }

    @Test
    void deveProcessarPagamentoComCartaoNulo() {
        String numeroCartao = null;
        Double valor = 500.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
    }

    @Test
    void deveProcessarPagamentoComValorNulo() {
        String numeroCartao = "1234567890123456";
        Double valor = null;

        assertThrows(NullPointerException.class, () -> {
            pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);
        });
    }

    @Test
    void deveProcessarPagamentoComValorNegativo() {
        String numeroCartao = "1234567890123456";
        Double valor = -100.0;

        StatusPagamentoDTO resultado = pagamentoGatewayAdapter.processarPagamento(numeroCartao, valor);

        assertNotNull(resultado);
        assertEquals("APROVADO", resultado.getStatus());
        assertNotNull(resultado.getPagamentoId());
        assertNotNull(resultado.getDataPagamento());
    }
}