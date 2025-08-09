package com.fiap.pedido.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponseDTO {

    private String pagamentoId;
    private String status;
    private String numeroCartao;
}