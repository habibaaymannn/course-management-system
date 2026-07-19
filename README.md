# Course Management System

A Spring Boot course management backend split into two services:

- `admin`: administrator-facing APIs for managing courses, instructors, and reports.
- `public`: student-facing APIs for browsing courses, registering students, and managing enrollments.

Both services use the same PostgreSQL database schema and follow a layered architecture with controllers, DTOs, entities, repositories, services, mappers, and centralized exception handling.

## Features

- Admin course management with create, read, update, instructor assignment, and soft delete
- Admin instructor management with duplicate email validation
- Admin reporting for course, instructor, student, and enrollment totals
- Public course browsing for active, non-deleted courses
- Public student registration with duplicate email validation
- Public enrollment with duplicate enrollment prevention
- Registration-window validation before students can enroll in a course
- Enrollment cancellation by setting status to `CANCELLED`
- Pagination support for list endpoints
- DTO-based API responses
- MapStruct entity/DTO mapping
- UUID-based identifiers
- PostgreSQL persistence
- Docker Compose setup for database and both services
- Mockito unit tests for service-layer business logic

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA
- Spring Boot Validation
- Spring Boot Actuator
- PostgreSQL
- Maven
- Lombok
- MapStruct
- JUnit 5
- Mockito
- Docker Compose

## Project Structure

```text
course_manag_system/
|-- admin/
|   |-- src/main/java/com/example/admin/
|   |   |-- controller/
|   |   |-- dto/
|   |   |-- entity/
|   |   |-- exception/
|   |   |-- mapper/
|   |   |-- repository/
|   |   |-- service/
|   |   `-- serviceImpl/
|   `-- src/test/java/com/example/admin/
|-- public/
|   |-- src/main/java/com/example/publicapi/
|   |   |-- controller/
|   |   |-- dto/
|   |   |-- entity/
|   |   |-- exception/
|   |   |-- mapper/
|   |   |-- repository/
|   |   |-- service/
|   |   `-- serviceImpl/
|   `-- src/test/java/com/example/publicapi/
|-- compose.yaml
`-- README.md
```

## Services

### Admin Service

Default local port: `8081`

Base admin endpoints:

```text
POST   /api/v1/admin/courses
GET    /api/v1/admin/courses
GET    /api/v1/admin/courses/{id}
PUT    /api/v1/admin/courses/{id}
PATCH  /api/v1/admin/courses/{courseId}/instructor/{instructorId}
DELETE /api/v1/admin/courses/{id}

POST   /api/v1/admin/instructors
GET    /api/v1/admin/instructors
GET    /api/v1/admin/instructors/{id}
PUT    /api/v1/admin/instructors/{id}
DELETE /api/v1/admin/instructors/{id}

GET    /api/v1/admin/reports/summary
```

### Public Service

Default local port: `8082`

Base public endpoints:

```text
GET    /api/v1/public/courses
GET    /api/v1/public/courses/{id}

POST   /api/v1/public/students
GET    /api/v1/public/students/{id}

POST   /api/v1/public/enrollments
GET    /api/v1/public/enrollments/student/{studentId}
DELETE /api/v1/public/enrollments/{id}
```

## Prerequisites

- Java 21
- Maven or the included Maven wrappers
- PostgreSQL, or Docker for the included Compose setup
- Git

## Environment Variables

Create a `.env` file at the repository root for Docker Compose, and in each service directory if running services directly from Maven.

```text
DB_HOST=localhost
DB_NAME=course_system
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

The service configuration reads:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Do not commit `.env` files or database credentials.

## Running With Docker Compose

From the repository root:

```bash
docker compose up --build
```

Expected service URLs:

```text
Admin API:  http://localhost:8081
Public API: http://localhost:8082
Database:   localhost:5432
```

## Running Locally With Maven

Start PostgreSQL and set the environment variables first.

Run the admin service:

```bash
cd admin
./mvnw spring-boot:run
```

Run the public service:

```bash
cd public
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
.\mvnw.cmd spring-boot:run
```

## Testing

Run the service-layer unit tests for the admin module:

```powershell
cd admin
.\mvnw.cmd -Dtest="com.example.admin.serviceImplTest.*Test" test
```

Run the service-layer unit tests for the public module:

```powershell
cd public
.\mvnw.cmd -Dtest="com.example.publicapi.serviceImplTest.*Test" test
```

Current service unit test coverage:

```text
Admin service tests:  18 passing
Public service tests: 18 passing
Total:                36 passing
```

Note: full `mvn test` runs also execute the generated Spring context tests. Those tests require a reachable PostgreSQL database with valid credentials.

## Architecture

- Controller layer: handles HTTP requests and responses
- Service layer: contains business rules and transaction boundaries
- Repository layer: handles persistence with Spring Data JPA
- DTO layer: separates API payloads from persistence entities
- Mapper layer: converts between entities and DTOs with MapStruct
- Exception layer: centralizes API error responses

## Main Domain Models

- `Student`: a public user who can enroll in courses
- `Instructor`: a teacher assigned to courses by admins
- `Course`: a course with credits, instructor assignment, soft-delete state, and a registration window
- `Enrollment`: the relationship between a student and a course, with an enrollment status

## Future Improvements

- Authentication and authorization with Spring Security
- Swagger/OpenAPI documentation
- Integration tests with Testcontainers
- CI/CD with GitHub Actions
- Production-ready secrets management

## Author

Developed as a Course Management System backend project.
