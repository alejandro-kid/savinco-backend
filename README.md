# Sistema de Gestión Financiera Multi-País - Savinco

## 📋 Descripción del Proyecto

Sistema backend para la gestión y visualización de datos financieros de múltiples países (Ecuador, España, Perú y Nepal) con conversión automática de monedas a dólares estadounidenses (USD). La solución permite gestionar capital ahorrado, capital prestado y utilidades generadas, manteniendo la trazabilidad de los valores originales y proporcionando totales consolidados.

---

## 🔧 Stack Tecnológico

- **Backend**:
  - Java 17
  - Spring Boot 3 (Spring Web, Spring Data JPA, Spring Validation)
  - PostgreSQL 15+ (o H2 en memoria para desarrollo)
  - Lombok, MapStruct
  - SpringDoc OpenAPI (Swagger)
- **Testing**:
  - JUnit 5, Mockito
  - Cucumber + Gherkin (tests BDD)
- **Herramientas**:
  - Maven, Git, Docker (opcional)

---

## 📁 Estructura del Proyecto (Backend)

```bash
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/savinco/financial/
│   │   │       ├── FinancialApplication.java
│   │   │       ├── web/controller/
│   │   │       │   ├── HealthController.java
│   │   │       │   ├── FinancialDataController.java
│   │   │       │   ├── CurrencyController.java
│   │   │       │   └── CountryController.java
│   │   │       ├── domain/
│   │   │       │   ├── model/
│   │   │       │   │   ├── FinancialData.java
│   │   │       │   │   ├── Currency.java
│   │   │       │   │   └── Country.java
│   │   │       │   └── repository/
│   │   │       │       ├── FinancialDataRepository.java
│   │   │       │       ├── CurrencyRepository.java
│   │   │       │       └── CountryRepository.java
│   │   │       ├── application/
│   │   │       │   ├── service/
│   │   │       │   │   ├── FinancialDataService.java
│   │   │       │   │   ├── CurrencyService.java
│   │   │       │   │   ├── CountryService.java
│   │   │       │   │   └── CurrencyConverterService.java
│   │   │       │   └── dto/
│   │   │       │       ├── FinancialDataRequest.java
│   │   │       │       ├── FinancialDataResponse.java
│   │   │       │       ├── ConsolidatedSummaryResponse.java
│   │   │       │       ├── CurrencyRequest.java
│   │   │       │       ├── CurrencyResponse.java
│   │   │       │       ├── CountryRequest.java
│   │   │       │       └── CountryResponse.java
│   │   │       └── infrastructure/
│   │   │           ├── persistence/entity/
│   │   │           │   ├── FinancialDataEntity.java
│   │   │           │   ├── CurrencyEntity.java
│   │   │           │   └── CountryEntity.java
│   │   │           └── persistence/repository/
│   │   │               ├── JpaFinancialDataRepository.java
│   │   │               ├── JpaCurrencyRepository.java
│   │   │               └── JpaCountryRepository.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   └── test/
│       ├── java/com/savinco/financial/
│       │   ├── CucumberTest.java
│       │   └── bdd/
│       │       ├── stepdefinitions/HealthCheckSteps.java
│       │       └── support/
│       │           ├── CucumberHooks.java
│       │           ├── TestConfiguration.java
│       │           └── TestContext.java
│       └── resources/
│           └── features/health-check.feature
├── pom.xml
└── README.md
```

---

## 🛠️ API REST (Backend)

### Health Check

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET`  | `/api/v1/health` | Verifica el estado de la API |

**Ejemplo de respuesta:**

```json
{
  "status": "UP",
  "timestamp": "2025-12-13T13:45:42.332508743Z"
}
```

### Financial Data (Base URL: `/api/v1/financial-data`)

> Estos endpoints gestionan la información financiera por país y realizan la conversión a USD.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET`  | `/` | Lista todos los países con datos en USD |
| `GET`  | `/{countryCode}` | Obtiene datos de un país específico en USD |
| `GET`  | `/summary` | Obtiene totales consolidados en USD |
| `POST` | `/` | Crea un nuevo registro de país |
| `PUT`  | `/{countryCode}` | Actualiza datos de un país |
| `DELETE` | `/{countryCode}` | Elimina datos de un país |

### Currency Management (Base URL: `/api/v1/currencies`)

> Estos endpoints gestionan las monedas y sus tasas de cambio.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET`  | `/` | Lista todas las monedas |
| `GET`  | `/{code}` | Obtiene una moneda por código |
| `POST` | `/` | Crea una nueva moneda |
| `PUT`  | `/{code}/exchange-rate` | Actualiza la tasa de cambio de una moneda |

### Country Management (Base URL: `/api/v1/countries`)

> Estos endpoints gestionan los países y su relación con monedas.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET`  | `/` | Lista todos los países |
| `GET`  | `/{code}` | Obtiene un país por código |
| `POST` | `/` | Crea un nuevo país |

**Ejemplo de request (POST/PUT):**

```json
{
  "countryCode": "ESP",
  "currencyCode": "EUR",
  "capitalSaved": 1000000.00,
  "capitalLoaned": 5000000.00,
  "profitsGenerated": 500000.00
}
```

**Ejemplo de request para crear Currency (POST `/api/v1/currencies`):**

```json
{
  "code": "EUR",
  "name": "Euro",
  "isBase": false,
  "exchangeRateToBase": 1.1111111111
}
```

**Ejemplo de request para crear Country (POST `/api/v1/countries`):**

```json
{
  "code": "ESP",
  "name": "España",
  "currencyCode": "EUR"
}
```

---

## 🚀 Instalación y Ejecución (Backend)

### Prerrequisitos

- Java 17+
- Maven 3.8+
- (Opcional) PostgreSQL 15+

### Inicio rápido con H2 (desarrollo)

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd backend

# 2. Compilar el proyecto
mvn clean compile

# 3. Ejecutar tests (recomendado)
mvn test

# 4. Ejecutar la aplicación con perfil de pruebas (H2 en memoria)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 5. Verificar health check
curl http://localhost:8080/api/v1/health
```

### Ejecución con PostgreSQL

Configura las credenciales en `application.yml` o mediante variables de entorno (`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`) y ejecuta:

```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

---

## 🧪 Testing

- **Tests unitarios y de integración**:

```bash
mvn test
```

- **Tests BDD con Cucumber** (runner principal):

```bash
mvn test -Dtest=CucumberTest
```

**Estado de tests:** 57 tests BDD pasando (1 health check + 9 POST + 10 GET + 11 PUT + 4 DELETE + 22 Currency/Country)

---

## 📚 Documentación

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs (OpenAPI JSON)**: `http://localhost:8080/api-docs`

---

## 👤 Autor

Desarrollado como parte de un reto técnico para el puesto de **Desarrollador Full Stack** en Savinco.

---

## 📄 Licencia

Este proyecto es parte de un reto técnico y no está destinado para uso comercial.
