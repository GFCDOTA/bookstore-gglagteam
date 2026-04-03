# Testing Bookstore Microservices

## Prerequisites
- Java 21 (OpenJDK)
- Maven 3.9+
- Docker & Docker Compose

## Unit Tests

Run tests per service with `mvn clean test` (always use `clean` to avoid stale compilation cache issues with Lombok):

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# All services
(cd book-catalog-service && mvn clean test)
(cd order-service && mvn clean test)
(cd payment-service && mvn clean test)
(cd notification-service && mvn clean test)
```

**Important:** Do NOT skip `clean` — Lombok annotation processing can produce stale `.class` files that cause `Unresolved compilation problem` errors at runtime even though `test-compile` succeeds.

### Test Structure
- Domain model tests: Pure unit tests, no mocks, no Spring context
- Use case tests: Mockito mocks for repositories and event publishers
- Notification consumer test: Uses `GenericMessage<String>` to simulate SQS messages
- All tests use JUnit 5 `@Nested` / `@DisplayName` and AAA pattern

### Known Quirks
- Payment service uses `Math.random()` for 70/30 approve/reject. Tests assert `APPROVED || REJECTED` which always passes. To make deterministic, inject a `PaymentGateway` strategy.
- `MoneyTest` is duplicated across catalog and order services — intentional per bounded-context isolation.

## End-to-End Testing (Docker Compose)

### Setup
```bash
cp .env.example .env
docker compose up --build -d
# Wait ~30s for Spring Boot services to initialize
```

### Verify Services Are Ready
```bash
curl -s http://localhost:8081/api/v1/books  # Should return 200
curl -s http://localhost:8082/api/v1/orders/00000000-0000-0000-0000-000000000000  # Should return 404
```

### Test Flow: Create Book → Order → Event Chain

1. **Create a book:**
```bash
curl -s -X POST http://localhost:8081/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book","author":"Author","isbn":"978-0321125217","price":89.90,"initialStock":10}'
# Expect: HTTP 201, {"id":"<uuid>","title":"Test Book","status":"ACTIVE"}
```

2. **Create an order** (use bookId from step 1):
```bash
curl -s -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"<any-uuid>","items":[{"bookId":"<bookId>","quantity":2,"unitPrice":89.90}]}'
# Expect: HTTP 201, {"orderId":"<uuid>","status":"AWAITING_STOCK"}
```

3. **Wait ~10 seconds** for event chain:
   - `OrderCreated` → catalog reserves stock → `StockReserved`
   - `OrderCreated` → payment processes → `PaymentApproved` or `PaymentRejected`
   - `StockReserved` → order → `AWAITING_PAYMENT`
   - `PaymentApproved` → order → `CONFIRMED` (or `PaymentRejected` → `CANCELLED`)

4. **Verify results:**
```bash
# Order should be CONFIRMED or CANCELLED
curl -s http://localhost:8082/api/v1/orders/<orderId>

# Stock should be reduced (10 → 8 for quantity=2)
curl -s http://localhost:8081/api/v1/books/<bookId>

# Payment should exist
curl -s http://localhost:8083/api/v1/payments/<orderId>

# Notifications should be logged
docker compose logs notification-service | grep "EMAIL NOTIFICATION"
```

### Service Ports
- Catalog: `localhost:8081`
- Order: `localhost:8082`
- Payment: `localhost:8083`
- Notification: `localhost:8084`
- LocalStack: `localhost:4566`
- PostgreSQL: `localhost:5432` (catalog), `5433` (order), `5434` (payment)

### Cleanup
```bash
docker compose down -v
```

### Troubleshooting
- If services fail to start, check `docker compose logs <service-name>` for connection errors to LocalStack or PostgreSQL.
- LocalStack health: `curl http://localhost:4566/_localstack/health`
- If events aren't flowing, check SQS subscriptions: `aws --endpoint-url=http://localhost:4566 sns list-subscriptions`
- The `.env` file must exist (copy from `.env.example`) — Docker Compose variables like `CATALOG_DB_PASSWORD` have no defaults.
