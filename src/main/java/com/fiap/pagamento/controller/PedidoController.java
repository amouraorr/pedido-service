package com.fiap.pagamento.controller;

import com.fiap.pagamento.dto.request.PedidoRequestDTO;
import com.fiap.pagamento.dto.response.PedidoResponseDTO;
import com.fiap.pagamento.usecase.PedidoUseCase;
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
