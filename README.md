# 📘 SettleFlow

**SettleFlow** — A Distributed Fintech Commerce & Embedded Finance Platform

A modular, fintech-grade backend system designed for multi-vendor commerce, wallet-based payments, BNPL (Buy Now Pay Later), merchant settlements, and extensible financial workflows.

---

## 🚀 Overview

SettleFlow is a modular fintech-commerce platform designed to simulate real-world financial systems used in modern digital marketplaces.

It supports:

- Multi-vendor commerce
- Wallet-based payments with ledger accounting
- Payment gateway integration
- BNPL (Buy Now Pay Later)
- Loan & installment management
- Merchant payout & settlement workflows

The system is designed using a modular monolith-first architecture, with a clear path toward event-driven microservices evolution.

---

## 🧩 Key Features

### 🛒 Commerce System
- Merchant onboarding & management
- Product catalog & inventory
- Shopping cart & order processing
- Multi-vendor support

### 💳 Payment System
- Direct wallet & gateway payments
- Payment callback verification
- Idempotent transaction handling

### 💰 Wallet & Ledger System
- Internal wallet per user/merchant
- Immutable ledger-based transactions
- Balance derived from transaction history

### 🏦 BNPL / Loan System
- Buy Now Pay Later checkout
- Loan approval workflow
- Credit scoring (extensible)
- Installment plan generation

### 📅 Installment System
- Monthly repayment plans
- Automated due tracking
- Penalty rules (extendable)
- Payment reconciliation

### 🏪 Merchant Settlement System
- Merchant earnings tracking
- Scheduled payouts
- Settlement batching system

### 🔔 Notification System
- Email / SMS / in-app notifications
- Event-driven messaging (future Kafka integration)

---

## 🏗️ Architecture
| SettleFlow Core App                        |                     |
| ------------------------------------------ | ------------------- |
| Auth Module                                | Merchant Module     |
| Product Module                             | Order Module        |
| Payment Module                             | Wallet Module       |
| Loan Module                                | Installment Module  |
| Payout Module                              | Notification Module |



---

## 🔄 Evolution Plan

### Phase 1 — Modular Monolith
- Single Spring Boot application
- Clear domain separation

### Phase 2 — Event-Driven System
- Introduce Kafka or RabbitMQ
- Async processing for payments

### Phase 3 — Microservices
- Wallet Service
- Payment Service
- Loan Service
- Payout Service
- Notification Service

### Phase 4 — Production Grade
- API Gateway
- Kubernetes
- Observability stack

---

## ⚙️ Tech Stack

### Backend
- Java 21
- Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA
- Hibernate

### Database
- PostgreSQL

### Caching
- Redis

### Messaging
- Kafka (future)

### Infrastructure
- Docker
- Kubernetes (future)
- Prometheus / Grafana

---

## 💳 Core Domain Design

### Wallet (Ledger-Based)

All transactions are immutable:

- Deposit +100
- Purchase -40
- Fee -2

Balance is derived from:

Balance = Sum(all ledger entries)

---

### Payment Flow
Order Created
->
Payment Request
->
Gateway Processing
->
Callback Verification
->
Wallet Update
->
Order Completed

Order Created
->
Payment Request
->
Gateway Processing
->
Callback Verification
->
Wallet Update
->
Order Completed


---

### BNPL Flow
Checkout
->
Loan Request
->
Credit Decision
->
Installment Plan Created
->
Monthly Payments


---

### Merchant Settlement Flow
Order Paid
->
Platform holds funds
->
Settlement cycle runs
->
Merchant payout


---

Each module contains:
- controller/
- service/
- repository/
- entity/
- dto/
- mapper/
- events/

---

## 🔐 Security

- JWT authentication
- Role-based access control (RBAC)

Roles:
- CUSTOMER
- MERCHANT
- ADMIN

---

## 🧪 Testing Strategy

- Unit Testing (JUnit 5)
- Mocking (Mockito)
- Integration Testing (Testcontainers)
- API Testing (MockMvc)

---

## 📈 Key Principles

- Domain-Driven Design (DDD)
- Ledger-based financial modeling
- Event-driven architecture readiness
- Idempotent payment processing
- Strong consistency for financial operations

---

## 🛣️ Roadmap

- Modular monolith setup
- Payment gateway integration
- Wallet ledger system
- BNPL system
- Merchant payout system
- Kafka event-driven migration
- Microservices extraction
- Kubernetes deployment
- Observability

---

## 🧯 Troubleshooting

### DB issues
- Check PostgreSQL connection
- Verify credentials

### Build issues
```bash
mvn clean install
```

## 👨‍💻 Contributors

Built as a backend engineering portfolio project.

---

## 📄 License

This is a personal project developed by **Iman Sadafi Tehrani** for learning and portfolio purposes.

No commercial use is permitted without explicit permission from the author.

---

If you want, I can next generate:
- Docker Compose (Postgres + Redis + App)
- Full OpenAPI Swagger spec
- Database schema (ERD)
- Clean architecture refactor version (production-grade)

Just tell me 👍