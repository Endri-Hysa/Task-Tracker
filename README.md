# Task Tracker Application

A RESTful task tracking application built with Spring Boot 3.x and Java 21.

## Technologies Used

- **Backend:** Java 21, Spring Boot 3.x
- **Security:** Spring Security (Basic Authentication)
- **Database:** H2 (in-memory), Flyway Migration
- **ORM:** Spring Data JPA / Hibernate
- **Documentation:** Swagger/OpenAPI (SpringDoc)
- **Email:** Spring Mail (Gmail SMTP)
- **Testing:** JUnit 5, Mockito, MockMvc
- **Frontend:** HTML, CSS, JavaScript
- **Build Tool:** Maven

## Features

### Core
- ✅ User management (register, login)
- ✅ Project management (CRUD)
- ✅ Task management (CRUD)
- ✅ Task filtering by status and pagination
- ✅ Tasks due today
- ✅ Tasks assigned to a user

### Advanced
- ✅ Basic Authentication with Spring Security
- ✅ Task search with Spring Data JPA Specifications
- ✅ Task activity logging (audit trail)
- ✅ Email notifications on task assignment
- ✅ Swagger API documentation

## Setup Instructions

### Prerequisites
- Java 21
- Maven 3.x

### Configuration
1. Clone the repository
2. Copy `application.properties.example` to `application.properties`
3. Fill in your Gmail credentials in `application.properties`

### Run
```bash
mvn spring-boot:run
```

### Access
- **Frontend:** http://localhost:8080/index.html
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console

## API Endpoints

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users | Create user |
| GET | /api/users/{id} | Get user by ID |
| GET | /api/users | Get all users |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/projects | Create project |
| GET | /api/projects/{id} | Get project by ID |
| GET | /api/projects | Get all projects |
| PUT | /api/projects/{id} | Update project |
| DELETE | /api/projects/{id} | Delete project |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/projects/{id}/tasks | Create task |
| GET | /api/tasks/{id} | Get task by ID |
| GET | /api/projects/{id}/tasks | Get tasks by project |
| PUT | /api/tasks/{id} | Update task |
| DELETE | /api/tasks/{id} | Delete task |
| GET | /api/tasks/due-today | Tasks due today |
| GET | /api/users/{id}/tasks | Tasks by user |
| GET | /api/tasks/search | Search tasks |
| GET | /api/tasks/{id}/activities | Task activity log |

## Database Schema
users

├── id (PK)

├── username

├── email

├── password (BCrypt)

└── created_at
projects

├── id (PK)

├── name

├── description

├── owner_id (FK → users)

└── created_at
tasks

├── id (PK)

├── title

├── description

├── status (TODO/IN_PROGRESS/COMPLETED)

├── priority (LOW/MEDIUM/HIGH)

├── due_date

├── project_id (FK → projects)

├── assignee_id (FK → users)

└── created_at
task_activities

├── id (PK)

├── task_id (FK → tasks)

├── action

├── old_value

├── new_value

├── performed_by

└── performed_at

## Testing

```bash
mvn test
```

- 33+ tests covering controllers, services and repositories
- Unit tests with Mockito
- Integration tests with MockMvc
- Repository tests with @DataJpaTest

## Default Users (V2 Migration)

| Username | Password | Role |
|----------|----------|------|
| admin | password | USER |
| endri | password | USER |
| iva | password | USER |   

## Assumptions

- H2 in-memory database resets on restart
- Basic Authentication used instead of JWT
- Only project owners can create/edit/delete tasks
- Assignees can only change task status
- Email notifications sent on task assignment