package com.fiap.pedido.controller;

import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.usecase.service.PedidoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {


    private final PedidoUseCase pedidoUseCase;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        log.info("Requisição para criar pedido: {}", pedidoRequest);
        try {
            PedidoResponseDTO response = pedidoUseCase.criarPedido(pedidoRequest);
            log.info("Pedido criado com sucesso: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao criar pedido: {}", pedidoRequest, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> consultarPedido(@PathVariable Long id) {
        log.info("Requisição para consultar pedido id: {}", id);
        try {
            PedidoResponseDTO response = pedidoUseCase.consultarPedido(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao consultar pedido id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos() {
        log.info("Requisição para listar pedidos");
        try {
            List<PedidoResponseDTO> pedidos = pedidoUseCase.listarPedidos();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            log.error("Erro ao listar pedidos", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("Requisição para atualizar status do pedido id: {} para status: {}", id, status);
        try {
            PedidoResponseDTO response = pedidoUseCase.atualizarStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido id: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }
}

