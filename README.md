# Course Management System

A RESTful backend application built with Spring Boot for managing
students, instructors, courses, and enrollments.

The project follows a layered architecture and demonstrates Spring Data
JPA, PostgreSQL, DTOs, MapStruct, validation, pagination, search, soft
deletion, and centralized exception handling.

## Features

-   Manage students, instructors, courses, and enrollments
-   CRUD operations
-   Assign instructors to courses
-   Enroll students in courses
-   Search courses by title
-   Pagination support
-   Soft deletion for courses
-   Request validation
-   Centralized exception handling
-   DTO-based API responses
-   MapStruct entity/DTO mapping
-   UUID-based identifiers
-   PostgreSQL persistence

## Tech Stack

-   Java 21
-   Spring Boot
-   Spring Web
-   Spring Data JPA
-   PostgreSQL
-   Maven
-   Lombok
-   MapStruct
-   Jakarta Validation
-   JUnit and Mockito

## Project Structure

``` text
src/main/java/com/example/course_manag_system/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── exception/
├── mapper/
├── repository/
├── service/
│   └── serviceImpl/
└── CourseManagSystemApplication.java
```

## Prerequisites

-   Java 21
-   Maven
-   PostgreSQL
-   Git

## Database Setup

Create a PostgreSQL database:

``` sql
CREATE DATABASE course_system;
```

Set these environment variables:

``` text
DB_HOST=localhost
DB_NAME=course_system
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

Do not commit your `.env` file or database credentials to GitHub.

## Application Configuration

``` yaml
spring:
  application:
    name: course-management-system

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## Running the Project

Clone the repository:

``` bash
git clone https://github.com/habibaaymannn/course-management-system.git
cd course_manag_system
```

Build the project:

``` bash
mvn clean package
```

Run the application:

``` bash
mvn spring-boot:run
```

The API runs at:

``` text
http://localhost:8080
```

## Main Domain Models

-   **Student** --- A student who can enroll in courses.
-   **Instructor** --- An instructor who can be assigned to courses.
-   **Course** --- A course with a title, description, credits, and
    optional instructor.
-   **Enrollment** --- The relationship between a student and a course.

## Architecture

-   **Controller Layer** --- Handles HTTP requests and responses
-   **Service Layer** --- Contains business logic
-   **Repository Layer** --- Handles database access with Spring Data
    JPA
-   **DTO Layer** --- Separates API data from persistence entities
-   **Mapper Layer** --- Converts entities and DTOs with MapStruct
-   **Exception Layer** --- Provides centralized error handling

## Testing the API

The API can be tested using Postman. Typical operations include creating
and retrieving students and instructors, creating and searching courses,
assigning instructors, enrolling students, updating records,
soft-deleting courses, and testing pagination and validation.

## Build Verification

``` bash
mvn clean compile
```

To create the executable JAR:

``` bash
mvn clean package
```

## Security Note

Add these entries to `.gitignore`:

``` gitignore
.env
target/
.idea/
*.iml
```

If credentials are accidentally pushed to a public repository, change
them immediately.

## Future Improvements

-   Authentication and authorization with Spring Security
-   Swagger/OpenAPI documentation
-   Docker support
-   Automated integration tests
-   CI/CD with GitHub Actions
-   Cloud deployment

## Author

Developed as a Course Management System backend project.
