package com.fiap.pedido.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "estoque-service", url = "${estoque.service.url}")
public interface EstoqueServiceClient {

    @GetMapping("/estoque")
    String consultaEstoque();

    @PostMapping("/estoques/{sku}/baixa")
    void baixarEstoque(@PathVariable("sku") String sku, @RequestParam("quantidade") Integer quantidade);
}