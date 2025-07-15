package com.fiap.pedido.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long id;
    private Long clienteId;
    private List<ItemPedidoResponseDTO> itens;
    private String status;
    private LocalDateTime dataCriacao;
}
