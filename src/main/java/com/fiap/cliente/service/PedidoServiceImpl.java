package com.fiap.cliente.service;

import com.fiap.cliente.domain.Pedido;
import com.fiap.cliente.dto.PedidoDTO;
import com.fiap.cliente.enuns.StatusPedido;
import com.fiap.cliente.mapper.PedidoMapper;
import com.fiap.cliente.repository.PedidoRepository;
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
    public PedidoDTO criarPedido(PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido.setStatus(StatusPedido.CRIADO);
        Pedido salvo = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(salvo);
    }

    @Override
    public PedidoDTO consultarPedido(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<PedidoDTO> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoDTO atualizarStatus(Long id, String status) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();
        pedido.setStatus(StatusPedido.valueOf(status));
        Pedido atualizado = pedidoRepository.save(pedido);
        return pedidoMapper.toDTO(atualizado);
    }
}