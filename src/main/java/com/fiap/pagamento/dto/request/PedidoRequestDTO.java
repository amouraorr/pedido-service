package com.fiap.pagamento.dto.request;

import com.fiap.pagamento.dto.response.ItemPedidoResponseDTO;
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
    private List<ItemPedidoResponseDTO> itens;
    private String status;
    private LocalDateTime dataCriacao;
}
