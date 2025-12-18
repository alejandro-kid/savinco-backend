# Financial Data Management API

RESTful API for managing financial data across multiple countries with automatic currency conversion to USD. Built following **Domain-Driven Design** and **Hexagonal Architecture** principles.

## Overview

The system manages financial data (capital saved, capital loaned, profits generated) for multiple countries, maintaining original currency values while providing consolidated summaries in USD. It supports currency management with automatic base currency assignment and exchange rate tracking.

## Architecture

The project follows a **layered architecture** with clear separation of concerns:

- **Domain Layer**: Pure business logic with Value Objects and domain entities
- **Application Layer**: Use cases and business orchestration
- **Infrastructure Layer**: JPA persistence and external integrations
- **Presentation Layer**: REST controllers and DTOs

Key design patterns:

- Repository Pattern for data access abstraction
- Value Objects for domain encapsulation
- Factory methods for entity creation
- DTOs for API contracts

## Tech Stack

- **Java 17** - LTS version
- **Spring Boot 3.2** - Web, Data JPA, Validation, Actuator
- **PostgreSQL** - Primary database (H2 for testing)
- **Flyway** - Database migrations
- **Cucumber** - BDD testing
- **TestContainers** - Integration testing
- **SpringDoc OpenAPI** - API documentation

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (optional, H2 used by default)

### Run Application

```bash
# Clone and navigate
git clone <repository-url>
cd backend

# Run with H2 (in-memory database)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Run with PostgreSQL
mvn spring-boot:run
```

API available at `http://localhost:8080`

### Database Setup

Migrations are executed manually:

```bash
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/savinco_financial \
  -Dflyway.user=postgres \
  -Dflyway.password=your_password
```

## API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

## Testing

```bash
# Run all tests
mvn test

# Run BDD tests
mvn test -Dtest=CucumberTest
```

## Features

- Multi-country financial data management
- Automatic currency conversion to USD
- Consolidated financial summaries
- Currency and country management
- Request ID tracking for traceability
- Structured logging
- Comprehensive BDD test coverage

## Configuration

Environment variables:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/savinco_financial
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=DEBUG
```

---

**Technical Assessment Project** - Demonstrating clean architecture and domain-driven design practices.
