# 🔔 RemindMe — Smart Reminder & Notification System

A backend REST API built with **Spring Boot 3.5** for creating, managing, and tracking reminders. RemindMe follows a clean layered architecture with DTOs, validation, and centralized exception handling — designed to scale into a full notification platform.

---

## 📌 Overview

**RemindMe** is a reminder management system that allows users to store reminders with a title, description, and scheduled time. The application exposes RESTful CRUD endpoints and persists data in **PostgreSQL** using **Spring Data JPA** and **Hibernate**.

> **Current status:** Core reminder CRUD APIs, validation, and global exception handling are fully implemented. Scheduler, notifications, authentication, and deployment features are planned for upcoming releases.

---

## ✨ Features Implemented

- ✅ Spring Boot **3.5.16** project setup with Maven
- ✅ PostgreSQL database integration
- ✅ **Reminder** entity with JPA & Hibernate
- ✅ Repository layer using **Spring Data JPA**
- ✅ **DTO architecture** (`ReminderRequest` & `ReminderResponse`)
- ✅ Service layer with business logic and manual entity mapping
- ✅ REST Controller with full **CRUD** operations
- ✅ **Jakarta Bean Validation** on request payloads
- ✅ **Global exception handling** via `@RestControllerAdvice`
- ✅ Custom **`ResourceNotFoundException`** for missing resources
- ✅ Clean **layered architecture** (Controller → Service → Repository → Entity)
- ✅ Structured JSON error responses (400, 404, 500)

---

## 🛠 Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.16 |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Build Tool | Maven |
| Utilities | Lombok |
| Validation | Jakarta Validation |
| API Style | REST |
| API Testing | Postman |

---

## 📁 Project Structure

```
src/main/java/com/om/remindme/
├── RemindmeApplication.java      # Spring Boot entry point
├── controller/                   # REST API layer
├── service/                      # Business logic layer
├── repository/                   # Data access layer
├── entity/                       # JPA entities
├── dto/                          # Request & Response DTOs
├── exception/                    # Custom exceptions & global handler
├── config/                       # (Planned) App & security configuration
├── scheduler/                    # (Planned) Scheduled reminder jobs
├── notification/                 # (Planned) Email & push notifications
└── util/                         # (Planned) Shared utility classes
```

### Package Overview

| Package | Purpose |
|---------|---------|
| **`controller`** | Exposes REST endpoints, handles HTTP requests/responses, and delegates to the service layer. |
| **`service`** | Contains business logic, entity-to-DTO mapping, and orchestrates repository operations. |
| **`repository`** | Spring Data JPA interfaces for database CRUD operations. |
| **`entity`** | JPA/Hibernate entity classes mapped to database tables. |
| **`dto`** | Data Transfer Objects for API input (`ReminderRequest`) and output (`ReminderResponse`). |
| **`exception`** | Custom exceptions (`ResourceNotFoundException`) and `GlobalExceptionHandler` for consistent error responses. |
| **`config`** | *(Planned)* Application-wide configuration such as Swagger, security, and CORS settings. |
| **`scheduler`** | *(Planned)* Cron-based jobs to trigger reminders at scheduled times. |
| **`notification`** | *(Planned)* Email, SMS, and WebSocket notification delivery services. |
| **`util`** | *(Planned)* Reusable helpers for date/time formatting and common utilities. |

---

## 🌐 API Endpoints

**Base URL:** `http://localhost:8080`

| Method | Endpoint | Description | Success Status |
|--------|----------|-------------|----------------|
| `POST` | `/api/v1/reminders` | Create a new reminder | `201 Created` |
| `GET` | `/api/v1/reminders` | Get all reminders | `200 OK` |
| `GET` | `/api/v1/reminders/{id}` | Get a reminder by ID | `200 OK` |
| `PUT` | `/api/v1/reminders/{id}` | Update a reminder | `200 OK` |
| `DELETE` | `/api/v1/reminders/{id}` | Delete a reminder | `204 No Content` |

### Request Body (Create / Update)

```json
{
  "title": "Team Standup",
  "description": "Daily sync at 10 AM",
  "reminderTime": "2026-06-26T10:00:00"
}
```

### Response Body

```json
{
  "id": 1,
  "title": "Team Standup",
  "description": "Daily sync at 10 AM",
  "reminderTime": "2026-06-26T10:00:00",
  "completed": false
}
```

### Validation Rules

| Field | Rule |
|-------|------|
| `title` | Must not be blank |
| `reminderTime` | Must not be null |
| `description` | Optional |

### Error Responses

**404 — Resource Not Found**
```json
{
  "timestamp": "2026-06-26T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Reminder not found with id: 99"
}
```

**400 — Validation Failed**
```json
{
  "timestamp": "2026-06-26T10:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "title": "must not be blank",
    "reminderTime": "must not be null"
  }
}
```

---

## 🗄 Database Schema

**Table:** `reminders`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | BIGINT | Primary Key, Auto Increment |
| `title` | VARCHAR(100) | Not Null |
| `description` | VARCHAR(500) | Nullable |
| `reminder_time` | TIMESTAMP | Not Null |
| `completed` | BOOLEAN | Not Null, Default `false` |

---

## 🚀 How to Run

### Prerequisites

- **Java 17+**
- **Maven 3.6+** (or use the included Maven Wrapper)
- **PostgreSQL** running locally
- **Postman** (optional, for API testing)

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd remindme
```

### 2. Create the Database

```sql
CREATE DATABASE remindme_db;
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/remindme_db
spring.datasource.username=your_username
spring.datasource.password=your_password
server.port=8080
```

### 4. Run the Application

**Using Maven Wrapper (recommended):**

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**Using Maven:**

```bash
mvn spring-boot:run
```

The application starts at **http://localhost:8080**.

### 5. Test with Postman

| Action | Method | URL |
|--------|--------|-----|
| Create Reminder | `POST` | `http://localhost:8080/api/v1/reminders` |
| Get All | `GET` | `http://localhost:8080/api/v1/reminders` |
| Get By ID | `GET` | `http://localhost:8080/api/v1/reminders/1` |
| Update | `PUT` | `http://localhost:8080/api/v1/reminders/1` |
| Delete | `DELETE` | `http://localhost:8080/api/v1/reminders/1` |

---

## 🗺 Future Roadmap

| Feature | Description | Status |
|---------|-------------|--------|
| ⏰ **Scheduler** | Cron jobs to check and trigger due reminders automatically | 🔜 Planned |
| 📧 **Email Notifications** | Send email alerts when reminders are due | 🔜 Planned |
| 🔐 **JWT Authentication** | Secure APIs with token-based user authentication | 🔜 Planned |
| 📖 **Swagger Documentation** | Interactive OpenAPI docs for all endpoints | 🔜 Planned |
| 🐳 **Docker** | Containerize the application with Docker Compose | 🔜 Planned |
| ☁️ **Deployment** | Deploy to cloud platforms (AWS, Render, Railway, etc.) | 🔜 Planned |
| 🧪 **Unit Testing** | JUnit & Mockito tests for service and controller layers | 🔜 Planned |
| 🔌 **WebSocket Notifications** | Real-time push notifications to connected clients | 🔜 Planned |

---

## 🏗 Architecture

```
Client (Postman / Frontend)
        │
        ▼
┌───────────────────┐
│    Controller     │  ← REST endpoints, @Valid validation
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│     Service       │  ← Business logic, DTO ↔ Entity mapping
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│    Repository     │  ← Spring Data JPA
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│   PostgreSQL DB   │
└───────────────────┘

Exception Flow:
Service throws ResourceNotFoundException
        │
        ▼
GlobalExceptionHandler → Structured JSON error response
```

---

## 👤 Author

**OM Kumar Singh**

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
