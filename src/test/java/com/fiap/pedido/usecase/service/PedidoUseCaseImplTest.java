package com.fiap.pedido.usecase.service;

import com.fiap.pedido.adapter.EstoqueServiceClient;
import com.fiap.pedido.domain.ItemPedido;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.ItemPedidoResponseDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.enuns.StatusPedido;
import com.fiap.pedido.mapper.PedidoMapper;
import com.fiap.pedido.pots.PedidoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private EstoqueServiceClient estoqueServiceClient;

    @InjectMocks
    private PedidoUseCaseImpl pedidoUseCase;

    private PedidoRequestDTO pedidoRequestDTO;
    private Pedido pedido;
    private PedidoResponseDTO pedidoResponseDTO;

    @BeforeEach
    void setUp() {
        pedidoRequestDTO = PedidoRequestDTO.builder()
                .clienteId(1L)
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .status(StatusPedido.ABERTO)
                .dataCriacao(LocalDateTime.now())
                .build();

        pedidoResponseDTO = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .status("ABERTO")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    void deveCriarPedidoComSucesso() {
        when(pedidoMapper.toDomain(pedidoRequestDTO)).thenReturn(pedido);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponseDTO);

        PedidoResponseDTO resultado = pedidoUseCase.criarPedido(pedidoRequestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getClienteId());
        assertEquals("ABERTO", resultado.getStatus());
        verify(pedidoMapper).toDomain(pedidoRequestDTO);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(pedidoMapper).toResponse(pedido);
    }

    @Test
    void deveLancarExcecaoAoCriarPedidoComErro() {
        when(pedidoMapper.toDomain(pedidoRequestDTO)).thenThrow(new RuntimeException("Erro no mapper"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.criarPedido(pedidoRequestDTO));

        assertEquals("Erro ao criar pedido", exception.getMessage());
        verify(pedidoMapper).toDomain(pedidoRequestDTO);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveCriarPedidoComStatusAbertoEDataAtual() {
        when(pedidoMapper.toDomain(pedidoRequestDTO)).thenReturn(pedido);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(pedidoResponseDTO);

        pedidoUseCase.criarPedido(pedidoRequestDTO);

        verify(pedidoRepository).save(argThat(p ->
                p.getStatus() == StatusPedido.ABERTO && p.getDataCriacao() != null));
    }

    @Test
    void deveConsultarPedidoComSucesso() {
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponseDTO);

        PedidoResponseDTO resultado = pedidoUseCase.consultarPedido(id);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getClienteId());
        verify(pedidoRepository).findById(id);
        verify(pedidoMapper).toResponse(pedido);
    }

    @Test
    void deveLancarExcecaoAoConsultarPedidoNaoEncontrado() {
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.consultarPedido(id));

        assertEquals("Pedido não encontrado", exception.getMessage());
        verify(pedidoRepository).findById(id);
        verify(pedidoMapper, never()).toResponse(any());
    }

    @Test
    void deveLancarExcecaoAoConsultarPedidoComErro() {
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenThrow(new RuntimeException("Erro no banco"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.consultarPedido(id));

        assertEquals("Erro no banco", exception.getMessage());
        verify(pedidoRepository).findById(id);
    }

    @Test
    void deveListarPedidosComSucesso() {
        List<Pedido> pedidos = Arrays.asList(pedido);
        List<PedidoResponseDTO> pedidosResponse = Arrays.asList(pedidoResponseDTO);

        when(pedidoRepository.findAll()).thenReturn(pedidos);
        when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponseDTO);

        List<PedidoResponseDTO> resultado = pedidoUseCase.listarPedidos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(pedidoRepository).findAll();
        verify(pedidoMapper).toResponse(pedido);
    }

    @Test
    void deveListarPedidosVazio() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList());

        List<PedidoResponseDTO> resultado = pedidoUseCase.listarPedidos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pedidoRepository).findAll();
    }

    @Test
    void deveLancarExcecaoAoListarPedidosComErro() {
        when(pedidoRepository.findAll()).thenThrow(new RuntimeException("Erro no banco"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.listarPedidos());

        assertEquals("Erro no banco", exception.getMessage());
        verify(pedidoRepository).findAll();
    }

    @Test
    void deveAtualizarStatusComSucesso() {
        Long id = 1L;
        String status = "FECHADO_COM_SUCESSO";
        Pedido pedidoAtualizado = Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .status(StatusPedido.FECHADO_COM_SUCESSO)
                .dataCriacao(LocalDateTime.now())
                .build();
        PedidoResponseDTO responseAtualizado = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .status("FECHADO_COM_SUCESSO")
                .dataCriacao(LocalDateTime.now())
                .build();

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoAtualizado);
        when(pedidoMapper.toResponse(pedidoAtualizado)).thenReturn(responseAtualizado);

        PedidoResponseDTO resultado = pedidoUseCase.atualizarStatus(id, status);

        assertNotNull(resultado);
        assertEquals("FECHADO_COM_SUCESSO", resultado.getStatus());
        verify(pedidoRepository).findById(id);
        verify(pedidoRepository).save(argThat(p -> p.getStatus() == StatusPedido.FECHADO_COM_SUCESSO));
        verify(pedidoMapper).toResponse(pedidoAtualizado);
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusPedidoNaoEncontrado() {
        Long id = 1L;
        String status = "FECHADO_COM_SUCESSO";
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.atualizarStatus(id, status));

        assertEquals("Pedido não encontrado", exception.getMessage());
        verify(pedidoRepository).findById(id);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusInvalido() {
        Long id = 1L;
        String status = "STATUS_INVALIDO";
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.atualizarStatus(id, status));

        assertTrue(exception.getMessage().contains("Status inválido"));
        verify(pedidoRepository).findById(id);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusComErroGenerico() {
        Long id = 1L;
        String status = "FECHADO_COM_SUCESSO";
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenThrow(new RuntimeException("Erro no banco"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pedidoUseCase.atualizarStatus(id, status));

        assertEquals("Erro no banco", exception.getMessage());
        verify(pedidoRepository).findById(id);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void deveAtualizarStatusComTodosStatusValidos() {
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(pedidoResponseDTO);

        String[] statusValidos = {"ABERTO", "FECHADO_COM_SUCESSO", "FECHADO_SEM_ESTOQUE", "FECHADO_SEM_CREDITO", "CANCELADO"};

        for (String status : statusValidos) {
            assertDoesNotThrow(() -> pedidoUseCase.atualizarStatus(id, status));
        }
    }
}