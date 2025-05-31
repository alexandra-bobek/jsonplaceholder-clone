# JSONPlaceholder Clone

A Spring Boot implementation of the JSONPlaceholder API with additional features including JWT authentication and SQLite database support.

## Features

- Full REST API implementation matching JSONPlaceholder
- JWT-based authentication
- SQLite database with Flyway migrations
- Docker containerization
- Initial data seeding
- Comprehensive test coverage

## Prerequisites

- Java 21
- Docker and Docker Compose
- Gradle

## Getting Started

1. Clone the repository
2. Build and run using Docker Compose:

```bash
docker-compose up --build
```

The API will be available at http://localhost:3000

## API Endpoints

### Public Endpoints

- GET /users - Get all users
- GET /users/{id} - Get user by ID

### Protected Endpoints (Requires JWT)

- POST /users - Create new user
- PUT /users/{id} - Update user
- DELETE /users/{id} - Delete user

### Authentication

- POST /auth/register - Register new user
- POST /auth/login - Login and get JWT token

## Development

To run the application locally:

```bash
./gradlew bootRun
```

To run tests:

```bash
./gradlew test
```

## Database

The application uses SQLite with Flyway for database migrations. The database file is stored in the `data` directory and is persisted through Docker volumes.

## Security

- JWT tokens are used for authentication
- Passwords are hashed using bcrypt
- All sensitive endpoints require authentication
- CORS is configured for security

## Testing

The project includes:
- Unit tests
- Integration tests
- API tests

Run tests with:

```bash
./gradlew test
``` 