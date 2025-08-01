package com.fiap.pedido.usecase.service;

import com.fiap.pedido.dto.request.DadosPagamentoRequestDTO;
import com.fiap.pedido.dto.request.ItemPedidoRequestDTO;
import com.fiap.pedido.dto.request.PagamentoRequestDTO;
import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.dto.response.ItemPedidoResponseDTO;
import com.fiap.pedido.dto.response.PagamentoResponseDTO;
import com.fiap.pedido.dto.response.PedidoResponseDTO;
import com.fiap.pedido.gateway.EstoqueGateway;
import com.fiap.pedido.gateway.PagamentoServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarPedidoUseCaseImplTest {

    @Mock
    private PedidoUseCase pedidoUseCase;

    @Mock
    private EstoqueGateway estoqueGateway;

    @Mock
    private PagamentoServiceClient pagamentoServiceClient;

    @InjectMocks
    private ProcessarPedidoUseCaseImpl processarPedidoUseCase;

    private PedidoRequestDTO pedidoRequestDTO;
    private PedidoResponseDTO pedidoResponseDTO;
    private PagamentoResponseDTO pagamentoResponseDTO;
    private ItemPedidoRequestDTO itemPedidoRequestDTO;

    @BeforeEach
    void setUp() {
        DadosPagamentoRequestDTO dadosPagamento = DadosPagamentoRequestDTO.builder()
                .metodoPagamento("CARTAO_CREDITO")
                .numeroCartao("1234567890123456")
                .build();

        itemPedidoRequestDTO = ItemPedidoRequestDTO.builder()
                .produtoId("PROD001")
                .quantidade(2)
                .build();

        pedidoRequestDTO = PedidoRequestDTO.builder()
                .clienteId(1L)
                .itens(Arrays.asList(itemPedidoRequestDTO))
                .dadosPagamento(dadosPagamento)
                .build();

        ItemPedidoResponseDTO itemResponse = ItemPedidoResponseDTO.builder()
                .produtoId("PROD001")
                .quantidade(2)
                .precoUnitario(100.0)
                .build();

        pedidoResponseDTO = PedidoResponseDTO.builder()
                .id(1L)
                .clienteId(1L)
                .itens(Arrays.asList(itemResponse))
                .status("ABERTO")
                .dataCriacao(LocalDateTime.now())
                .build();

        pagamentoResponseDTO = PagamentoResponseDTO.builder()
                .pagamentoId("PAG123")
                .status("APROVADO")
                .numeroCartao("1234567890123456")
                .build();
    }

    @Test
    void deveProcessarPedidoComSucesso() {
        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenReturn(true);
        when(pagamentoServiceClient.processarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoResponseDTO);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(estoqueGateway).baixarEstoque("PROD001", 2);
        verify(pagamentoServiceClient).processarPagamento(any(PagamentoRequestDTO.class));
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_COM_SUCESSO");
    }

    @Test
    void deveFecharPedidoQuandoEstoqueInsuficiente() {
        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenReturn(false);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(estoqueGateway).baixarEstoque("PROD001", 2);
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_ESTOQUE");
        verifyNoInteractions(pagamentoServiceClient);
    }

    @Test
    void deveFecharPedidoQuandoPagamentoRecusado() {
        PagamentoResponseDTO pagamentoRecusado = PagamentoResponseDTO.builder()
                .pagamentoId("PAG123")
                .status("RECUSADO")
                .numeroCartao("1234567890123456")
                .build();

        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenReturn(true);
        when(pagamentoServiceClient.processarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoRecusado);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(estoqueGateway).baixarEstoque("PROD001", 2);
        verify(pagamentoServiceClient).processarPagamento(any(PagamentoRequestDTO.class));
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_CREDITO");
    }

    @Test
    void deveProcessarPedidoComMultiplosItens() {
        ItemPedidoRequestDTO item2 = ItemPedidoRequestDTO.builder()
                .produtoId("PROD002")
                .quantidade(1)
                .build();

        List<ItemPedidoRequestDTO> itens = Arrays.asList(itemPedidoRequestDTO, item2);
        pedidoRequestDTO.setItens(itens);

        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenReturn(true);
        when(estoqueGateway.baixarEstoque("PROD002", 1)).thenReturn(true);
        when(pagamentoServiceClient.processarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoResponseDTO);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(estoqueGateway).baixarEstoque("PROD001", 2);
        verify(estoqueGateway).baixarEstoque("PROD002", 1);
        verify(pagamentoServiceClient).processarPagamento(any(PagamentoRequestDTO.class));
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_COM_SUCESSO");
    }

    @Test
    void deveFecharPedidoQuandoUmItemSemEstoque() {
        ItemPedidoRequestDTO item2 = ItemPedidoRequestDTO.builder()
                .produtoId("PROD002")
                .quantidade(1)
                .build();

        List<ItemPedidoRequestDTO> itens = Arrays.asList(itemPedidoRequestDTO, item2);
        pedidoRequestDTO.setItens(itens);

        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenReturn(true);
        when(estoqueGateway.baixarEstoque("PROD002", 1)).thenReturn(false);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(estoqueGateway).baixarEstoque("PROD001", 2);
        verify(estoqueGateway).baixarEstoque("PROD002", 1);
        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_ESTOQUE");
        verifyNoInteractions(pagamentoServiceClient);
    }

    @Test
    void deveCalcularValorTotalCorretamente() {
        ItemPedidoRequestDTO item2 = ItemPedidoRequestDTO.builder()
                .produtoId("PROD002")
                .quantidade(3)
                .build();

        List<ItemPedidoRequestDTO> itens = Arrays.asList(itemPedidoRequestDTO, item2);
        pedidoRequestDTO.setItens(itens);

        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pagamentoServiceClient.processarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoResponseDTO);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pagamentoServiceClient).processarPagamento(argThat(pagamento ->
                pagamento.getValor().equals(BigDecimal.valueOf(500.0))
        ));
    }

    @Test
    void deveDefinirStatusErroQuandoExcecaoOcorrer() {
        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenThrow(new RuntimeException("Erro no estoque"));

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(pedidoUseCase).atualizarStatus(1L, "ERRO_PROCESSAMENTO");
    }

    @Test
    void naoDeveAtualizarStatusQuandoPedidoNaoFoiCriado() {
        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenThrow(new RuntimeException("Erro ao criar pedido"));

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).criarPedido(pedidoRequestDTO);
        verify(pedidoUseCase, never()).atualizarStatus(anyLong(), anyString());
    }

    @Test
    void deveProcessarPagamentoComDadosCorretos() {
        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque(anyString(), anyInt())).thenReturn(true);
        when(pagamentoServiceClient.processarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoResponseDTO);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pagamentoServiceClient).processarPagamento(argThat(pagamento ->
                pagamento.getPedidoId().equals(1L) &&
                        pagamento.getMetodoPagamento().equals("CARTAO_CREDITO") &&
                        pagamento.getNumeroCartao().equals("1234567890123456")
        ));
    }

    @Test
    void deveFecharPedidoQuandoPagamentoStatusNulo() {
        PagamentoResponseDTO pagamentoStatusNulo = PagamentoResponseDTO.builder()
                .pagamentoId("PAG123")
                .status(null)
                .numeroCartao("1234567890123456")
                .build();

        when(pedidoUseCase.criarPedido(pedidoRequestDTO)).thenReturn(pedidoResponseDTO);
        when(estoqueGateway.baixarEstoque("PROD001", 2)).thenReturn(true);
        when(pagamentoServiceClient.processarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoStatusNulo);

        processarPedidoUseCase.processarPedido(pedidoRequestDTO);

        verify(pedidoUseCase).atualizarStatus(1L, "FECHADO_SEM_CREDITO");
    }
}