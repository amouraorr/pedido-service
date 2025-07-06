package com.fiap.pagamento.repository;

import com.fiap.pagamento.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
/*    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();*/
}
