package com.fiap.cliente.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private List<ItemPedidoDTO> itens;
    private String status;
    private LocalDateTime dataCriacao;
}
