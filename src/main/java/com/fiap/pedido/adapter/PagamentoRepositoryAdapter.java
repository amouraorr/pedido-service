package com.fiap.pedido.adapter;

import com.fiap.pedido.entity.PadidoPagamentoEntity;
import com.fiap.pedido.pots.PagamentoRepositoryPort;
import com.fiap.pedido.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PagamentoRepositoryAdapter implements PagamentoRepositoryPort {

    private final PagamentoRepository pagamentoRepository;

    @Override
    public PadidoPagamentoEntity save(PadidoPagamentoEntity pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    @Override
    public Optional<PadidoPagamentoEntity> findById(Long id) {
        return pagamentoRepository.findById(id);
    }
}