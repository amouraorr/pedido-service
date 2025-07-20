package com.fiap.pedido.repository;

import com.fiap.pedido.entity.PadidoPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<PadidoPagamentoEntity, Long> {

}