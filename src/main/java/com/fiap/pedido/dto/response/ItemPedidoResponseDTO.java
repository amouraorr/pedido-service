package com.fiap.pedido.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponseDTO {

    private String produtoId;
    private Integer quantidade;
    private Double precoUnitario;
}