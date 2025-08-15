package com.fiap.pedido.adapter;

import com.fiap.pedido.domain.ItemPedido;
import com.fiap.pedido.domain.Pedido;
import com.fiap.pedido.entity.ItemPedidoEntity;
import com.fiap.pedido.entity.PedidoEntity;
import com.fiap.pedido.enuns.StatusPedido;
import com.fiap.pedido.mapper.PedidoMapper;
import com.fiap.pedido.repository.PedidoRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryAdapterTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoRepositoryAdapter pedidoRepositoryAdapter;

    @Test
    void deveSalvarPedidoComSucesso() {
        Pedido pedidoDomain = criarPedidoDomain();
        PedidoEntity pedidoEntity = criarPedidoEntity();
        PedidoEntity pedidoSalvo = criarPedidoEntitySalvo();
        Pedido pedidoRetorno = criarPedidoDomainSalvo();

        when(pedidoMapper.toEntity(pedidoDomain)).thenReturn(pedidoEntity);
        when(pedidoRepository.save(pedidoEntity)).thenReturn(pedidoSalvo);
        when(pedidoMapper.toDomain(pedidoSalvo)).thenReturn(pedidoRetorno);

        Pedido resultado = pedidoRepositoryAdapter.save(pedidoDomain);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getClienteId());
        assertEquals(StatusPedido.ABERTO, resultado.getStatus());

        verify(pedidoMapper).toEntity(pedidoDomain);
        verify(pedidoRepository).save(pedidoEntity);
        verify(pedidoMapper).toDomain(pedidoSalvo);
    }

    @Test
    void deveLancarExcecaoQuandoErroAoSalvar() {
        Pedido pedidoDomain = criarPedidoDomain();
        PedidoEntity pedidoEntity = criarPedidoEntity();

        when(pedidoMapper.toEntity(pedidoDomain)).thenReturn(pedidoEntity);
        when(pedidoRepository.save(pedidoEntity)).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThrows(RuntimeException.class, () -> pedidoRepositoryAdapter.save(pedidoDomain));

        verify(pedidoMapper).toEntity(pedidoDomain);
        verify(pedidoRepository).save(pedidoEntity);
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveBuscarPedidoPorIdComSucesso() {
        Long pedidoId = 1L;
        PedidoEntity pedidoEntity = criarPedidoEntitySalvo();
        Pedido pedidoDomain = criarPedidoDomainSalvo();

        when(pedidoRepository.findByIdWithItens(pedidoId)).thenReturn(Optional.of(pedidoEntity));
        when(pedidoMapper.toDomain(pedidoEntity)).thenReturn(pedidoDomain);

        Optional<Pedido> resultado = pedidoRepositoryAdapter.findById(pedidoId);

        assertTrue(resultado.isPresent());
        assertEquals(pedidoId, resultado.get().getId());
        assertEquals(StatusPedido.ABERTO, resultado.get().getStatus());

        verify(pedidoRepository).findByIdWithItens(pedidoId);
        verify(pedidoMapper).toDomain(pedidoEntity);
    }

    @Test
    void deveRetornarVazioQuandoPedidoNaoEncontrado() {
        Long pedidoId = 1L;

        when(pedidoRepository.findByIdWithItens(pedidoId)).thenReturn(Optional.empty());

        Optional<Pedido> resultado = pedidoRepositoryAdapter.findById(pedidoId);

        assertFalse(resultado.isPresent());

        verify(pedidoRepository).findByIdWithItens(pedidoId);
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoErroAoBuscarPorId() {
        Long pedidoId = 1L;

        when(pedidoRepository.findByIdWithItens(pedidoId)).thenThrow(new RuntimeException("Erro ao buscar"));

        assertThrows(RuntimeException.class, () -> pedidoRepositoryAdapter.findById(pedidoId));

        verify(pedidoRepository).findByIdWithItens(pedidoId);
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveListarTodosPedidosComSucesso() {
        List<PedidoEntity> entidades = Arrays.asList(criarPedidoEntitySalvo());
        List<Pedido> dominios = Arrays.asList(criarPedidoDomainSalvo());

        when(pedidoRepository.findAll()).thenReturn(entidades);
        when(pedidoMapper.toDomain(any(PedidoEntity.class))).thenReturn(dominios.get(0));

        List<Pedido> resultado = pedidoRepositoryAdapter.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(StatusPedido.ABERTO, resultado.get(0).getStatus());

        verify(pedidoRepository).findAll();
        verify(pedidoMapper).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoErroAoListarTodos() {
        when(pedidoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));

        assertThrows(RuntimeException.class, () -> pedidoRepositoryAdapter.findAll());

        verify(pedidoRepository).findAll();
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveAtualizarStatusComSucesso() {
        Long pedidoId = 1L;
        String novoStatus = "FECHADO_COM_SUCESSO";
        PedidoEntity entity = criarPedidoEntitySalvo();
        PedidoEntity entityAtualizada = criarPedidoEntityComStatus(StatusPedido.FECHADO_COM_SUCESSO);
        Pedido pedidoAtualizado = criarPedidoDomainComStatus(StatusPedido.FECHADO_COM_SUCESSO);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(entity));
        when(pedidoRepository.save(any(PedidoEntity.class))).thenReturn(entityAtualizada);
        when(pedidoMapper.toDomain(entityAtualizada)).thenReturn(pedidoAtualizado);

        Pedido resultado = pedidoRepositoryAdapter.atualizarStatus(pedidoId, novoStatus);

        assertNotNull(resultado);
        assertEquals(StatusPedido.FECHADO_COM_SUCESSO, resultado.getStatus());
        assertEquals(pedidoId, resultado.getId());

        verify(pedidoRepository).findById(pedidoId);
        verify(pedidoRepository).save(any(PedidoEntity.class));
        verify(pedidoMapper).toDomain(entityAtualizada);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontradoParaAtualizarStatus() {
        Long pedidoId = 1L;
        String novoStatus = "FECHADO_COM_SUCESSO";

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> pedidoRepositoryAdapter.atualizarStatus(pedidoId, novoStatus));

        verify(pedidoRepository).findById(pedidoId);
        verify(pedidoRepository, never()).save(any());
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoStatusInvalido() {
        Long pedidoId = 1L;
        String statusInvalido = "STATUS_INEXISTENTE";
        PedidoEntity entity = criarPedidoEntitySalvo();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class,
                () -> pedidoRepositoryAdapter.atualizarStatus(pedidoId, statusInvalido));

        verify(pedidoRepository).findById(pedidoId);
        verify(pedidoRepository, never()).save(any());
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoErroAoAtualizarStatus() {
        Long pedidoId = 1L;
        String novoStatus = "CANCELADO";
        PedidoEntity entity = criarPedidoEntitySalvo();

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(entity));
        when(pedidoRepository.save(any(PedidoEntity.class))).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThrows(RuntimeException.class,
                () -> pedidoRepositoryAdapter.atualizarStatus(pedidoId, novoStatus));

        verify(pedidoRepository).findById(pedidoId);
        verify(pedidoRepository).save(any(PedidoEntity.class));
        verify(pedidoMapper, never()).toDomain(any(PedidoEntity.class));
    }

    private Pedido criarPedidoDomain() {
        return Pedido.builder()
                .clienteId(1L)
                .status(StatusPedido.ABERTO)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedidoDomain()))
                .build();
    }

    private Pedido criarPedidoDomainSalvo() {
        return Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .status(StatusPedido.ABERTO)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedidoDomain()))
                .build();
    }

    private Pedido criarPedidoDomainComStatus(StatusPedido status) {
        return Pedido.builder()
                .id(1L)
                .clienteId(1L)
                .status(status)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedidoDomain()))
                .build();
    }

    private ItemPedido criarItemPedidoDomain() {
        return ItemPedido.builder()
                .produtoId("PROD001")
                .quantidade(2)
                .precoUnitario(25.50)
                .build();
    }

    private PedidoEntity criarPedidoEntity() {
        return PedidoEntity.builder()
                .clienteId(1L)
                .status(StatusPedido.ABERTO)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedidoEntity()))
                .build();
    }

    private PedidoEntity criarPedidoEntitySalvo() {
        return PedidoEntity.builder()
                .id(1L)
                .clienteId(1L)
                .status(StatusPedido.ABERTO)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedidoEntity()))
                .build();
    }

    private PedidoEntity criarPedidoEntityComStatus(StatusPedido status) {
        return PedidoEntity.builder()
                .id(1L)
                .clienteId(1L)
                .status(status)
                .dataCriacao(LocalDateTime.now())
                .itens(Arrays.asList(criarItemPedidoEntity()))
                .build();
    }

    private ItemPedidoEntity criarItemPedidoEntity() {
        return ItemPedidoEntity.builder()
                .id(1L)
                .produtoId("PROD001")
                .quantidade(2)
                .precoUnitario(25.50)
                .build();
    }
}