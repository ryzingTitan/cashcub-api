# CashCub API

A Spring Boot 4 reactive REST API built with Kotlin for personal budget tracking.

## Overview

CashCub API is a modern, reactive backend service that enables users to manage their personal budgets, track transactions, and analyze spending patterns. The application leverages Spring WebFlux for reactive programming, R2DBC for non-blocking database access, and OAuth2 JWT for secure authentication.

## Tech Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 4 with WebFlux
- **Database**: PostgreSQL with R2DBC (reactive)
- **Authentication**: OAuth2 JWT (Auth0)
- **Testing**: JUnit 5, Mockito Kotlin, Cucumber BDD
- **Code Quality**: ktlint, detekt
- **Build Tool**: Gradle with Kotlin DSL
- **Java Version**: JDK 21

## Architecture

The codebase follows a clean three-layer architecture organized by domain entities:

### Layer Structure

1. **Presentation Layer** (`presentation/`)
   - REST controllers with WebFlux reactive endpoints
   - Security configuration (OAuth2 JWT)
   - OpenAPI/Swagger documentation setup
   - All `/api/*` endpoints require JWT authentication

2. **Domain Layer** (`domain/`)
   - Business logic and orchestration services
   - DTOs for request/response handling
   - Domain-specific exceptions
   - Core business rules

3. **Data Layer** (`data/`)
   - R2DBC reactive repositories
   - Database entity mappings
   - Schema migrations via Liquibase

### Domain Entities

- **Budgets**: Monthly budget periods (month/year combination)
- **Budget Items**: Line items within budgets (e.g., "Groceries $500")
- **Transactions**: Individual income/expense entries linked to budget items
- **Categories**: Predefined categories for organizing items and transactions
- **Analytics**: Aggregated budget data and summaries

## Getting Started

### Prerequisites

- JDK 21
- PostgreSQL database
- Auth0 account for OAuth2 configuration

### Environment Variables

The application requires the following environment variables:

```bash
DB_HOST=localhost        # PostgreSQL host
DB_USER=your_db_user    # Database username
DB_PASSWORD=your_pass   # Database password
```

### Running Locally

```bash
# Build the project
./gradlew build

# Run with local profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Development

### Building and Testing

```bash
# Full build with tests and quality checks
./gradlew build

# Run tests only
./gradlew test

# Run specific test class
./gradlew test --tests "com.ryzingtitan.cashcub.domain.budgets.services.BudgetServiceTests"

# Run tests with coverage report
./gradlew jacocoTestReport

# Verify coverage meets threshold (90%)
./gradlew jacocoTestCoverageVerification
```

### Code Quality

```bash
# Format code with ktlint
./gradlew ktlintFormat

# Check ktlint compliance
./gradlew ktlintCheck

# Run detekt static analysis
./gradlew detekt

# Check for dependency updates
./gradlew dependencyUpdates
```

## Testing

The project uses a comprehensive testing strategy:

- **Unit Tests**: Service and controller tests using JUnit 5 and Mockito Kotlin
- **Integration Tests**: Cucumber BDD tests with Gherkin feature files
- **Test Database**: H2 in-memory database for integration tests
- **Authentication**: mock-oauth2-server for JWT simulation in tests

Feature files are located in `src/test/resources/features/` with corresponding step definitions in `src/test/kotlin/.../cucumber/`.

## Docker

### Building Docker Image

```bash
# Build image using Spring Boot buildpack
./gradlew bootBuildImage --imageName=cashcub-api:latest

# Build and publish to registry
./gradlew bootBuildImage --imageName=<registry>/cashcub-api:<version> --publishImage
```

## API Documentation

When the application is running, OpenAPI/Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

## Authentication

The API uses OAuth2 JWT tokens issued by Auth0:

- **Issuer**: https://dev-7pr07becg7e5y37g.us.auth0.com/
- **Protected Endpoints**: All `/api/*` endpoints require valid JWT
- **Public Endpoints**: Health checks (`/actuator/health`) and API documentation

## Database Migrations

Schema changes are managed via Liquibase. All migrations are defined in:

```
src/main/resources/db/changelog/db.changelog-master.yaml
```

New database changes must be added as changesets in this file.

## CI/CD

The project uses GitHub Actions for continuous integration and deployment:

- **CI Workflow** (`ci.yml`): Runs on every push, executes tests and quality checks
- **Build Workflow** (`build.yml`): Builds and publishes Docker images to GitHub Container Registry

Docker images are tagged with the version from `build.gradle.kts` and pushed to `ghcr.io`.

## Project Structure

```
src/
├── main/
│   ├── kotlin/com/ryzingtitan/cashcub/
│   │   ├── presentation/     # Controllers, security config
│   │   ├── domain/           # Services, DTOs, exceptions
│   │   └── data/             # Repositories, entities
│   └── resources/
│       ├── db/changelog/     # Liquibase migrations
│       ├── application.yml   # Application configuration
│       └── application-*.yml # Profile-specific configs
└── test/
    ├── kotlin/               # Unit and integration tests
    └── resources/features/   # Cucumber BDD feature files
```

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

## Contributing

[Add contributing guidelines here]

## AI Development

For AI coding assistants like Claude Code, see [CLAUDE.md](CLAUDE.md) for architecture details, common commands, and development patterns specific to this codebase.
