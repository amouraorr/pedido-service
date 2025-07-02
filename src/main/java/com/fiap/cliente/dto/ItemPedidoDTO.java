package com.fiap.cliente.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private Long produtoId;
    private Integer quantidade;
    private Double precoUnitario;
}
