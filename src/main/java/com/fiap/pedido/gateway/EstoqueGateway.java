package com.fiap.pedido.gateway;

public interface EstoqueGateway {
    boolean baixarEstoque(String produtoId, int quantidade);
}