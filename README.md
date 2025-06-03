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

Visualizar no Swagger: http://localhost:8080/q/swagger-ui/

### Descrição das Novidades na Versão **v2** da API
A versão **v2** da API introduziu diversas melhorias importantes em relação à segurança, controle de acesso, idempotência e novas funcionalidades nos endpoints de pedidos e clientes. Abaixo está a explicação detalhada sobre essas adições:
### 1. **Configuração de CORS (Cross-Origin Resource Sharing)**
A nova versão da API suporta **CORS**, permitindo solicitações a partir de origens diferentes, como frontends hospedados em diferentes domínios/portas.
- Agora, o CORS está configurado para habilitar origens específicas, como `http://localhost:8000`.
- Permite os seguintes métodos HTTP: `GET`, `POST`, `PUT`, `DELETE`, e os **headers** esperados `Content-Type`, `Authorization`, `Idempotency-Key`.
- Exposição de **headers customizados**, como `X-Custom-Header`.

**Por que é importante?**
- Garante que aplicações externas tenham autorização para se comunicar com a API, respeitando modelos de segurança.
- Facilita o desenvolvimento em ambientes onde o cliente (frontend) e o servidor (backend) rodam em domínios diferentes.

**Exemplo de Configuração no :`CorsFilter`**
``` java
if (origin != null && origin.equals("http://localhost:8000")) {
    responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
    responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
    responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization,Idempotency-Key");
    responseContext.getHeaders().add("Access-Control-Expose-Headers", "X-Custom-Header");
    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
}
```
### 2. **Rate Limiting (Limitação de Requisições por Cliente)**
Foi implementado controle de taxa de requisições por cliente (rate limiting) com base no endereço IP para evitar abuso da API.
- Para cada endereço IP, é permitido um máximo de **2 requisições por minuto**. Se ultrapassado, a resposta retorna com o **status 429 (Too Many Requests)**.

**Como funciona?**
- A lógica está controlada por uma estrutura `Map<String, ClientRequestInfo>`, vinculando cada cliente (IP) ao número de requisições feitas.
- Quando o limite é excedido, a API bloqueia temporariamente novas requisições desse cliente por 1 minuto.

**Por que é importante?**
- Garante que a API seja protegida contra abusos, como ataques de força bruta ou sobrecarga de requisições.
- Melhora a estabilidade e performance do sistema.

**Exemplo de Mensagem de Resposta:**
``` json
{
    "message": "Limite de requisições excedido. Tente novamente mais tarde."
}
```
### 3. **Idempotency Key**
A versão **v2** implementa suporte ao cabeçalho **`Idempotency-Key`** para operações POST. Isso ajuda a evitar processamentos duplicados em endpoints que criam entidades, como o registro de Clientes ou Pedidos.
- O cabeçalho `Idempotency-Key` é obrigatório para todas as requisições POST.
- O sistema verifica se a chave **`Idempotency-Key`** já foi utilizada.
- Se a chave já foi consumida, a API retorna o registro associado à chave, ao invés de criar duplicados.

**Por que é importante?**
- Resolve o problema de requisições duplicadas causadas por falhas no cliente ou redes instáveis.
- Melhora a experiência do usuário, confirmando a criação do registro sem gerar redundância no banco.

**Exemplo de Resposta para ID Já Criado:** Se o cliente envia um **Idempotency-Key** já usado:
``` json
{
    "message": "Cliente já existe com a idempotencyKey informada"
}
```
**Exemplo de Validação no Código:**
``` java
if (idempotencyKey == null || idempotencyKey.isEmpty()) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity("Idempotency-Key é obrigatório").build();
}

// Verificar se existe um cliente com a mesma chave
Cliente clienteExistente = clienteRepository.find("idempotencyKey", idempotencyKey).firstResult();
if (clienteExistente != null) {
    return Response.status(Response.Status.OK)
        .entity("Cliente já existe com a idempotencyKey informada").build();
}
```
### 4. **Campo Quantidade em Produtos**
A nova versão da API também introduziu suporte ao controle de **quantidade** nos produtos de um pedido. Com isso, agora é possível:
- Especificar a quantidade de cada produto no momento de criar ou atualizar pedidos.
- Calcular o **valor total do pedido** com base nos preços individuais dos produtos ajustados pela quantidade.

**Por que é importante?**
- Garante maior flexibilidade no uso da API de Pedidos.
- Melhora os recursos de cálculo no sistema, permitindo a manipulação de múltiplos produtos em diferentes quantidades.

**Exemplo de Requisição com Quantidade:**
``` json
{
    "descricao": "Pedido com quantidades",
    "cliente": {
        "id": 1
    },
    "produtos": [
        {
            "id": 1,
            "quantidade": 2
        },
        {
            "id": 2,
            "quantidade": 3
        }
    ]
}
```
### 5. **Melhoria nos Endpoints**
A versão **v2** trouxe aprimoramentos importantes nos principais endpoints:
- **GET `/api/v2/pedidos`**: Agora exibe os detalhes dos produtos junto de suas quantidades.
- **POST `/api/v2/pedidos`**: Calcula automaticamente o valor total com base nas quantidades.
- **PUT `/api/v2/pedidos/{id}`**: Suporte para atualizar quantidades e recalcular valores.

### Exemplo de Fluxo na V2
1. **Criar Pedido (POST):** Requisição:
``` json
   {
       "descricao": "Novo pedido",
       "cliente": {
           "id": 1
       },
       "produtos": [
           { "id": 1, "quantidade": 2 },
           { "id": 2, "quantidade": 5 }
       ]
   }
```
Resposta:
``` json
   {
       "id": 1,
       "descricao": "Novo pedido",
       "valor": 60.0,
       "produtos": [
           { "id": 1, "nome": "Sabao", "preco": 10.0, "quantidade": 2 },
           { "id": 2, "nome": "Banana", "preco": 5.0, "quantidade": 5 }
       ],
       "cliente": { "id": 1, "nome": "João" }
   }
```
1. **Requerer ID Duplicado (POST):** Requisição com **mesmo `Idempotency-Key`**
``` json
   {
       "descricao": "Novo pedido",
       "cliente": {
           "id": 1
       },
       "produtos": [
           { "id": 1, "quantidade": 2 },
           { "id": 2, "quantidade": 5 }
       ],
       "Idempotency-Key": "abc123"
   }
```
Resposta:
``` json
   {
       "message": "Cliente já existe com a idempotencyKey informada"
   }
```
1. **Limite Excedido (Rate Limiting):** Após várias requisições:
``` json
   {
       "message": "Limite de requisições excedido. Tente novamente mais tarde."
   }
```

