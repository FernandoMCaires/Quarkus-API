# Railway Java Quarkus API

Esta API foi criada com **Quarkus** para gerenciar clientes, pedidos e produtos em um sistema de e-commerce. A aplicação usa **Hibernate ORM** para persistência de dados com um banco de dados em memória **H2**.

## Tecnologias Utilizadas

- **Quarkus**: Framework Java para microserviços.
- **JAX-RS**: API para criação de serviços RESTful.
- **Hibernate ORM**: Para mapeamento objeto-relacional.
- **H2**: Banco de dados em memória para desenvolvimento.
- **Jakarta Persistence (JPA)**: Para gerenciamento de entidades e interações com o banco de dados.
- **Jackson**: Para serialização e desserialização de objetos JSON.

## Requisitos

- **Java 17** ou superior.
- **Maven** ou **Gradle** para compilação e execução.

## Como Executar o Projeto

### 1. Clonando o Repositório

Clone o repositório para sua máquina local:

```bash
git clone https://github.com/seu-usuario/railway-java-quarkus.git
```

## 2. Instalando as dependencias:
```bash
./mvnw clean install
```

## 3. Execuando a aplicação:
```bash
./mvnw quarkus:dev
```
## 3. ENDPOINTS da API:

Clientes
GET /clientes: Retorna todos os clientes cadastrados.

GET /clientes/{id}: Retorna um cliente específico pelo ID.

POST /clientes: Cria um novo cliente.

PUT /clientes/{id}: Atualiza os dados de um cliente existente pelo ID.

DELETE /clientes/{id}: Deleta um cliente pelo ID.

Produtos
GET /produtos: Retorna todos os produtos cadastrados.

GET /produtos/{id}: Retorna um produto específico pelo ID.

POST /produtos: Cria um novo produto.

PUT /produtos/{id}: Atualiza os dados de um produto existente pelo ID.

DELETE /produtos/{id}: Deleta um produto pelo ID.

Pedidos
GET /pedidos: Retorna todos os pedidos.

GET /pedidos/{id}: Retorna um pedido específico pelo ID.

POST /pedidos: Cria um novo pedido (não inclui produtos no pedido diretamente).

PUT /pedidos/{id}: Atualiza os dados de um pedido existente pelo ID.

DELETE /pedidos/{id}: Deleta um pedido pelo ID.



