package com.fiap.pedido.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosPagamentoRequestDTO {
    private String metodoPagamento;
    private String numeroCartao;
}