package com.fiap.pagamento.service;

import com.fiap.pagamento.domain.Pedido;
import com.fiap.pagamento.dto.request.PedidoRequestDTO;
import com.fiap.pagamento.dto.response.PedidoResponseDTO;
import com.fiap.pagamento.enuns.StatusPedido;
import com.fiap.pagamento.mapper.PedidoMapper;
import com.fiap.pagamento.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoDTO) {
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido.setStatus(StatusPedido.CRIADO);
        Pedido salvo = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(salvo);
    }

    @Override
    public PedidoResponseDTO consultarPedido(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponse)
                .orElse(null);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponseDTO atualizarStatus(Long id, String status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(StatusPedido.valueOf(status));
        Pedido atualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(atualizado);
    }
}
