package com.fiap.pedido.message;

import com.fiap.pedido.dto.request.PedidoRequestDTO;
import com.fiap.pedido.usecase.service.PedidoUseCaseImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka para processar pedidos recebidos do tópico 'novo-pedido'.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoConsumer {

    private final PedidoUseCaseImpl pedidoUseCase;

    @KafkaListener(topics = "novo-pedido", groupId = "pedido-service", containerFactory = "pedidoKafkaListenerContainerFactory")
    public void consumirPedido(PedidoRequestDTO pedidoRequestDTO) {
        log.info("Recebido pedido do Kafka: {}", pedidoRequestDTO);
        //Chamar o método de processamento do pedido
        pedidoUseCase.criarPedido(pedidoRequestDTO);

        // TODO: Após criar, iniciar o fluxo de chamadas REST para os outros microsserviços:
        // - Produto Service
        // - Cliente Service
        // - Estoque Service
        // - Pagamento Service
        // Utilize RestTemplate/WebClient ou FeignClient para essas integrações.
    }
}
