package com.fiap.cliente.mapper;

import com.fiap.cliente.domain.Pedido;
import com.fiap.cliente.dto.PedidoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    PedidoDTO toDTO(Pedido pedido);
    Pedido toEntity(PedidoDTO pedidoDTO);
}