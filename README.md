<div style="display:flex; align-items:center; width:100%; gap:12px;">
  <img src="docs/assets/images/avenir-esr-logo_medium.jpg" alt="Avenir ESR Logo" height="48" />
  <div style="flex:1; text-align:center;">
    <h1 style="margin:0;">ePortFolio ESR</h1>
  </div>
  <div style="width:48px;"></div>
</div>

---

## Security microservice

## Purpose

This microservice provides OIDC-based authentication and RBAC (Role-Based Access Control) authorization.

## 🚀 Prerequisites

- **Java** 25
- **Maven** 4.0.0
- **PostgreSQL** (development)
- **Docker** (optional, to run PostgreSQL locally)

---

## 🏗️ Installation

```bash
git clone https://github.com/avenirs-esr/avenirs-portfolio-security.git
cd avenirs-portfolio-security
```

---

## 🛠️ Run the application

```bash
mvn spring-boot:run
```

The service is available at:

`http://localhost:12000`

---

## ⚙️ Configuration

Main configuration is located in `src/main/resources/application.properties`.

Key settings:

- `security.authentication.filter`
- `security.authentication.api-key`
- `security.permit-all-paths`
- `avenirs.authentication.oidc.*`

---

## 🔐 OIDC endpoints

Default endpoints are configured by:

- `avenirs.authentication.oidc.login`
- `avenirs.authentication.oidc.callback`
- `avenirs.authentication.oidc.callback.redirect`
- `avenirs.authentication.oidc.callback.profile`
- `avenirs.authentication.oidc.callback.introspect`

Request headers used by the controller:

- `x-forwarded-host`
- `x-authorization`

---

## 🧪 Tests

```bash
mvn clean test
```

The test profile uses an in-memory database configuration:

- `src/test/resources/application-test.properties`

---

## 📚 API documentation

Swagger UI:

`http://localhost:12000/avenirs-portfolio-security/swagger-ui`

OpenAPI JSON:

`http://localhost:12000/avenirs-portfolio-security/api-docs`

---

## 🔍 Monitoring and health

Spring Boot Actuator endpoints:

`http://localhost:12000/actuator`

---

## 🧹 Code Formatting – Google Java Format (via Spotless)

```bash
mvn spotless:check
```

```bash
mvn spotless:apply
```

---

## ⚡ CI/CD

This project includes automated checks for:

- Unit tests and coverage reporting
- Lint / formatting checks
- Security analysis

---

## API examples

See [docs/api/curl.md](docs/api/curl.md).
