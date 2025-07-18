package com.fiap.pedido.pots;

import com.fiap.pedido.entity.PagamentoEntity;

import java.util.Optional;

public interface PagamentoRepositoryPort {
    PagamentoEntity save(PagamentoEntity pagamento);
    Optional<PagamentoEntity> findById(Long id);
}