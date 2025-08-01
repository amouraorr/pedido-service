package com.fiap.pedido.enuns;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusPedidoTest {

    @Test
    void deveConterTodosOsValoresEsperados() {
        StatusPedido[] values = StatusPedido.values();

        assertEquals(5, values.length);
        assertEquals(StatusPedido.ABERTO, values[0]);
        assertEquals(StatusPedido.FECHADO_COM_SUCESSO, values[1]);
        assertEquals(StatusPedido.FECHADO_SEM_ESTOQUE, values[2]);
        assertEquals(StatusPedido.FECHADO_SEM_CREDITO, values[3]);
        assertEquals(StatusPedido.CANCELADO, values[4]);
    }

    @Test
    void deveRetornarValorCorretoParaValueOf() {
        assertEquals(StatusPedido.ABERTO, StatusPedido.valueOf("ABERTO"));
        assertEquals(StatusPedido.FECHADO_COM_SUCESSO, StatusPedido.valueOf("FECHADO_COM_SUCESSO"));
        assertEquals(StatusPedido.FECHADO_SEM_ESTOQUE, StatusPedido.valueOf("FECHADO_SEM_ESTOQUE"));
        assertEquals(StatusPedido.FECHADO_SEM_CREDITO, StatusPedido.valueOf("FECHADO_SEM_CREDITO"));
        assertEquals(StatusPedido.CANCELADO, StatusPedido.valueOf("CANCELADO"));
    }

    @Test
    void deveLancarExcecaoParaValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> StatusPedido.valueOf("INVALIDO"));
        assertThrows(IllegalArgumentException.class, () -> StatusPedido.valueOf(""));
        assertThrows(NullPointerException.class, () -> StatusPedido.valueOf(null));
    }

    @Test
    void deveRetornarNomeCorretoParaToString() {
        assertEquals("ABERTO", StatusPedido.ABERTO.toString());
        assertEquals("FECHADO_COM_SUCESSO", StatusPedido.FECHADO_COM_SUCESSO.toString());
        assertEquals("FECHADO_SEM_ESTOQUE", StatusPedido.FECHADO_SEM_ESTOQUE.toString());
        assertEquals("FECHADO_SEM_CREDITO", StatusPedido.FECHADO_SEM_CREDITO.toString());
        assertEquals("CANCELADO", StatusPedido.CANCELADO.toString());
    }

    @Test
    void deveRetornarNomeCorretoParaName() {
        assertEquals("ABERTO", StatusPedido.ABERTO.name());
        assertEquals("FECHADO_COM_SUCESSO", StatusPedido.FECHADO_COM_SUCESSO.name());
        assertEquals("FECHADO_SEM_ESTOQUE", StatusPedido.FECHADO_SEM_ESTOQUE.name());
        assertEquals("FECHADO_SEM_CREDITO", StatusPedido.FECHADO_SEM_CREDITO.name());
        assertEquals("CANCELADO", StatusPedido.CANCELADO.name());
    }

    @Test
    void deveRetornarOrdinalCorreto() {
        assertEquals(0, StatusPedido.ABERTO.ordinal());
        assertEquals(1, StatusPedido.FECHADO_COM_SUCESSO.ordinal());
        assertEquals(2, StatusPedido.FECHADO_SEM_ESTOQUE.ordinal());
        assertEquals(3, StatusPedido.FECHADO_SEM_CREDITO.ordinal());
        assertEquals(4, StatusPedido.CANCELADO.ordinal());
    }

    @Test
    void deveCompararEnumsCorretamente() {
        assertTrue(StatusPedido.ABERTO.equals(StatusPedido.ABERTO));
        assertFalse(StatusPedido.ABERTO.equals(StatusPedido.CANCELADO));

        assertEquals(StatusPedido.FECHADO_COM_SUCESSO, StatusPedido.FECHADO_COM_SUCESSO);
        assertNotEquals(StatusPedido.FECHADO_SEM_ESTOQUE, StatusPedido.FECHADO_SEM_CREDITO);
    }
}