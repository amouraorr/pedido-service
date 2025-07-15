package com.fiap.pedido.mapper;

import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.entity.PedidoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
    Pedido toDomain(PedidoRequestDTO dto);
    PedidoResponseDTO toResponse(Pedido domain);
    PedidoEntity toEntity(Pedido domain);
    Pedido toDomain(PedidoEntity entity);
}
