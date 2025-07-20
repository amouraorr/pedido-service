package com.fiap.pedido.pots;

import com.fiap.pedido.entity.PadidoPagamentoEntity;

import java.util.Optional;

public interface PagamentoRepositoryPort {
    PadidoPagamentoEntity save(PadidoPagamentoEntity pagamento);
    Optional<PadidoPagamentoEntity> findById(Long id);
}