# Rejection Hotline

**Job Application Follow-up Management System** — track applications, get reminded when follow-ups are due, and analyze your job search pipeline.

## Monorepo Structure

```
Rejection-Hotline/
├── backend/          # Spring Boot 3.5 REST API
├── frontend/         # React + TypeScript SPA
├── docker-compose.yml
├── .env.example
└── README.md
```

## Features

- JWT authentication (register, login, refresh, profile)
- User-scoped job application CRUD with search, filters, and pagination
- Dashboard KPIs (total, applied today, need follow-up, interviews, rejected, offers, rates)
- Daily scheduler for in-app follow-up reminders (4-day rule)
- In-app notification center
- Mark follow-up sent / reset follow-up eligibility
- Excel (.xlsx) import with preview
- Google Sheets import via OAuth2
- Analytics charts (monthly trend, company breakdown, rates)
- OpenAPI / Swagger documentation
- Docker Compose full-stack deployment
- GitHub Actions CI

## Tech Stack

| Backend | Frontend |
|---------|----------|
| Java 17, Spring Boot 3.5.16 | React 19, TypeScript, Vite |
| Spring Security + JWT | TanStack Query, React Router |
| Spring Data JPA, Flyway | Axios, Zustand, Recharts |
| PostgreSQL | Custom CSS UI |

## Quick Start

### Prerequisites

- Java 17+, Maven
- Node.js 20+
- PostgreSQL 16+

### 1. Database

```sql
CREATE DATABASE rejection_hotline_db;
```

Copy `.env.example` to `.env` and set your credentials.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

API: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

UI: http://localhost:5173

### Docker (full stack)

```bash
docker compose up --build
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register |
| POST | `/api/v1/auth/login` | Login |
| GET | `/api/v1/auth/me` | Current user |
| POST | `/api/v1/applications` | Create application |
| GET | `/api/v1/applications` | List (search/filter/page) |
| GET | `/api/v1/dashboard/summary` | Dashboard KPIs |
| GET | `/api/v1/notifications` | Notifications |
| POST | `/api/v1/import/excel` | Excel import |
| GET | `/api/v1/analytics/applications-by-month` | Analytics |

## Follow-up Business Rule

A reminder is created when **all** are true:

- Email status = `SENT`
- Reply received = `false`
- Applied date + 4 days <= today
- Follow-up sent count = `0`
- Follow-up reminder enabled = `true`

## Author

OM Kumar Singh
