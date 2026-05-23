# SettleFlow

> **A Distributed Fintech Commerce & Embedded Finance Platform**  
> Built with Java 21 · Spring Boot 3 · PostgreSQL · Redis · Kafka (future)

SettleFlow is a fintech-grade backend platform simulating real-world digital marketplace infrastructure. It combines multi-vendor commerce, wallet-based payments, BNPL (Buy Now Pay Later), loan & installment management, and merchant settlement workflows — all designed as a modular monolith with a clear path to microservices.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Architecture](#architecture)
- [Domain Design](#domain-design)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Overview](#api-overview)
- [Security](#security)
- [Evolution Roadmap](#evolution-roadmap)
- [Testing Strategy](#testing-strategy)
- [Key Principles](#key-principles)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## Overview

SettleFlow is designed to simulate systems found in modern fintech platforms. The architecture follows a **modular monolith-first** approach — each domain is fully isolated internally, making the system straightforward to extract into independent microservices when scale demands it.

**What it covers:**

- Multi-vendor marketplace with merchant onboarding
- Wallet-based payments with immutable ledger accounting
- Buy Now Pay Later (BNPL) checkout with loan workflows
- Installment plan generation and automated repayment tracking
- Merchant earnings tracking and scheduled payout/settlement cycles
- Role-based access control across Customer, Merchant, and Admin roles
- Event-driven architecture readiness (Kafka integration planned)

---

## Key Features

### Commerce
- Merchant onboarding, store management, and product catalog
- Shopping cart with multi-vendor support
- Order processing and lifecycle management

### Payments
- Direct wallet and payment gateway flows
- Callback verification and idempotent transaction handling
- Payment reconciliation

### Wallet & Ledger
- Internal wallet per user and per merchant
- Immutable ledger-based balance tracking
- Balance derived from the full transaction history — no mutable balance field

### BNPL / Loan
- Buy Now Pay Later checkout flow
- Loan approval workflow with extensible credit scoring
- Multi-bank support with configurable credit limits per bank
- Installment plan generation with interest calculation

### Installments
- Monthly repayment schedules
- Automated due-date tracking
- Overdue detection and penalty rules (extensible)

### Merchant Settlement
- Merchant earnings tracking per order
- Settlement cycle batching
- Scheduled payout processing

### Notifications
- Email and SMS alert support
- Event-driven notification hooks (Kafka-ready)

---

## Architecture

SettleFlow is built as a **modular monolith** — one deployable Spring Boot application with strict domain boundaries. Each module is structured to be extracted into an independent microservice with minimal code changes.

```
┌─────────────────────────────────────────────────────────────┐
│                      settleflow-app                         │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  identity   │  │  merchant   │  │       banking       │ │
│  │  User, Auth │  │ Store,Prod  │  │  Bank, Credit,Loan  │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  customer   │  │    cart     │  │        order        │ │
│  │   Profile   │  │ Cart, Items │  │  Order, Installment  │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   wallet    │  │   payout    │  │    notification     │ │
│  │  Ledger, Tx │  │ Settlement  │  │   Email, SMS, Log   │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  shared — exceptions, ApiResponse, JWT, utils        │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                          │
                 PostgreSQL (one schema
                  per domain, no cross-
                  domain FK constraints)
```

### Module isolation rules

1. **Modules never import each other's repositories** — cross-domain communication goes through service interfaces only.
2. **No cross-domain foreign keys in the database** — domain IDs are stored as plain columns; consistency is enforced at the service layer.
3. **Table names are domain-prefixed** — `identity_users`, `banking_banks`, `order_orders` — so the database is already partitioned for future extraction.
4. **Package by domain, not by layer** — each domain folder contains its own `controller/`, `service/`, `repository/`, `model/`, `dto/`, and `events/`.

---

## Domain Design

### Wallet — Ledger Model

All financial transactions are immutable entries. The current balance is always derived, never stored as a single mutable value.

```
Deposit   +1000.00
Purchase   -400.00
Fee          -5.00
─────────────────
Balance    +595.00   ← sum of all ledger entries
```

### Payment Flow

```
Order Created → Payment Request → Gateway Processing
    → Callback Verification → Wallet Update → Order Completed
```

### BNPL Flow

```
Checkout → Loan Request → Credit Decision
    → Installment Plan Created → Monthly Payments
```

### Merchant Settlement Flow

```
Order Paid → Platform holds funds → Settlement cycle runs → Merchant payout
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Auth | Spring Security + JWT (JJWT 0.12) |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Caching | Redis |
| Messaging | Kafka *(Phase 2)* |
| Containerisation | Docker |
| Orchestration | Kubernetes *(Phase 4)* |
| Observability | Prometheus + Grafana *(Phase 4)* |
| Docs | OpenAPI 3 / Swagger UI |
| Testing | JUnit 5, Mockito, Testcontainers, MockMvc |

---

## Project Structure

```
src/main/java/com/settleflow/
│
├── identity/               # User registration, login, JWT
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   └── enums/
│
├── merchant/               # Store management, product catalog
├── banking/                # Banks, credit limits, loan approval
├── customer/               # Customer profile, credit view
├── cart/                   # Shopping cart, checkout
├── order/                  # Orders, installment generation
├── wallet/                 # Ledger, transactions, balance
├── payout/                 # Merchant settlement, payouts
├── notification/           # Email, SMS, notification log
├── admin/                  # Admin dashboard, reports
│
└── shared/
    ├── response/           # ApiResponse<T>, PageResponse<T>
    ├── exception/          # GlobalExceptionHandler, BusinessException
    ├── security/           # JwtAuthFilter, SecurityUtils
    ├── util/               # MoneyUtils, DateUtils, InstallmentMath
    └── config/             # SecurityConfig, JwtConfig, OpenApiConfig

src/main/resources/
├── application.properties
├── application-dev.properties
└── application-prod.properties
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 15+
- Redis 7+ *(optional for local dev)*

### 1. Clone the repository

```bash
git clone https://github.com/your-username/settleflow.git
cd settleflow
```

### 2. Configure the database

Create a PostgreSQL database:

```sql
CREATE DATABASE settleflow;
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/settleflow
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your-256-bit-secret-key-change-this-in-production
jwt.expiration=86400
jwt.refresh-expiration=604800
```

### 3. Run the application

```bash
mvn spring-boot:run
```

### 4. Access Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### 5. Quick smoke test

```bash
# Register a merchant
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"merchant@test.com","password":"password123","role":"MERCHANT"}'

# Login and get a token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"merchant@test.com","password":"password123"}'
```

---

## API Overview

All responses follow a consistent envelope:

```json
{
  "success": true,
  "message": "Login successful",
  "data": { ... }
}
```

| Domain | Base Path | Auth Required |
|---|---|---|
| Auth | `/api/auth/**` | No |
| Merchant | `/api/merchants/**` | Yes — MERCHANT |
| Products | `/api/products/**` | Yes — MERCHANT / public GET |
| Banking | `/api/banks/**` | Yes — ADMIN |
| Cart | `/api/cart/**` | Yes — CUSTOMER |
| Orders | `/api/orders/**` | Yes — CUSTOMER |
| Installments | `/api/installments/**` | Yes — CUSTOMER |
| Wallet | `/api/wallet/**` | Yes — CUSTOMER / MERCHANT |
| Admin | `/api/admin/**` | Yes — ADMIN |

Full API documentation is available at `/swagger-ui/index.html` when the application is running.

---

## Security

- **JWT authentication** — stateless, token-based. Access token (24h) + refresh token (7d).
- **Role-based access control (RBAC)** enforced at the filter and method level via `@PreAuthorize`.

| Role | Access |
|---|---|
| `CUSTOMER` | Browse products, manage cart, place orders, view installments, manage wallet |
| `MERCHANT` | Manage store, products, view own orders and settlements |
| `ADMIN` | Full access — manage banks, credits, merchants, reports |

Passwords are hashed with BCrypt. The same generic error message is returned for both wrong email and wrong password (security best practice — no enumeration).

---

## Evolution Roadmap

### Phase 1 — Modular Monolith *(current)*
- Single Spring Boot application
- Clear domain separation with domain-prefixed tables
- No cross-domain repository access

### Phase 2 — Event-Driven
- Introduce Kafka or RabbitMQ
- Async processing for payments and notifications
- Domain events replace direct service calls

### Phase 3 — Microservices Extraction
- wallet-service
- payment-service
- loan-service
- payout-service
- notification-service
- Spring Cloud Gateway as API Gateway

### Phase 4 — Production Grade
- Kubernetes deployment
- Prometheus + Grafana observability stack
- Distributed tracing (OpenTelemetry)
- CI/CD pipeline

---

## Testing Strategy

| Type | Tool | What it covers |
|---|---|---|
| Unit tests | JUnit 5 + Mockito | Service logic, calculations |
| Integration tests | Testcontainers | Real PostgreSQL in Docker |
| API tests | MockMvc | Controller layer, request/response shapes |
| Financial logic | JUnit 5 | Installment math, ledger accuracy |

Run tests:

```bash
mvn test
```

---

## Key Principles

- **Domain-Driven Design (DDD)** — each domain owns its model, logic, and data
- **Ledger-based financial modeling** — immutable transactions, derived balances
- **Event-driven architecture readiness** — service boundaries map directly to future event producers/consumers
- **Idempotent payment processing** — duplicate payment requests are safe
- **Strong consistency for financial operations** — `@Transactional` on all money-moving operations
- **No cross-domain FK constraints** — application-enforced consistency enables independent databases in microservices phase

---

## Troubleshooting

**App won't start — database connection refused**
- Verify PostgreSQL is running: `pg_ctl status`
- Check credentials in `application.properties`
- Confirm the database exists: `psql -l | grep settleflow`

**`ddl-auto=update` not creating tables**
- Ensure your `@Entity` classes have `@Table(name = "...")` with the correct domain prefix
- Check Hibernate logs — enable with `spring.jpa.show-sql=true`

**JWT token rejected (401)**
- Confirm the `Authorization` header is `Bearer <token>` (with a space)
- Check `jwt.secret` is at least 256 bits (32 characters)
- Access tokens expire after 24h — use the `/api/auth/refresh` endpoint

**Build failures**
```bash
mvn clean install -U
```

---

## License

This project was developed by **Iman Sadafi Tehrani** as a backend engineering portfolio project.

No commercial use is permitted without explicit written permission from the author.

---

*Built with Java 21 · Spring Boot 3 · PostgreSQL · Designed for microservices evolution*
