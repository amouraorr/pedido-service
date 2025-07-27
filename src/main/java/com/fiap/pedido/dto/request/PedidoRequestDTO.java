package com.fiap.pedido.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    private Long id;
    private Long clienteId;
    private List<ItemPedidoRequestDTO> itens;
    private String status;
    private LocalDateTime dataCriacao;
    private DadosPagamentoRequestDTO dadosPagamento;

}
