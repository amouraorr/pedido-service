package com.fiap.pedido.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequestDTO {

    private String produtoId;
    private Integer quantidade;
}
