package com.fiap.pedido.gateway.adapter;

import com.fiap.pedido.adapter.EstoqueServiceClient;
import com.fiap.pedido.gateway.EstoqueGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstoqueGatewayAdapter implements EstoqueGateway {

    private final EstoqueServiceClient estoqueServiceClient;

    @Override
    public boolean baixarEstoque(String produtoId, int quantidade) {
        try {
            estoqueServiceClient.baixarEstoque(produtoId, quantidade);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}