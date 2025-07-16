# pedido-service

# Serviço de Pedido - Backend

## Introdução

Este microsserviço faz parte de um sistema modular de gerenciamento de pedidos, responsável pelo processamento completo 
dos pedidos realizados pelos clientes. Ele gerencia a criação, atualização, 
consulta e controle do status dos pedidos, integrando-se com os microsserviços de cliente, produto, estoque e pagamento para garantir 
a consistência e autonomia do sistema.

## Objetivo do Projeto

O objetivo principal deste microsserviço é fornecer uma API robusta para gerenciar pedidos, incluindo a validação de estoque, 
processamento de pagamento e atualização do status do pedido conforme regras de negócio definidas. 
O serviço é autônomo, persistindo seus dados isoladamente e comunicando-se com outros microsserviços via Kafka e REST,
seguindo os princípios da arquitetura de microsserviços.

## Requisitos do Sistema

Para executar este microsserviço, você precisará dos seguintes requisitos:

- **Sistema Operacional**: Windows, macOS ou Linux
- **Memória RAM**: Pelo menos 4 GB recomendados
- **Espaço em Disco**: Pelo menos 500 MB de espaço livre
- **Software**:
    - Docker e Docker Compose
    - Java JDK 11 ou superior
    - Maven 3.6 ou superior
    - PostgreSQL
    - Kafka
    - Git

## Estrutura do Projeto

A estrutura do projeto está organizada da seguinte forma:
```plaintext
pedido-service/
│
├── src/
│ └── main/
│   ├── java/
│   │ └── com.fiap.pedido
│   │   ├── adapter/ : Adaptadores para integração com outros microsserviços e persistência.
│   │   ├── config/ : Configurações de segurança, Kafka e Swagger.
│   │   ├── controller/ : Controladores REST para endpoints de pedido.
│   │   ├── domain/ : Entidades de domínio do pedido.
│   │   ├── dto/ : Objetos de transferência de dados (DTOs) para requisições e respostas.
│   │   ├── entity/ : Entidades JPA para persistência.
│   │   ├── enuns/ : Enumerações, como status do pedido.
│   │   ├── exception/ : Tratamento global de exceções.
│   │   ├── mapper/ : Mapeamento entre entidades, domínios e DTOs.
│   │   ├── message/ : Consumidores Kafka para processamento assíncrono.
│   │   ├── pots/ : Portas para abstração de repositórios.
│   │   ├── repository/ : Repositórios JPA.
│   │   ├── usecase/ : Serviços de caso de uso para regras de negócio.
│   │   └── PedidoServiceApplication.java : Classe principal da aplicação.
│   └── resources/
│       └── application.properties : Configurações da aplicação.
├── pom.xml : Arquivo de configuração do Maven.
├── Dockerfile : Arquivo para construção da imagem Docker.
├── docker-compose.yml : Arquivo para orquestração de contêineres.
└── README.md : Documentação do projeto.
```

## Segurança

A segurança do microsserviço é configurada com Spring Security.

## Visão Geral do Projeto

Este microsserviço é desenvolvido com Spring Boot e segue uma arquitetura limpa simplificada, 
separando claramente as responsabilidades entre domínio, persistência, casos de uso e interface. 
A comunicação com outros microsserviços ocorre via Kafka e adaptadores REST mock para simulação.

## Arquitetura

A arquitetura segue o padrão MVC e princípios da Arquitetura Limpa, com camadas bem definidas:

- **Domain**: Representa as entidades de negócio (Pedido, ItemPedido).
- **UseCase**: Serviços que implementam regras de negócio e casos de uso.
- **Gateway / Adapter**: Interfaces e implementações para acesso a dados e integração com outros microsserviços.
- **Controller**: Exposição dos endpoints REST para interação externa.
- **Mapper**: Conversão entre entidades JPA, domínios e DTOs.
- **Message**: Consumidores Kafka para processamento assíncrono de pedidos.

## Princípios de Design e Padrões de Projeto

### Princípios de Design

1. **Single Responsibility Principle (SRP)**:
    - Cada classe tem uma única responsabilidade, como o serviço de criação de pedido ou o consumidor Kafka.

2. **Open/Closed Principle (OCP)**:
    - As classes são abertas para extensão e fechadas para modificação, facilitando manutenção e evolução.

### Padrões de Projeto

1. **MVC (Model-View-Controller)**:
    - Controladores REST atuam como controladores.
    - Domínio representa o modelo.
    - Respostas JSON são a "view".

2. **Gateway / Adapter Pattern**:
    - Abstrai o acesso a dados e integrações externas, facilitando troca da implementação sem impactar o domínio.

3. **Mapper Pattern**:
    - Facilita a conversão entre diferentes camadas do sistema.

4. **Event-Driven Architecture**:
    - Uso de Kafka para comunicação assíncrona entre microsserviços.

## Interação entre as Partes do Sistema

1. **Cliente**: Envia requisições HTTP para criar pedidos.
2. **Controller**: Recebe as requisições e delega para os casos de uso.
3. **UseCase**: Executa regras de negócio, validações e chama o gateway para persistência.
4. **Adapter**: Integra com microsserviços externos (cliente, produto, estoque, pagamento) via mocks REST.
5. **Message**: Consome eventos Kafka para processar pedidos assincronamente.
6. **Banco de Dados**: Armazena os dados dos pedidos.

## Tecnologias Utilizadas

- **Spring Boot**: Framework para desenvolvimento Java.
- **Spring Security**: Segurança da aplicação.
- **Spring Data JPA**: Persistência com banco relacional.
- **PostgreSQL**: Banco de dados relacional.
- **Kafka**: Mensageria para comunicação assíncrona.
- **MapStruct**: Mapeamento entre objetos.
- **Lombok**: Redução de boilerplate.
- **Swagger (Springdoc OpenAPI)**: Documentação da API.
- **Docker e Docker Compose**: Containerização e orquestração.

## Pré-requisitos

Antes de executar o microsserviço, certifique-se de ter instalado:

- Docker e Docker Compose
- Java JDK 11 ou superior
- Maven 3.6 ou superior
- PostgreSQL rodando localmente ou via container
- Kafka rodando localmente ou via container

## Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```
2. Navegue até o diretório do microsserviço pedido-service:
   ```bash
   cd pedido-service
   ```
3. Compile e empacote o projeto com Maven:
   ```bash
   mvn clean package -DskipTests
   ```
4. Configure o banco de dados PostgreSQL conforme `application.properties`.
5. Execute a aplicação localmente:
   ```bash
   mvn spring-boot:run
   ```
6. Ou utilize Docker Compose para subir o serviço, banco e Kafka:
   ```bash
   docker compose up
   ```

## Endpoints Principais

- `POST /pedidos` - Criar novo pedido
- `GET /pedidos/{id}` - Consultar pedido por ID
- `GET /pedidos` - Listar todos os pedidos
- `PATCH /pedidos/{id}/status?status=STATUS` - Atualizar status do pedido

## Fluxo de Processamento do Pedido

1. Pedido criado com status **ABERTO**.
2. Consulta cliente e produtos via adaptadores mock.
3. Reserva estoque para os produtos solicitados.
4. Processa pagamento via adaptador mock.
5. Se pagamento aprovado, baixa estoque e atualiza status para **FECHADO_COM_SUCESSO**.
6. Se falta de estoque, estorna pagamento e atualiza status para **FECHADO_SEM_ESTOQUE**.
7. Se pagamento recusado, estorna estoque e atualiza status para **FECHADO_SEM_CREDITO**.

## Contribuição

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nome-da-feature`).
3. Faça commit das suas alterações (`git commit -m 'Descrição da feature'`).
4. Envie para o repositório remoto (`git push origin feature/nome-da-feature`).
5. Abra um Pull Request.

## Licença

Este projeto é privado ou não possui licença específica.

## Referências e Recursos

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Swagger OpenAPI Documentation](https://springdoc.org/)

## Conclusão

Este microsserviço de pedido exemplifica a aplicação de arquitetura limpa e boas práticas no desenvolvimento de microsserviços, 
garantindo modularidade, escalabilidade e facilidade de manutenção dentro do sistema de gerenciamento de pedidos.
