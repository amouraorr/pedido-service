package com.fiap.pagamento.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    private Long produtoId;
    private Integer quantidade;
    private Double precoUnitario;
}
