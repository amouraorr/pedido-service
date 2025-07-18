package com.fiap.pedido.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusPagamentoDTO {
    private String pagamentoId;
    private String status;
    private LocalDateTime dataPagamento;
}