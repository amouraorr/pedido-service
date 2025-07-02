package com.fiap.cliente.controller;

import com.fiap.cliente.dto.PedidoDTO;
import com.fiap.cliente.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public PedidoDTO criarPedido(@RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.criarPedido(pedidoDTO);
    }

    @GetMapping("/{id}")
    public PedidoDTO consultarPedido(@PathVariable Long id) {
        return pedidoService.consultarPedido(id);
    }

    @GetMapping
    public List<PedidoDTO> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @PatchMapping("/{id}/status")
    public PedidoDTO atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        return pedidoService.atualizarStatus(id, status);
    }
}
