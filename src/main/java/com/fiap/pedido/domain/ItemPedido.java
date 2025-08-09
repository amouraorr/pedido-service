package com.fiap.pedido.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    private String produtoId;
    private Integer quantidade;
    private Double precoUnitario;
}
