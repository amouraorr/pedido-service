package com.fiap.pagamento.domain;

import com.fiap.pagamento.enuns.StatusPedido;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long id;
    private Long clienteId;
    private List<ItemPedido> itens;
    private StatusPedido status;
    private LocalDateTime dataCriacao;
}
