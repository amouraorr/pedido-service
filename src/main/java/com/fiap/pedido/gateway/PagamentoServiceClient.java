package com.fiap.pedido.gateway;

import com.fiap.pedido.dto.request.PagamentoRequestDTO;
import com.fiap.pedido.dto.response.PagamentoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pagamento-service", url = "${pagamento.service.url}")
public interface PagamentoServiceClient {
    @PostMapping("/api/pagamentos")
    PagamentoResponseDTO processarPagamento(@RequestBody PagamentoRequestDTO pagamentoRequestDTO);
}