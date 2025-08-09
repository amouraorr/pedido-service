package com.fiap.pedido.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {

    private Long pedidoId;
    private BigDecimal valor;
    private String metodoPagamento;
    private String numeroCartao;

}
