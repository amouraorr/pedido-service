CREATE TABLE itens_pedido (
                              id SERIAL PRIMARY KEY,
                              produto_id VARCHAR(255),
                              quantidade INTEGER,
                              preco_unitario NUMERIC(10, 2),
                              pedido_id BIGINT,
                              CONSTRAINT fk_pedido
                                  FOREIGN KEY (pedido_id)
                                      REFERENCES pedidos(id)
                                      ON DELETE CASCADE
);
