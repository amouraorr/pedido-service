package com.fiap.pagamento.mapper;

import com.fiap.pagamento.domain.Pedido;
import com.fiap.pagamento.dto.request.PedidoRequestDTO;
import com.fiap.pagamento.dto.response.PedidoResponseDTO;
import com.fiap.pagamento.entity.PedidoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
    Pedido toDomain(PedidoRequestDTO dto);
    PedidoResponseDTO toResponse(Pedido domain);
    PedidoEntity toEntity(Pedido domain);
    Pedido toDomain(PedidoEntity entity);
}
