# Autoflex Inventory

Web system for inventory management and production planning:
- Product CRUD
- Raw material CRUD
- Product/raw material association (BOM) CRUD
- Production suggestion prioritized by highest product value and limited by current stock

## Stack
- Backend: Quarkus (Java 21), REST API, Panache, Flyway
- Frontend: React + TypeScript + Vite
- Database: PostgreSQL
- Infrastructure: Docker Compose

## Run with Docker
1. Copy `.env.example` to `.env` and adjust the local-only values.
2. Run:

```bash
docker compose up --build
```

URLs:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/q/swagger-ui`

## Local Run

Backend:
Configure your local PostgreSQL connection before starting Quarkus:
- `QUARKUS_DATASOURCE_JDBC_URL`
- `QUARKUS_DATASOURCE_USERNAME`
- `QUARKUS_DATASOURCE_PASSWORD`

```bash
cd backend
mvn quarkus:dev
```

Frontend:
```bash
cd frontend
npm install
npm run dev
```

Notes:
- `.env` is for local use only and must not be committed.
- Docker Compose reads `POSTGRES_USER` and `POSTGRES_PASSWORD` from `.env`.

## Tests
Backend unit/integration tests:
```bash
cd backend
mvn test
```

Test profile uses in-memory H2 with Flyway migrations (no local PostgreSQL required).

Frontend unit tests:
```bash
cd frontend
npm install
npm run test
```
