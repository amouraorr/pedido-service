package com.fiap.pedido.message;

import com.fiap.pedido.adapter.ServicoExternoMockAdapter;
import com.fiap.pedido.dto.ClienteDTO;
import com.fiap.pedido.dto.ProdutoDTO;
import com.fiap.pedido.dto.StatusPagamentoDTO;
import com.fiap.pedido.dto.request.DadosPagamentoRequestDTO;
import com.fiap.pedido.dto.request.ItemPedidoRequestDTO;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.usecase.service.PedidoUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoConsumerTest {

    @Mock
    private PedidoUseCaseImpl pedidoUseCase;

    @Mock
    private ServicoExternoMockAdapter servicoExternoAdapter;

    @InjectMocks
    private PedidoConsumer pedidoConsumer;

    private PedidoRequestDTO pedidoRequestDTO;
    private PedidoResponseDTO pedidoResponseDTO;
    private ClienteDTO clienteDTO;
    private ProdutoDTO produtoDTO;
    private StatusPagamentoDTO statusPagamentoDTO;

    @BeforeEach
    void setUp() {
        DadosPagamentoRequestDTO dadosPagamento = DadosPagamentoRequestDTO.builder()
                .numeroCartao("1234567890123456")
                .build();

        ItemPedidoRequestDTO item1 = ItemPedidoRequestDTO.builder()
                .produtoId("SKU001")
                .quantidade(2)
                .build();

        ItemPedidoRequestDTO item2 = ItemPedidoRequestDTO.builder()
                .produtoId("SKU002")
                .quantidade(1)
                .build();

        pedidoRequestDTO = PedidoRequestDTO.builder()
                .clienteId(1L)
                .dadosPagamento(dadosPagamento)
                .itens(Arrays.asList(item1, item2))
                .build();

        pedidoResponseDTO = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .status("ABERTO")
                .build();

        clienteDTO = new ClienteDTO();
        clienteDTO.setId("1");
        clienteDTO.setNome("Cliente Teste");

        produtoDTO = new ProdutoDTO();
        produtoDTO.setId("SKU001");
        produtoDTO.setPreco(100.0);

        statusPagamentoDTO = new StatusPagamentoDTO();
        statusPagamentoDTO.setStatus("APROVADO");
        statusPagamentoDTO.setPagamentoId("PAG123");
    }

    @Test
    void deveProcessarPedidoComSucesso() {
        when(pedidoUseCase.criarPedido(any())).thenReturn(pedidoResponseDTO);
        when(servicoExternoAdapter.consultarCliente(anyString())).thenReturn(clienteDTO);
        when(servicoExternoAdapter.consultarProduto(anyString())).thenReturn(produtoDTO);
        when(servicoExternoAdapter.reservarEstoque(anyString(), anyInt())).thenReturn(true);
        when(servicoExternoAdapter.processarPagamento(anyString(), anyDouble())).thenReturn(statusPagamentoDTO);
        when(servicoExternoAdapter.baixarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pedidoUseCase.atualizarStatus(anyLong(), eq("FECHADO_COM_SUCESSO"))).thenReturn(pedidoResponseDTO);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(servicoExternoAdapter).consultarCliente("1");
        verify(servicoExternoAdapter, times(2)).consultarProduto(anyString());
        verify(servicoExternoAdapter, times(2)).reservarEstoque(anyString(), anyInt());
        verify(servicoExternoAdapter).processarPagamento(eq("1234567890123456"), eq(300.0));
        verify(servicoExternoAdapter, times(2)).baixarEstoque(anyString(), anyInt());
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_COM_SUCESSO");
    }

    @Test
    void deveInterromperProcessamentoQuandoDadosPagamentoNulos() {
        pedidoRequestDTO.setDadosPagamento(null);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verifyNoInteractions(pedidoUseCase);
        verifyNoInteractions(servicoExternoAdapter);
    }

    @Test
    void deveAtualizarStatusParaFechadoSemEstoqueQuandoReservaNaoDisponivel() {
        when(pedidoUseCase.criarPedido(any())).thenReturn(pedidoResponseDTO);
        when(servicoExternoAdapter.consultarCliente(anyString())).thenReturn(clienteDTO);
        when(servicoExternoAdapter.consultarProduto(anyString())).thenReturn(produtoDTO);
        when(servicoExternoAdapter.reservarEstoque("SKU001", 2)).thenReturn(true);
        when(servicoExternoAdapter.reservarEstoque("SKU002", 1)).thenReturn(false);
        when(pedidoUseCase.atualizarStatus(anyLong(), eq("FECHADO_SEM_ESTOQUE"))).thenReturn(pedidoResponseDTO);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_ESTOQUE");
        verify(servicoExternoAdapter, never()).processarPagamento(anyString(), anyDouble());
    }

    @Test
    void deveEstornarEstoqueEAtualizarStatusQuandoPagamentoRecusado() {
        StatusPagamentoDTO pagamentoRecusado = new StatusPagamentoDTO();
        pagamentoRecusado.setStatus("RECUSADO");
        pagamentoRecusado.setPagamentoId("PAG456");

        when(pedidoUseCase.criarPedido(any())).thenReturn(pedidoResponseDTO);
        when(servicoExternoAdapter.consultarCliente(anyString())).thenReturn(clienteDTO);
        when(servicoExternoAdapter.consultarProduto(anyString())).thenReturn(produtoDTO);
        when(servicoExternoAdapter.reservarEstoque(anyString(), anyInt())).thenReturn(true);
        when(servicoExternoAdapter.processarPagamento(anyString(), anyDouble())).thenReturn(pagamentoRecusado);
        when(servicoExternoAdapter.estornarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pedidoUseCase.atualizarStatus(anyLong(), eq("FECHADO_SEM_CREDITO"))).thenReturn(pedidoResponseDTO);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(servicoExternoAdapter, times(2)).estornarEstoque(anyString(), anyInt());
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_CREDITO");
        verify(servicoExternoAdapter, never()).baixarEstoque(anyString(), anyInt());
    }

    @Test
    void deveEstornarPagamentoEEstoqueQuandoFalhaNaBaixaDeEstoque() {
        when(pedidoUseCase.criarPedido(any())).thenReturn(pedidoResponseDTO);
        when(servicoExternoAdapter.consultarCliente(anyString())).thenReturn(clienteDTO);
        when(servicoExternoAdapter.consultarProduto(anyString())).thenReturn(produtoDTO);
        when(servicoExternoAdapter.reservarEstoque(anyString(), anyInt())).thenReturn(true);
        when(servicoExternoAdapter.processarPagamento(anyString(), anyDouble())).thenReturn(statusPagamentoDTO);
        when(servicoExternoAdapter.baixarEstoque("SKU001", 2)).thenReturn(true);
        when(servicoExternoAdapter.baixarEstoque("SKU002", 1)).thenReturn(false);
        when(servicoExternoAdapter.estornarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pedidoUseCase.atualizarStatus(anyLong(), eq("FECHADO_SEM_ESTOQUE"))).thenReturn(pedidoResponseDTO);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(servicoExternoAdapter).estornarPagamento("PAG123");
        verify(servicoExternoAdapter, times(2)).estornarEstoque(anyString(), anyInt());
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_ESTOQUE");
    }

    @Test
    void deveCapturarExcecaoGeralDuranteProcessamento() {
        when(pedidoUseCase.criarPedido(any())).thenThrow(new RuntimeException("Erro simulado"));

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verifyNoMoreInteractions(servicoExternoAdapter);
    }

    @Test
    void deveProcessarCorretamenteCalculoValorTotal() {
        ProdutoDTO produto1 = new ProdutoDTO();
        produto1.setId("SKU001");
        produto1.setPreco(50.0);

        ProdutoDTO produto2 = new ProdutoDTO();
        produto2.setId("SKU002");
        produto2.setPreco(75.0);

        when(pedidoUseCase.criarPedido(any())).thenReturn(pedidoResponseDTO);
        when(servicoExternoAdapter.consultarCliente(anyString())).thenReturn(clienteDTO);
        when(servicoExternoAdapter.consultarProduto("SKU001")).thenReturn(produto1);
        when(servicoExternoAdapter.consultarProduto("SKU002")).thenReturn(produto2);
        when(servicoExternoAdapter.reservarEstoque(anyString(), anyInt())).thenReturn(true);
        when(servicoExternoAdapter.processarPagamento(anyString(), anyDouble())).thenReturn(statusPagamentoDTO);
        when(servicoExternoAdapter.baixarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pedidoUseCase.atualizarStatus(anyLong(), eq("FECHADO_COM_SUCESSO"))).thenReturn(pedidoResponseDTO);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(servicoExternoAdapter).processarPagamento(eq("1234567890123456"), eq(175.0));
    }

    @Test
    void deveProcessarPedidoComUmItem() {
        ItemPedidoRequestDTO item = ItemPedidoRequestDTO.builder()
                .produtoId("SKU001")
                .quantidade(1)
                .build();

        pedidoRequestDTO.setItens(List.of(item));

        when(pedidoUseCase.criarPedido(any())).thenReturn(pedidoResponseDTO);
        when(servicoExternoAdapter.consultarCliente(anyString())).thenReturn(clienteDTO);
        when(servicoExternoAdapter.consultarProduto(anyString())).thenReturn(produtoDTO);
        when(servicoExternoAdapter.reservarEstoque(anyString(), anyInt())).thenReturn(true);
        when(servicoExternoAdapter.processarPagamento(anyString(), anyDouble())).thenReturn(statusPagamentoDTO);
        when(servicoExternoAdapter.baixarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pedidoUseCase.atualizarStatus(anyLong(), eq("FECHADO_COM_SUCESSO"))).thenReturn(pedidoResponseDTO);

        pedidoConsumer.consumirPedido(pedidoRequestDTO);

        verify(servicoExternoAdapter, times(1)).consultarProduto("SKU001");
        verify(servicoExternoAdapter, times(1)).reservarEstoque("SKU001", 1);
        verify(servicoExternoAdapter, times(1)).baixarEstoque("SKU001", 1);
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_COM_SUCESSO");
    }
}