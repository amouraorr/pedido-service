package com.fiap.pagamento.mapper;

import com.fiap.pagamento.domain.Pedido;
import com.fiap.pagamento.dto.request.PedidoRequestDTO;
import com.fiap.pagamento.dto.response.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    PedidoResponseDTO toResponse(Pedido pedido);
    Pedido toEntity(PedidoRequestDTO pedidoRequest);
}