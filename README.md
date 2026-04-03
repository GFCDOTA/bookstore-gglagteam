# Bookstore Microservices

Arquitetura de microsserviços para uma livraria online, implementada com **Spring Boot 3.3**, **Java 21**, **PostgreSQL**, **SNS/SQS** (via LocalStack) e **Hexagonal Architecture**.

## Arquitetura

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  book-catalog   │     │  order-service   │     │ payment-service │     │  notification   │
│    service      │     │                  │     │                 │     │    service      │
│   :8081         │     │   :8082          │     │   :8083         │     │   :8084         │
│                 │     │                  │     │                 │     │                 │
│  PostgreSQL     │     │  PostgreSQL      │     │  PostgreSQL     │     │  (stateless)    │
│  catalog_db     │     │  order_db        │     │  payment_db     │     │                 │
└────────┬────────┘     └────────┬─────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                        │                       │
         └───────────────┬───────┴────────────────┬───────┴───────────────────────┘
                         │                        │
                    ┌────┴────┐             ┌─────┴─────┐
                    │   SNS   │────────────▶│    SQS    │
                    │ Topic   │  fan-out    │  Queues   │
                    └─────────┘             └───────────┘
                         LocalStack (:4566)
```

## Microsserviços

| Serviço | Porta | Banco | Responsabilidade |
|---------|-------|-------|------------------|
| book-catalog-service | 8081 | catalog_db | CRUD do catálogo, controle de estoque |
| order-service | 8082 | order_db | Criação e ciclo de vida do pedido |
| payment-service | 8083 | payment_db | Processamento de pagamento (simulado 70/30) |
| notification-service | 8084 | — | Notificações fake via log estruturado |

## Endpoints REST

### book-catalog-service (:8081)
| Método | Path | Descrição |
|--------|------|-----------|
| POST | /api/v1/books | Cadastrar livro |
| GET | /api/v1/books | Listar livros |
| GET | /api/v1/books/{id} | Consultar livro |
| PUT | /api/v1/books/{id}/price | Atualizar preço |
| PUT | /api/v1/books/{id}/stock | Atualizar estoque |

### order-service (:8082)
| Método | Path | Descrição |
|--------|------|-----------|
| POST | /api/v1/orders | Criar pedido |
| GET | /api/v1/orders/{id} | Consultar pedido |
| DELETE | /api/v1/orders/{id} | Cancelar pedido |

### payment-service (:8083)
| Método | Path | Descrição |
|--------|------|-----------|
| GET | /api/v1/payments/{orderId} | Consultar pagamento |

## Fluxo de Eventos

1. **POST /api/v1/orders** → `OrderCreated` (SNS)
2. **book-catalog** consome `OrderCreated` → reserva estoque → `StockReserved` ou `StockReservationFailed`
3. **order-service** consome `StockReserved` → marca `AWAITING_PAYMENT`
4. **payment-service** consome `OrderCreated` → processa pagamento → `PaymentApproved` (70%) ou `PaymentRejected` (30%)
5. **order-service** consome `PaymentApproved` → `OrderConfirmed` | `PaymentRejected` → `OrderCancelled`
6. **notification-service** consome `OrderConfirmed`, `OrderCancelled`, `PaymentApproved`, `PaymentRejected` → log simulando e-mail

## Pré-requisitos

- Docker e Docker Compose
- Java 21 (para desenvolvimento local)
- Maven 3.6+

## Como executar

```bash
# Subir toda a infraestrutura + serviços
docker compose up --build

# Ou apenas a infraestrutura (para rodar os serviços na IDE)
docker compose up localstack postgres-catalog postgres-order postgres-payment
```

## Exemplos de uso

### Criar um livro
```bash
curl -X POST http://localhost:8081/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Architecture",
    "author": "Robert C. Martin",
    "isbn": "9780134494166",
    "price": 89.90,
    "initialStock": 50
  }'
```

### Criar um pedido
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "bookId": "<BOOK_ID>",
        "quantity": 2,
        "unitPrice": 89.90
      }
    ]
  }'
```

### Consultar pedido
```bash
curl http://localhost:8082/api/v1/orders/<ORDER_ID>
```

### Consultar pagamento
```bash
curl http://localhost:8083/api/v1/payments/<ORDER_ID>
```

## Padrões e Práticas

- **Hexagonal Architecture** (Ports & Adapters)
- **DDD**: Aggregates, Value Objects, Domain Events
- **SOLID**: Interfaces segregadas, inversão de dependência
- **12-Factor App**: Configuração via ambiente/SSM
- **Idempotência**: Tabela `processed_messages` em cada consumidor
- **DLQ**: Dead Letter Queues com `maxReceiveCount: 3`
- **Versionamento de Eventos**: Campo `eventVersion` em todos os eventos

## Estrutura de Pacotes (por serviço)

```
com.bookstore.<service>/
├── domain/
│   ├── model/          # Aggregates, Entities, Enums
│   ├── vo/             # Value Objects (Money, BookId, ISBN)
│   ├── event/          # Domain Events
│   ├── port/
│   │   ├── in/         # Use Cases (interfaces)
│   │   └── out/        # Repository, EventPublisher (interfaces)
│   └── service/        # Domain Services
├── application/
│   └── usecase/        # Use Case implementations
├── infrastructure/
│   ├── persistence/    # JPA Entities, Repositories, Adapters
│   ├── messaging/      # SNS Publisher, SQS Consumer
│   └── config/         # AWS Config, ObjectMapper
└── interfaces/
    ├── rest/           # Controllers, DTOs
    └── exception/      # GlobalExceptionHandler
```

## Roadmap

### v2 — Melhorias Arquiteturais
- Outbox Pattern completo
- Resilience4j (circuit breaker, retry)
- Flyway para migrations
- Testcontainers (testes de integração)
- OpenTelemetry + Jaeger
- Spring Actuator + Micrometer

### v3 — Produção
- Deploy AWS (ECS/EKS)
- RDS + Secrets Manager reais
- CI/CD com GitHub Actions
- IaC com Terraform/CDK
