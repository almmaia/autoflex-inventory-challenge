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
```bash
docker compose up --build
```

URLs:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/q/swagger-ui`

## Local Run

Backend:
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

## Tests
Backend unit/integration tests:
```bash
cd backend
mvn test
```

Test profile uses in-memory H2 with Flyway migrations (no local PostgreSQL required).
