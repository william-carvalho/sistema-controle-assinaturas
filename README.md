## Controle de Assinaturas (Spring Boot 3 / Java 21)

API para gerenciar planos e assinaturas com fluxo orientado a eventos e RabbitMQ. Segue práticas de DDD, TDD e clean code, com métricas e documentação via OpenAPI.

### Stack
- Java 21, Spring Boot 3.4 (web, data-jpa, validation, actuator, springdoc)
- RabbitMQ (fila assíncrona `subscription.events`)
- PostgreSQL (produção) / H2 (testes)

### Rodando local
1) Suba dependências:
```bash
docker-compose up -d
```
RabbitMQ UI em http://localhost:15672 (guest/guest).

2) Aplicação:
```bash
./mvnw spring-boot:run
```

Config padrão em `src/main/resources/application.properties`:
- DB: `jdbc:postgresql://localhost:5432/subscriptions` (postgres/postgres)
- Exchange: `subscription.exchange`
- Fila/route: `subscription.events`

Documentação: Swagger UI em `http://localhost:8080/docs` e OpenAPI JSON em `/api/docs`.

### Endpoints principais
- **Criar assinatura** `POST /api/subscriptions`
```json
{
  "plan_id": "uuid-do-plano",
  "customer_email": "cliente@email.com"
}
```
Resposta `201`:
```json
{
  "subscription_id": "uuid",
  "status": "pending",
  "next_billing_date": "2025-02-01",
  "plan_id": "uuid-do-plano",
  "customer_email": "cliente@email.com"
}
```
Publica evento `subscription_created` na fila.

- **Webhook de pagamento** `POST /api/webhooks/payment`
```json
{
  "subscription_id": "uuid",
  "event": "payment_success", // ou payment_failed
  "amount": 29.99,
  "date": "2025-01-01"
}
```
Resposta `202` e evento `payment_success|payment_failed` publicado.

- **Relatório** `GET /api/reports/subscriptions`
```json
{
  "total_active": 120,
  "total_cancelled": 15,
  "plans": [
    { "plan_id": "...", "name": "Basic Plan", "active_subscriptions": 80 },
    { "plan_id": "...", "name": "Premium Plan", "active_subscriptions": 40 }
  ]
}
```

### Fluxo de eventos
- Eventos persistidos em tabela `events` (`payload` JSON, `processed` flag).
- Publicação via `subscription.exchange` -> rota `subscription.events`.
- Consumer (`SubscriptionEventListener`) processa:
  - `subscription_created`: apenas marca como processado (ponto para integrar billing).
  - `payment_success`: ativa assinatura e calcula próxima cobrança pelo ciclo do plano.
  - `payment_failed`: suspende assinatura.

Formato do evento:
```json
{
  "eventId": "uuid",
  "type": "PAYMENT_SUCCESS",
  "subscriptionId": "uuid",
  "planId": "uuid",
  "customerEmail": "cliente@email.com",
  "amount": 29.99,
  "eventDate": "2025-01-01"
}
```

### Estrutura (DDD)
- `domain`: entidades (Plan, Subscription, EventRecord), enums e serviços de domínio.
- `application`: serviços de caso de uso, agregação de métricas e logging de eventos.
- `infrastructure`: controllers REST, mensageria (Rabbit ou mock), configuração e seed de planos.

### Testes
```bash
./mvnw test
```
- Perfil `test` usa H2 e publisher in-memory (sem RabbitMQ).
- Cobrem criação de assinatura, processamento de eventos e relatório.

### Próximos passos sugeridos
- Integrar provedor real de cobrança nos handlers de eventos.
- Adicionar autenticação/tenant e endpoints para CRUD de planos.
- Configurar observabilidade (traces/metrics) e retries na fila.
