package com.fiap.pedido.gateway.adapter;

import com.fiap.pedido.adapter.EstoqueServiceClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueGatewayAdapterTest {

    @Mock
    private EstoqueServiceClient estoqueServiceClient;

    @InjectMocks
    private EstoqueGatewayAdapter estoqueGatewayAdapter;

    @Test
    void deveBaixarEstoqueComSucesso() {
        doNothing().when(estoqueServiceClient).baixarEstoque("SKU001", 5);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque("SKU001", 5);

        assertTrue(resultado);
        verify(estoqueServiceClient).baixarEstoque("SKU001", 5);
    }

    @Test
    void deveRetornarFalsoQuandoOcorrerRuntimeException() {
        doThrow(new RuntimeException("Erro interno"))
                .when(estoqueServiceClient).baixarEstoque("SKU002", 3);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque("SKU002", 3);

        assertFalse(resultado);
        verify(estoqueServiceClient).baixarEstoque("SKU002", 3);
    }

    @Test
    void deveRetornarFalsoQuandoOcorrerFeignException() {
        doThrow(mock(FeignException.class))
                .when(estoqueServiceClient).baixarEstoque("SKU003", 2);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque("SKU003", 2);

        assertFalse(resultado);
        verify(estoqueServiceClient).baixarEstoque("SKU003", 2);
    }

    @Test
    void deveBaixarEstoqueComQuantidadeZero() {
        doNothing().when(estoqueServiceClient).baixarEstoque("SKU004", 0);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque("SKU004", 0);

        assertTrue(resultado);
        verify(estoqueServiceClient).baixarEstoque("SKU004", 0);
    }

    @Test
    void deveBaixarEstoqueComQuantidadeAlta() {
        doNothing().when(estoqueServiceClient).baixarEstoque("SKU005", 1000);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque("SKU005", 1000);

        assertTrue(resultado);
        verify(estoqueServiceClient).baixarEstoque("SKU005", 1000);
    }

    @Test
    void deveRetornarFalsoQuandoOcorrerIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Parâmetro inválido"))
                .when(estoqueServiceClient).baixarEstoque("SKU006", -1);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque("SKU006", -1);

        assertFalse(resultado);
        verify(estoqueServiceClient).baixarEstoque("SKU006", -1);
    }

    @Test
    void devePassarParametrosCorretamente() {
        String produtoId = "PRODUTO-123";
        int quantidade = 15;

        doNothing().when(estoqueServiceClient).baixarEstoque(produtoId, quantidade);

        boolean resultado = estoqueGatewayAdapter.baixarEstoque(produtoId, quantidade);

        assertTrue(resultado);
        verify(estoqueServiceClient).baixarEstoque(eq(produtoId), eq(quantidade));
    }
}