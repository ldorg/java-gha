# Spring Boot Template

A comprehensive Spring Boot template project with production-ready best practices, featuring user management, security, testing, containerization, and CI/CD pipelines.

## Features

### Core Features
- **Spring Boot 3.3.x** with Java 17
- **RESTful API** with comprehensive user management
- **Security** with Spring Security and BCrypt password encoding
- **Data Persistence** with Spring Data JPA and H2/PostgreSQL support
- **API Documentation** with OpenAPI 3 (Swagger UI)
- **Validation** with Bean Validation (JSR-303)
- **Exception Handling** with global exception handlers
- **Configuration** with profiles (dev, prod, test)

### DevOps & Infrastructure
- **Docker** support with multi-stage builds
- **Docker Compose** for local development
- **GitHub Actions** CI/CD pipelines
- **Testing** with unit, integration, and security tests
- **Code Coverage** with JaCoCo
- **Dependency Updates** automation
- **Maven Wrapper** for consistent builds

### Development Tools
- **Hot Reload** with Spring Boot DevTools
- **Code Quality** with EditorConfig
- **Logging** with Logback configuration
- **Health Checks** for monitoring and Kubernetes
- **CORS** configuration for frontend integration

##  Tech Stack

| Technology | Purpose |
|------------|---------|
| Spring Boot 3.3.x | Application Framework |
| Spring Security | Authentication & Authorization |
| Spring Data JPA | Data Access Layer |
| H2/PostgreSQL | Database |
| OpenAPI 3 | API Documentation |
| Docker | Containerization |
| GitHub Actions | CI/CD |
| JUnit 5 | Testing Framework |
| Testcontainers | Integration Testing |
| Maven | Build Tool |

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+ (or use included Maven Wrapper)
- Docker (optional, for containerization)

### Running the Application

#### Option 1: Using Maven
```bash
# Clone the repository
git clone <repository-url>
cd spring-boot-template

# Run the application
./mvnw spring-boot:run
```

#### Option 2: Using Docker
```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build Docker image manually
docker build -t spring-boot-template .
docker run -p 8080:8080 spring-boot-template
```

#### Option 3: Using IDE
1. Import the project into your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run the `Application.java` class

### Accessing the Application

Once running, you can access:

- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/api-docs
- **H2 Console** (dev mode): http://localhost:8080/api/h2-console
- **Health Check**: http://localhost:8080/api/health
- **Actuator Endpoints**: http://localhost:8080/api/actuator

### Default Authentication
- **Username**: `admin`
- **Password**: `admin`

## 📁 Project Structure

```
spring-boot-template/
├── .github/
│   └── workflows/          # GitHub Actions CI/CD
├── src/
│   ├── main/
│   │   ├── java/com/example/template/
│   │   │   ├── config/     # Configuration classes
│   │   │   ├── controller/ # REST controllers
│   │   │   ├── dto/        # Data Transfer Objects
│   │   │   ├── exception/  # Exception handling
│   │   │   ├── model/      # JPA entities
│   │   │   ├── repository/ # Data repositories
│   │   │   ├── service/    # Business logic
│   │   │   └── Application.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/               # Tests (unit & integration)
├── docker-compose.yml      # Development environment
├── Dockerfile             # Production container
├── pom.xml                # Maven configuration
└── README.md              # This file
```

## Configuration

### Application Profiles

The application supports multiple profiles:

#### Development (`dev`)
- H2 in-memory database
- Debug logging enabled
- H2 console accessible
- Hot reload enabled

#### Production (`prod`)
- External database configuration
- Minimal logging
- Security headers enabled
- Optimized for performance

#### Test (`test`)
- Isolated test database
- Fast startup configuration
- Comprehensive logging for debugging

### Environment Variables

Key environment variables for production:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/myapp
DATABASE_USERNAME=myuser
DATABASE_PASSWORD=mypassword

# Server Configuration
PORT=8080
SPRING_PROFILES_ACTIVE=prod

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://myapp.com,https://www.myapp.com
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run integration tests
./mvnw verify

# Run with coverage
./mvnw test jacoco:report

# Run specific test
./mvnw test -Dtest=UserServiceTest
```

### Test Categories

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test complete workflows with real database
- **Security Tests**: Test authentication and authorization
- **API Tests**: Test REST endpoints with MockMvc

## 🐳 Docker

### Development with Docker Compose

```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# Rebuild and start
docker-compose up --build

# Stop services
docker-compose down

# View logs
docker-compose logs -f app
```

### Production Deployment

```bash
# Build production image
docker build -t spring-boot-template:latest .

# Run production container
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/myapp \
  spring-boot-template:latest
```

## API Endpoints

### User Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/users` | Create user | Yes |
| GET | `/api/users/{id}` | Get user by ID | Yes |
| GET | `/api/users` | List users (paginated) | Yes |
| PUT | `/api/users/{id}` | Update user | Yes |
| DELETE | `/api/users/{id}` | Delete user | Yes |
| PATCH | `/api/users/{id}/activate` | Activate user | Yes |
| PATCH | `/api/users/{id}/deactivate` | Deactivate user | Yes |

### Health & Monitoring

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/health` | Application health | No |
| GET | `/api/health/ready` | Readiness probe | No |
| GET | `/api/health/live` | Liveness probe | No |
| GET | `/api/actuator/health` | Detailed health | No |
| GET | `/api/actuator/metrics` | Application metrics | Yes |

### Example API Calls

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepassword"
  }'

# Get all users
curl -X GET http://localhost:8080/api/users \
  -u admin:admin

# Search users
curl -X GET "http://localhost:8080/api/users?search=john" \
  -u admin:admin
```

## Security

### Security Features

- **Authentication**: HTTP Basic Authentication (easily replaceable with JWT)
- **Password Encoding**: BCrypt with salt
- **HTTPS**: Ready for SSL/TLS termination
- **CORS**: Configurable cross-origin resource sharing
- **SQL Injection**: Protected by JPA and parameterized queries
- **XSS Protection**: JSON serialization with proper encoding
- **Security Headers**: Configurable security headers

### Security Configuration

```yaml
# application.yml
spring:
  security:
    user:
      name: admin
      password: admin  # Change in production!
      roles: ADMIN

# CORS configuration
app:
  cors:
    allowed-origins: "http://localhost:3000,https://yourdomain.com"
```

##  Monitoring & Observability

### Health Checks

- **Application Health**: `/api/health`
- **Liveness Probe**: `/api/health/live`
- **Readiness Probe**: `/api/health/ready`
- **Detailed Health**: `/api/actuator/health`

### Metrics

- **JVM Metrics**: Memory, threads, garbage collection
- **HTTP Metrics**: Request count, response times
- **Database Metrics**: Connection pool, query performance
- **Custom Metrics**: Business-specific metrics

### Logging

```yaml
# Structured logging configuration
logging:
  level:
    com.example.template: INFO
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
```

## Deployment

### Kubernetes Deployment

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-template
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-boot-template
  template:
    metadata:
      labels:
        app: spring-boot-template
    spec:
      containers:
      - name: app
        image: ghcr.io/yourorg/spring-boot-template:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        livenessProbe:
          httpGet:
            path: /api/health/live
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/health/ready
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```


## Contributing

### Development Workflow

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc for public APIs
- Maintain test coverage above 80%
- Use the provided `.editorconfig` settings

### Testing Guidelines

- Write unit tests for all business logic
- Include integration tests for API endpoints
- Use meaningful test method names
- Mock external dependencies
- Test both success and failure scenarios


## 🐛 Troubleshooting

### Common Issues

**Issue**: Application fails to start with database connection error
```bash
# Solution: Check database configuration in application.yml
# For H2, ensure the database URL and credentials are correct
# For PostgreSQL, verify the database server is running
```

**Issue**: Tests fail with "Port already in use" error
```bash
# Solution: Kill process using the port or use a different test port
lsof -ti:8080 | xargs kill -9
```

**Issue**: Docker build fails with permission denied
```bash
# Solution: Ensure Docker daemon is running and user has permissions
sudo systemctl start docker
sudo usermod -a -G docker $USER
```

