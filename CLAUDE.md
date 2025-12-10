# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CashCub API is a Spring Boot 4 reactive REST API built with Kotlin for personal budget tracking. It uses WebFlux for reactive programming, R2DBC for non-blocking database access, and OAuth2 JWT for authentication.

**Tech Stack**: Kotlin, Spring Boot 4, WebFlux, R2DBC, PostgreSQL, OAuth2 JWT (Auth0), JUnit 5, Cucumber BDD, ktlint, detekt, Gradle with Kotlin DSL, JDK 21

## Common Commands

### Building and Running
```bash
# Build the project (includes tests and code quality checks)
./gradlew build

# Run locally with local profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.ryzingtitan.cashcub.domain.budgets.services.BudgetServiceTests"

# Run tests with coverage report
./gradlew jacocoTestReport

# Verify coverage meets threshold (90% required)
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

### Docker
```bash
# Build Docker image using Spring Boot buildpack
./gradlew bootBuildImage --imageName=cashcub-api:latest

# Build and publish to registry
./gradlew bootBuildImage --imageName=<registry>/cashcub-api:<version> --publishImage
```

## Architecture

### Three-Layer Domain-Driven Design

The codebase is organized by domain entities with a clean three-layer architecture:

1. **Presentation Layer** (`presentation/`)
   - Reactive REST controllers using Spring WebFlux
   - All endpoints return reactive types (`Flow<T>` or `suspend` functions)
   - Security configuration with OAuth2 JWT
   - All `/api/*` endpoints require JWT authentication (except OPTIONS)
   - Public endpoints: `/actuator/health`, `/swagger-ui/**`, `/v3/api-docs/**`

2. **Domain Layer** (`domain/`)
   - Business logic services
   - DTOs for request/response (separate from entities)
   - Domain-specific exceptions (e.g., `DuplicateBudgetException`, `BudgetDoesNotExistException`)
   - Services use constructor injection with Spring's `@Service`

3. **Data Layer** (`data/`)
   - R2DBC reactive repositories
   - Database entities (separate from DTOs)
   - Repositories extend Spring Data R2DBC interfaces

### Domain Entities

The API manages five core domains:

- **Budgets**: Monthly budget periods identified by month/year combination
- **Budget Items**: Line items within budgets (e.g., "Groceries $500") linked to a budget and category
- **Transactions**: Individual income/expense entries linked to budget items
- **Categories**: Predefined categories for organizing items and transactions
- **Analytics**: Aggregated budget data and summaries

### Reactive Programming Patterns

- Controllers use `Flow<T>` for streaming responses and `suspend` for single-item responses
- Services return `Flow<T>` for collections and use `suspend` for single operations
- Repositories use R2DBC reactive types (`Mono`, `Flux`) which are converted to coroutines in services
- All database operations are non-blocking

### Authentication

- OAuth2 JWT tokens issued by Auth0 (issuer: https://dev-7pr07becg7e5y37g.us.auth0.com/)
- Security configured in `SecurityConfiguration.kt` with explicit path matchers
- Test authentication uses `mock-oauth2-server` library

## Environment Variables

Required for running the application:
- `DB_HOST` - PostgreSQL host (default: localhost)
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password

## Testing Strategy

- **Unit Tests**: Service and controller tests using JUnit 5 and Mockito Kotlin
- **Integration Tests**: Cucumber BDD tests with Gherkin feature files in `src/test/resources/features/`
- **Test Database**: H2 in-memory database (configured in `application-test.yml`)
- **Test Coverage**: 90% minimum line coverage enforced by JaCoCo

When writing tests:
- Use `mockito-kotlin` for mocking (not Java Mockito)
- Feature files follow pattern: `src/test/resources/features/<Feature>.feature`
- Step definitions: `src/test/kotlin/.../cucumber/`
- Mock JWT tokens with `mock-oauth2-server`

## Database Migrations

- All schema changes managed via Liquibase
- Master changelog: `src/main/resources/db/changelog/db.changelog-master.yaml`
- New changes must be added as changesets to the master file
- Liquibase runs on startup using JDBC (blocking) while the app uses R2DBC (reactive)

## Code Style and Conventions

- **ktlint**: Enforced formatting - run `./gradlew ktlintFormat` before committing
- **detekt**: Static analysis - run `./gradlew detekt` to check for issues
- **Logging**: Use SLF4J logger (`LoggerFactory.getLogger()`) at service layer
- **Nullable Types**: Database IDs are nullable in entities (null before save, non-null after)
- **Exception Handling**: Use domain-specific exceptions, not generic ones

## CI/CD

- **CI Workflow** (`ci.yml`): Runs on every push, executes `./gradlew build`
- **Build Workflow** (`build.yml`): Runs on push to `main` or manual trigger
  - Extracts version from `build.gradle.kts`
  - Creates Git tag `v{version}` if it doesn't exist
  - Builds Docker image and pushes to GitHub Container Registry (ghcr.io)

## Version Management

- Version defined in `build.gradle.kts` as `version = "x.y.z"`
- Docker images tagged with this version
- Git tags created automatically by build workflow with `v` prefix
