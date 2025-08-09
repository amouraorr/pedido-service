package com.fiap.pedido.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private String id;
    private String nome;
    private String cpf;
    private String dataNascimento;
    private String endereco;
}