package com.fiap.pagamento.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequestDTO {

    private Long produtoId;
    private Integer quantidade;
}
