CREATE TABLE pedidos (
                         id SERIAL PRIMARY KEY,
                         cliente_id BIGINT,
                         status VARCHAR(50),
                         data_criacao TIMESTAMP
);
