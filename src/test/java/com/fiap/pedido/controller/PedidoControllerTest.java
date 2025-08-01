package com.fiap.pedido.controller;

import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.ItemPedidoResponseDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.usecase.service.PedidoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoUseCase pedidoUseCase;

    @InjectMocks
    private PedidoController pedidoController;

    @Test
    void deveCriarPedidoComSucesso() {
        PedidoRequestDTO request = criarPedidoRequest();
        PedidoResponseDTO expectedResponse = criarPedidoResponse();

        when(pedidoUseCase.criarPedido(any(PedidoRequestDTO.class))).thenReturn(expectedResponse);

        ResponseEntity<PedidoResponseDTO> response = pedidoController.criarPedido(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getId(), response.getBody().getId());
        assertEquals(expectedResponse.getClienteId(), response.getBody().getClienteId());
        assertEquals(expectedResponse.getStatus(), response.getBody().getStatus());
        verify(pedidoUseCase).criarPedido(request);
    }

    @Test
    void deveRetornarErro500AoCriarPedidoComExcecao() {
        PedidoRequestDTO request = criarPedidoRequest();

        when(pedidoUseCase.criarPedido(any(PedidoRequestDTO.class)))
                .thenThrow(new RuntimeException("Erro interno"));

        ResponseEntity<PedidoResponseDTO> response = pedidoController.criarPedido(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoUseCase).criarPedido(request);
    }

    @Test
    void deveConsultarPedidoComSucesso() {
        Long pedidoId = 1L;
        PedidoResponseDTO expectedResponse = criarPedidoResponse();

        when(pedidoUseCase.consultarPedido(pedidoId)).thenReturn(expectedResponse);

        ResponseEntity<PedidoResponseDTO> response = pedidoController.consultarPedido(pedidoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getId(), response.getBody().getId());
        assertEquals(expectedResponse.getClienteId(), response.getBody().getClienteId());
        verify(pedidoUseCase).consultarPedido(pedidoId);
    }

    @Test
    void deveRetornarNotFoundAoConsultarPedidoComExcecao() {
        Long pedidoId = 1L;

        when(pedidoUseCase.consultarPedido(pedidoId))
                .thenThrow(new RuntimeException("Pedido não encontrado"));

        ResponseEntity<PedidoResponseDTO> response = pedidoController.consultarPedido(pedidoId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoUseCase).consultarPedido(pedidoId);
    }

    @Test
    void deveListarPedidosComSucesso() {
        List<PedidoResponseDTO> expectedPedidos = Arrays.asList(
                criarPedidoResponse(),
                criarSegundoPedidoResponse()
        );

        when(pedidoUseCase.listarPedidos()).thenReturn(expectedPedidos);

        ResponseEntity<List<PedidoResponseDTO>> response = pedidoController.listarPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(expectedPedidos.get(0).getId(), response.getBody().get(0).getId());
        assertEquals(expectedPedidos.get(1).getId(), response.getBody().get(1).getId());
        verify(pedidoUseCase).listarPedidos();
    }

    @Test
    void deveRetornarErro500AoListarPedidosComExcecao() {
        when(pedidoUseCase.listarPedidos())
                .thenThrow(new RuntimeException("Erro interno"));

        ResponseEntity<List<PedidoResponseDTO>> response = pedidoController.listarPedidos();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoUseCase).listarPedidos();
    }

    @Test
    void deveAtualizarStatusComSucesso() {
        Long pedidoId = 1L;
        String novoStatus = "CONFIRMADO";
        PedidoResponseDTO expectedResponse = criarPedidoResponseComStatus(novoStatus);

        when(pedidoUseCase.atualizarStatus(pedidoId, novoStatus)).thenReturn(expectedResponse);

        ResponseEntity<PedidoResponseDTO> response = pedidoController.atualizarStatus(pedidoId, novoStatus);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getId(), response.getBody().getId());
        assertEquals(novoStatus, response.getBody().getStatus());
        verify(pedidoUseCase).atualizarStatus(pedidoId, novoStatus);
    }

    @Test
    void deveRetornarErro500AoAtualizarStatusComExcecao() {
        Long pedidoId = 1L;
        String novoStatus = "CONFIRMADO";

        when(pedidoUseCase.atualizarStatus(pedidoId, novoStatus))
                .thenThrow(new RuntimeException("Erro ao atualizar"));

        ResponseEntity<PedidoResponseDTO> response = pedidoController.atualizarStatus(pedidoId, novoStatus);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(pedidoUseCase).atualizarStatus(pedidoId, novoStatus);
    }

    private PedidoRequestDTO criarPedidoRequest() {
        return PedidoRequestDTO.builder()
                .clienteId(1L)
                .build();
    }

    private PedidoResponseDTO criarPedidoResponse() {
        return PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .status("PENDENTE")
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedido()))
                .build();
    }

    private PedidoResponseDTO criarSegundoPedidoResponse() {
        return PedidoResponseDTO.builder()
                .id(2L)
                .clienteId(2L)
                .status("CONFIRMADO")
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedido()))
                .build();
    }

    private PedidoResponseDTO criarPedidoResponseComStatus(String status) {
        return PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .status(status)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedido()))
                .build();
    }

    private ItemPedidoResponseDTO criarItemPedido() {
        return ItemPedidoResponseDTO.builder()
                .produtoId("PROD001")
                .quantidade(2)
                .precoUnitario(25.50)
                .build();
    }
}