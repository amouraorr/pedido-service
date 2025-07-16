package com.fiap.pedido.mapper;

import com.fiap.pedido.domain.ItemPedido;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.dto.request.ItemPedidoRequestDTO;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.ItemPedidoResponseDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.entity.ItemPedidoEntity;
import com.fiap.pedido.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    Pedido toDomain(PedidoRequestDTO dto);

    PedidoResponseDTO toResponse(Pedido domain);

    PedidoEntity toEntity(Pedido domain);

    Pedido toDomain(PedidoEntity entity);

    // Mapear lista de itens
    List<ItemPedido> toDomainItens(List<ItemPedidoRequestDTO> itens);

    List<ItemPedidoResponseDTO> toResponseItens(List<ItemPedido> itens);

    List<ItemPedidoEntity> toEntityItens(List<ItemPedido> itens);

    List<ItemPedido> toDomainItensEntity(List<ItemPedidoEntity> itens);
}