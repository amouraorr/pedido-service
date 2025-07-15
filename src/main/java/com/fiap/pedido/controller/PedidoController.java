package com.fiap.pedido.controller;

import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.usecase.service.PedidoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoUseCase pedidoUseCase;

    @PostMapping
    public PedidoResponseDTO criarPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        return pedidoUseCase.criarPedido(pedidoRequest);
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO consultarPedido(@PathVariable Long id) {
        return pedidoUseCase.consultarPedido(id);
    }

    @GetMapping
    public List<PedidoResponseDTO> listarPedidos() {
        return pedidoUseCase.listarPedidos();
    }

    @PatchMapping("/{id}/status")
    public PedidoResponseDTO atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        return pedidoUseCase.atualizarStatus(id, status);
    }
}
