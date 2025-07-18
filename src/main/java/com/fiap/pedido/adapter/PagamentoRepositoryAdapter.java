package com.fiap.pedido.adapter;

import com.fiap.pedido.entity.PagamentoEntity;
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
    public PagamentoEntity save(PagamentoEntity pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    @Override
    public Optional<PagamentoEntity> findById(Long id) {
        return pagamentoRepository.findById(id);
    }
}