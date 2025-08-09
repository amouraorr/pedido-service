package com.fiap.pedido.repository;

import com.fiap.pedido.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    @Query("SELECT p FROM PedidoEntity p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<PedidoEntity> findByIdWithItens(@Param("id") Long id);
}