package com.fiap.pedido.dto;

import lombok.Data;

@Data
public class ProdutoDTO {
    private String id;
    private String nome;
    private String sku;
    private Double preco;
}