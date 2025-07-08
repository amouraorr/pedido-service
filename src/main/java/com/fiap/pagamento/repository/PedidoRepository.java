package com.fiap.pagamento.repository;

import com.fiap.pagamento.domain.Pedido;
import com.fiap.pagamento.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
}
