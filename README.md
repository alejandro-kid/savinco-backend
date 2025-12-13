# Sistema de Gestión Financiera Multi-País - Savinco

## 📋 Descripción del Proyecto

Sistema fullstack para la gestión y visualización de datos financieros de múltiples países (Ecuador, España, Perú y Nepal) con conversión automática de monedas a dólares estadounidenses (USD). La solución permite gestionar capital ahorrado, capital prestado y utilidades generadas, manteniendo la trazabilidad de los valores originales y proporcionando totales consolidados.

---

## 🎯 Objetivos del Reto

1. **Evaluar competencias técnicas:**
   - Modelado y persistencia de datos en base de datos relacional
   - Desarrollo backend en Java con Spring Boot (servicios RESTful)
   - Implementación frontend con HTML5, CSS3 y framework JavaScript (React)
   - Buenas prácticas, organización del código, Git y documentación

2. **Validar capacidad de desarrollo:**
   - Transformar requerimientos funcionales en aplicación operativa
   - Mantener código estructurado y mantenible

---

## 🏗️ Arquitectura de la Solución

### Decisiones Arquitectónicas

Para este reto de recruitment, se ha optado por una **arquitectura simplificada pero profesional** que demuestra seniority sin sobre-ingeniería:

#### ✅ Arquitectura Elegida: **Layered Architecture (3 Capas)**

```bash
┌─────────────────────────────────────┐
│     Presentation Layer (REST API)   │
│     - Controllers                   │
│     - DTOs                          │
│     - Exception Handlers            │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Application Layer (Services)    │
│     - Use Cases                     │
│     - Business Logic                │
│     - Currency Conversion Service   │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     Infrastructure Layer            │
│     - Repositories (JPA)            │
│     - Entities                      │
│     - Database Configuration        │
└─────────────────────────────────────┘
```

**Razón:** Para un reto de recruitment, una arquitectura de 3 capas es suficiente para demostrar:

- Separación de responsabilidades
- Testabilidad
- Mantenibilidad
- Sin complejidad innecesaria (CQRS/EDA no son necesarios aquí)

#### ❌ Patrones NO utilizados (y por qué)

- **CQRS**: No necesario - queries simples, no hay problemas de rendimiento
- **Event-Driven Architecture**: No hay comunicación entre bounded contexts
- **Outbox Pattern**: No hay eventos críticos que requieran garantía de entrega
- **Hexagonal Architecture completa**: Over-engineering para el scope del reto

---

## 📊 Modelo de Datos

### Entidad: `FinancialData`

```sql
CREATE TABLE financial_data (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL,  -- ECU, ESP, PER, NPL
    currency_code VARCHAR(3) NOT NULL, -- USD, EUR, PEN, NPR
    capital_saved NUMERIC(19,2) NOT NULL,
    capital_loaned NUMERIC(19,2) NOT NULL,
    profits_generated NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_country UNIQUE (country_code)
);
```

### Países y Monedas

| País | Código | Moneda | Código Moneda |
|------|--------|--------|---------------|
| Ecuador | ECU | Dólares | USD |
| España | ESP | Euros | EUR |
| Perú | PER | Soles | PEN |
| Nepal | NPL | Rupia Nepalí | NPR |

### Tipos de Cambio (fijos)

```java
1 USD = 0.90 EUR   → 1 EUR = 1.1111 USD
1 USD = 3.3 PEN    → 1 PEN = 0.3030 USD
1 USD = 133 NPR    → 1 NPR = 0.0075 USD
```

**Decisión:** Los tipos de cambio se almacenan como constantes en el código. Para producción se podría usar una tabla de configuración, pero para el reto es suficiente.

---

## 🔧 Stack Tecnológico

### Stack Backend

- **Java 17** (LTS)
- **Spring Boot 3.x**
  - Spring Web (REST API)
  - Spring Data JPA (persistencia)
  - Spring Validation (validaciones)
  - SpringDoc OpenAPI (Swagger)
- **PostgreSQL 15+** (base de datos)
- **Lombok** (reducción de boilerplate)
- **MapStruct** (mapeo DTO ↔ Entity)
- **JUnit 5 + Mockito** (unit testing)
- **TestContainers** (integración tests con PostgreSQL)
- **Cucumber + Gherkin** (BDD - E2E testing) ⚠️ **INVARIABLE - OBLIGATORIO**

### Stack Frontend

- **React 18+** (Hooks, Functional Components)
- **Vite** (build tool)
- **Axios** (HTTP client)
- **React Router** (navegación)
- **CSS3 puro** (semántico y accesible)
- **HTML5 + CSS3** (semántico y accesible)

### Herramientas

- **Git** (control de versiones)
- **Maven** (gestión de dependencias)
- **Docker** (opcional, para facilitar setup)

---

## 📁 Estructura del Proyecto

### Backend (Spring Boot)

```bash
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/savinco/
│   │   │       └── financial/
│   │   │           ├── FinancialApplication.java          ✅
│   │   │           ├── web/
│   │   │           │   └── controller/
│   │   │           │       └── HealthController.java      ✅
│   │   │           ├── domain/                            ⏳
│   │   │           │   ├── model/
│   │   │           │   │   ├── FinancialData.java
│   │   │           │   │   └── Country.java (enum)
│   │   │           │   └── repository/
│   │   │           │       └── FinancialDataRepository.java
│   │   │           ├── application/                       ⏳
│   │   │           │   ├── service/
│   │   │           │   │   ├── FinancialDataService.java
│   │   │           │   │   └── CurrencyConverterService.java
│   │   │           │   └── dto/
│   │   │           │       ├── FinancialDataRequest.java
│   │   │           │       ├── FinancialDataResponse.java
│   │   │           │       └── ConsolidatedSummaryResponse.java
│   │   │           └── infrastructure/                    ⏳
│   │   │               ├── persistence/
│   │   │               │   ├── entity/
│   │   │               │   │   └── FinancialDataEntity.java
│   │   │               │   └── repository/
│   │   │               │       └── JpaFinancialDataRepository.java
│   │   │               └── config/
│   │   │                   └── DatabaseConfig.java
│   │   └── resources/
│   │       ├── application.yml                            ✅
│   │       └── db/migration/ (Flyway - opcional)
│   └── test/
│       ├── java/
│       │   └── com/savinco/
│       │       └── financial/
│       │           ├── CucumberTest.java                  ✅
│       │           └── bdd/
│       │               ├── stepdefinitions/
│       │               │   └── HealthCheckSteps.java      ✅
│       │               └── support/
│       │                   ├── CucumberHooks.java          ✅
│       │                   ├── TestConfiguration.java     ✅
│       │                   └── TestContext.java             ✅
│       └── resources/
│           ├── application-test.yml                       ✅
│           └── features/
│               └── health-check.feature                  ✅
├── pom.xml                                                ✅
└── README.md
```

**Leyenda:**

- ✅ Implementado y funcionando
- ⏳ Pendiente de implementación

### Frontend (React)

```bash
frontend/
├── public/
│   └── index.html
├── src/
│   ├── assets/
│   │   └── styles/
│   │       └── main.css
│   ├── components/
│   │   ├── FinancialCard.jsx
│   │   ├── FinancialForm.jsx
│   │   └── SummaryCard.jsx
│   ├── pages/
│   │   ├── HomePage.jsx
│   │   └── DashboardPage.jsx
│   ├── services/
│   │   └── api.js
│   ├── App.jsx
│   └── main.jsx
├── package.json
└── vite.config.js
```

---

## 🔄 Flujo de Datos

### 1. Almacenamiento

- Los datos se guardan en su **moneda original** en la base de datos
- Se mantiene trazabilidad del país y moneda

### 2. Conversión

- La conversión a USD se realiza **en el backend** (nunca en el frontend)
- Se aplica al momento de consultar los datos

### 3. Visualización

- El frontend consume endpoints que ya devuelven valores en USD
- Se muestran totales consolidados por país y total general

---

## 🛠️ API REST - Endpoints

### Health Check

| Método | Endpoint | Descripción | Status |
|--------|----------|-------------|--------|
| `GET` | `/api/v1/health` | Verificar estado de la API | ✅ Implementado |

**Response:**

```json
{
  "status": "UP",
  "timestamp": "2025-12-13T13:45:42.332508743Z"
}
```

### Financial Data (Base URL: `/api/v1/financial-data`)

| Método | Endpoint | Descripción | Request Body | Status |
|--------|----------|-------------|--------------|--------|
| `GET` | `/` | Listar todos los países con datos en USD | - | ⏳ Pendiente |
| `GET` | `/{countryCode}` | Obtener datos de un país específico en USD | - | ⏳ Pendiente |
| `GET` | `/summary` | Obtener totales consolidados en USD | - | ⏳ Pendiente |
| `POST` | `/` | Crear nuevo registro de país | `FinancialDataRequest` | ⏳ Pendiente |
| `PUT` | `/{countryCode}` | Actualizar datos de un país | `FinancialDataRequest` | ⏳ Pendiente |
| `DELETE` | `/{countryCode}` | Eliminar datos de un país | - | ⏳ Pendiente |

### Ejemplo de Request (POST/PUT)

```json
{
  "countryCode": "ESP",
  "currencyCode": "EUR",
  "capitalSaved": 1000000.00,
  "capitalLoaned": 5000000.00,
  "profitsGenerated": 500000.00
}
```

### Ejemplo de Response (GET)

```json
{
  "countryCode": "ESP",
  "countryName": "España",
  "originalCurrency": "EUR",
  "capitalSaved": 1111111.11,
  "capitalLoaned": 5555555.56,
  "profitsGenerated": 555555.56,
  "totalInUSD": 7222222.23
}
```

### Ejemplo de Summary Response

```json
{
  "totalCapitalSaved": 33666477.00,
  "totalCapitalLoaned": 274878091.00,
  "totalProfitsGenerated": 39581411.00,
  "grandTotal": 348125979.00,
  "byCountry": [
    {
      "countryCode": "ECU",
      "countryName": "Ecuador",
      "capitalSaved": 1000000.00,
      "capitalLoaned": 5000000.00,
      "profitsGenerated": 500000.00
    },
    ...
  ]
}
```

---

## 🎨 Frontend - Páginas

### 1. Página Principal (Home)

**Ruta:** `/`

**Características:**

- Diseño atractivo y moderno
- Muestra los datos consolidados del Anexo
- Cards visuales para cada métrica:
  - Capital Ahorrado Total
  - Capital Prestado Total
  - Utilidades Generadas Total
- Filtro por país (opcional)
- Diseño responsive

**Componentes:**

- `SummaryCard.jsx` - Cards de métricas
- `FinancialCard.jsx` - Card por país

### 2. Página de Administración (Dashboard)

**Ruta:** `/dashboard`

**Características:**

- Tabla con todos los países
- Formulario para crear/editar registros
- Botones de acción (Editar/Eliminar)
- Validación de formularios
- Confirmación antes de eliminar
- Mensajes de éxito/error

**Componentes:**

- `FinancialForm.jsx` - Formulario CRUD
- Tabla de datos con acciones

---

## 🧪 Testing Strategy

### ⚠️ REGLA INVARIABLE: BDD con Cucumber + Gherkin

**NUNCA escribir código sin primero tener un archivo `.feature` con escenarios Gherkin validados.**

**Flujo obligatorio:**

1. Discovery Workshop (Example Mapping)
2. Aplicar Decision Framework
3. Escribir archivo `.feature` con escenarios Gherkin
4. Validar con Product Owner
5. Crear step definitions (RED)
6. Implementar código (GREEN)
7. Refactor

### Backend Testing

1. **BDD Tests (Cucumber + Gherkin) - OBLIGATORIO:**
   - Escenarios Gherkin en `src/test/resources/features/`
   - Step definitions en `src/test/java/.../stepdefinitions/`
   - Cobertura de:
     - Happy path (validación SYNC inmediata)
     - Error más común (feedback SYNC inmediato)
     - Comportamiento ASYNC (si aplica)
     - Casos límite

   **Estructura actual implementada:**

   ```bash
   src/test/
   ├── java/com/savinco/financial/
   │   ├── CucumberTest.java                    # Test suite runner
   │   └── bdd/
   │       ├── stepdefinitions/
   │       │   └── HealthCheckSteps.java        # ✅ Implementado
   │       └── support/
   │           ├── CucumberHooks.java          # Spring Boot context
   │           ├── TestConfiguration.java        # Test beans
   │           └── TestContext.java             # Shared state
   └── resources/
       ├── application-test.yml                 # Test profile config
       └── features/
           └── health-check.feature             # ✅ Implementado
   ```

   **Ejecutar tests BDD:**

   ```bash
   # Ejecutar todos los tests de Cucumber
   mvn test -Dtest=CucumberTest
   
   # Ejecutar todos los tests
   mvn test
   ```

2. **Unit Tests:**
   - `CurrencyConverterServiceTest` - Validar conversiones (⏳ Pendiente)
   - `FinancialDataServiceTest` - Lógica de negocio (⏳ Pendiente)

3. **Integration Tests:**
   - `FinancialDataControllerTest` - Endpoints REST (⏳ Pendiente)
   - `FinancialDataIntegrationTest` - Flujo completo con TestContainers (⏳ Pendiente)

4. **Cobertura objetivo:** >80% en lógica de negocio

### Frontend

1. **Unit Tests (opcional):**
   - Tests de componentes React
   - Tests de servicios API

2. **E2E Tests (opcional):**
   - Cypress o Playwright para flujos críticos

---

## 🚀 Instalación y Ejecución

### ⚡ Inicio Rápido

**Para ejecutar el proyecto rápidamente (modo desarrollo con H2):**

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd backend

# 2. (Opcional) Configurar variables de entorno
cp .env.example .env
# Editar .env si necesitas personalizar configuración

# 3. Compilar el proyecto
mvn clean compile

# 4. Ejecutar tests (opcional, pero recomendado)
mvn test

# 5. Ejecutar la aplicación (usa H2 en memoria automáticamente)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=test

# 6. Verificar que funciona
curl http://localhost:8080/api/v1/health
```

**Nota:** Spring Boot carga automáticamente las variables de entorno del sistema. Si defines variables como `SERVER_PORT` o `DATABASE_URL`, estas sobrescribirán los valores por defecto en `application.yml`.

**Respuesta esperada:**

```json
{"status":"UP","timestamp":"2025-12-13T..."}
```

### Configuración con Variables de Entorno

El proyecto soporta configuración mediante variables de entorno. Esto permite ejecutar la aplicación en diferentes entornos sin modificar archivos de configuración.

**Configurar variables de entorno:**

```bash
# 1. Copiar archivo de ejemplo
cp .env.example .env

# 2. Editar .env con tus valores
nano .env  # o tu editor preferido

# 3. Cargar variables de entorno (opcional, según tu shell)
# Para bash/zsh:
export $(cat .env | xargs)

# Para fish:
cat .env | while read line; set -x (string split = $line)[1] (string split = $line)[2]; end
```

**Variables de entorno disponibles:**

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `APP_NAME` | Nombre de la aplicación | `financial-backend` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `DATABASE_URL` | URL de conexión a la base de datos | `jdbc:postgresql://localhost:5432/savinco_financial` |
| `DATABASE_USERNAME` | Usuario de la base de datos | `postgres` |
| `DATABASE_PASSWORD` | Contraseña de la base de datos | `postgres` |
| `DATABASE_DRIVER` | Driver JDBC | `org.postgresql.Driver` |
| `JPA_DDL_AUTO` | Estrategia de DDL de Hibernate | `update` |
| `JPA_SHOW_SQL` | Mostrar SQL en logs | `true` |
| `SWAGGER_ENABLED` | Habilitar Swagger UI | `true` |
| `LOG_LEVEL_APP` | Nivel de log de la aplicación | `DEBUG` |

**Ejemplo de uso:**

```bash
# Ejecutar con puerto personalizado
export SERVER_PORT=9090
mvn spring-boot:run

# Ejecutar con base de datos diferente
export DATABASE_URL=jdbc:postgresql://localhost:5432/mi_base_datos
export DATABASE_USERNAME=mi_usuario
export DATABASE_PASSWORD=mi_password
mvn spring-boot:run

# Cargar desde archivo .env (bash/zsh)
export $(cat .env | grep -v '^#' | xargs)
mvn spring-boot:run
```

**Configuración por Entorno:**

```bash
# Desarrollo local
export DATABASE_URL=jdbc:postgresql://localhost:5432/savinco_financial_dev
export LOG_LEVEL_APP=DEBUG

# Staging
export DATABASE_URL=jdbc:postgresql://staging-db:5432/savinco_financial
export LOG_LEVEL_APP=INFO
export SWAGGER_ENABLED=false

# Producción
export DATABASE_URL=jdbc:postgresql://prod-db:5432/savinco_financial
export LOG_LEVEL_APP=WARN
export SWAGGER_ENABLED=false
export JPA_SHOW_SQL=false
```

**Nota importante:** Spring Boot carga automáticamente las variables de entorno del sistema operativo. No se requiere configuración adicional. Si usas un archivo `.env`, necesitas cargarlo manualmente o usar herramientas como `dotenv` (requiere dependencia adicional).

### Prerrequisitos

- **Java 17+** (verificar con `java -version`)
- **Maven 3.8+** (verificar con `mvn -version`)
- **PostgreSQL 15+** (opcional, solo si no usas H2)
- **Node.js 18+ y npm** (solo para frontend)
- **Git**

### Instalación Backend

#### Opción 1: Desarrollo con H2 (Recomendado para empezar)

Esta opción no requiere configuración de base de datos:

```bash
# 1. Clonar repositorio
git clone <repository-url>
cd backend

# 2. Compilar proyecto
mvn clean compile

# 3. Ejecutar tests
mvn test

# 4. Ejecutar aplicación con perfil test (H2 en memoria)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# La aplicación estará disponible en http://localhost:8080
```

#### Opción 2: Desarrollo con PostgreSQL

Si prefieres usar PostgreSQL para desarrollo:

```bash
# 1. Clonar repositorio
git clone <repository-url>
cd backend

# 2. Crear base de datos PostgreSQL
psql -U postgres
CREATE DATABASE savinco_financial;
\q

# 3. Configurar variables de entorno (recomendado)
# Copiar archivo de ejemplo
cp .env.example .env

# Editar .env con tus credenciales
# DATABASE_URL=jdbc:postgresql://localhost:5432/savinco_financial
# DATABASE_USERNAME=postgres
# DATABASE_PASSWORD=tu_password

# 4. Compilar proyecto
mvn clean compile

# 5. Ejecutar aplicación (sin perfil, usa PostgreSQL)
mvn spring-boot:run

# La aplicación estará disponible en http://localhost:8080
```

#### Alternativa: Configurar directamente en application.yml

Si prefieres no usar variables de entorno, puedes editar directamente:
`src/main/resources/application.yml` y ajustar las credenciales.

#### Opción 3: Solo Ejecutar Tests

```bash
# Ejecutar todos los tests (incluye BDD con Cucumber)
mvn test

# Ejecutar solo tests de Cucumber
mvn test -Dtest=CucumberTest

# Ejecutar tests con reportes detallados
mvn test -Dtest=CucumberTest
# Reportes HTML disponibles en: target/cucumber-reports/index.html
```

### Verificación y Endpoints Disponibles

Una vez que la aplicación esté ejecutándose, puedes verificar y acceder a:

**1. Health Check:**

```bash
curl http://localhost:8080/api/v1/health
```

**2. Swagger UI (Interfaz gráfica para probar la API):**

```text
http://localhost:8080/swagger-ui.html
```

**3. API Documentation (JSON):**

```text
http://localhost:8080/api-docs
```

**4. Actuator Health (si está habilitado):**

```text
http://localhost:8080/actuator/health
```

### Comandos Útiles

```bash
# Compilar sin ejecutar tests
mvn clean compile -DskipTests

# Ejecutar solo tests unitarios (sin BDD)
mvn test -Dtest='*Test' -Dtest='!CucumberTest'

# Limpiar y recompilar todo
mvn clean install

# Ver logs en tiempo real
mvn spring-boot:run -Dspring-boot.run.profiles=test | tail -f

# Ejecutar en puerto diferente usando variable de entorno
export SERVER_PORT=8081
mvn spring-boot:run
```

### Troubleshooting

#### Puerto 8080 ya está en uso

```bash
# Opción 1: Usar variable de entorno (recomendado)
export SERVER_PORT=8081
mvn spring-boot:run

# Opción 2: Cambiar puerto en application.yml
# server.port: 8081

# Opción 3: Usar argumento de línea de comandos
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

#### Error de conexión a PostgreSQL

- Verificar que PostgreSQL esté ejecutándose: `sudo systemctl status postgresql`
- Verificar variables de entorno: `echo $DATABASE_URL $DATABASE_USERNAME`
- Verificar credenciales en `.env` o `application.yml`
- Verificar que la base de datos existe: `psql -U postgres -l`

#### Tests fallan

- Verificar que Java 17+ está instalado: `java -version`
- Limpiar y recompilar: `mvn clean test`
- Verificar logs en `target/surefire-reports/`

#### Maven no encuentra dependencias

```bash
# Limpiar cache de Maven y descargar dependencias
mvn clean
mvn dependency:resolve
```

### Instalación Frontend

> ⚠️ **Nota:** El frontend aún no está implementado. Estas instrucciones son para referencia futura.

```bash
# 1. Navegar a directorio frontend
cd frontend

# 2. Instalar dependencias
npm install

# 3. Configurar API base URL
# Editar src/services/api.js con URL del backend
# Ejemplo: const API_BASE_URL = 'http://localhost:8080/api/v1'

# 4. Ejecutar en modo desarrollo
npm run dev

# La aplicación frontend estará disponible en http://localhost:5173 (Vite default)

# 5. Build para producción
npm run build

# Los archivos compilados estarán en dist/
```

### Flujo de Trabajo de Desarrollo

**Flujo recomendado para desarrollo diario:**

```bash
# 1. Actualizar código desde repositorio
git pull origin develop

# 2. Ejecutar tests antes de hacer cambios
mvn test

# 3. Hacer cambios en el código

# 4. Ejecutar tests después de cambios
mvn test

# 5. Si todo pasa, ejecutar aplicación para probar manualmente
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 6. Probar endpoints manualmente o con Swagger UI
# http://localhost:8080/swagger-ui.html

# 7. Hacer commit de cambios
git add .
git commit -m "feat: descripción del cambio"
```

### CI/CD con GitHub Actions

El proyecto incluye un pipeline de CI/CD automatizado que se ejecuta en cada push y pull request.

**Pipeline incluye:**

1. **Linting y Compilación:**
   - Compilación del proyecto con Maven
   - Verificación de calidad de código con Checkstyle

2. **Testing:**
   - Ejecución de todos los tests unitarios
   - Ejecución de tests BDD con Cucumber
   - Generación de reportes de tests

3. **Build y Push de Docker:**
   - Construcción de imagen Docker (solo en push a main/develop)
   - Push automático a GitHub Container Registry (ghcr.io)
   - Tags automáticos basados en branch y commit

**Ver estado del pipeline:**

- Ve a la pestaña "Actions" en GitHub
- Cada workflow muestra el estado de cada job
- Los reportes de tests están disponibles como artifacts

**Imágenes Docker disponibles:**

El sistema de etiquetado automático genera los siguientes tags:

```bash
# Para la rama main (default branch):
ghcr.io/<usuario>/<repo>/financial-backend:latest          # Tag latest
ghcr.io/<usuario>/<repo>/financial-backend:abc1234         # SHA corto sin prefijo

# Para la rama develop:
ghcr.io/<usuario>/<repo>/financial-backend:develop-abc1234 # SHA corto con prefijo de branch
```

**Sistema de etiquetado:**

- **Rama main**: `latest` + SHA corto sin prefijo (ej: `abc1234`)
- **Rama develop**: SHA corto con prefijo de branch (ej: `develop-abc1234`)
- Las imágenes se construyen automáticamente solo en push a `main` o `develop`

**Pull de imagen desde GHCR:**

```bash
# Login a GHCR
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Pull imagen
docker pull ghcr.io/<usuario>/<repo>/financial-backend:latest
```

### Docker

El proyecto incluye un Dockerfile optimizado para producción y docker-compose para facilitar el desarrollo.

#### Construir y Ejecutar con Docker

##### Opción 1: Solo la aplicación (requiere PostgreSQL externo)

```bash
# 1. Construir la imagen
docker build -t savinco/financial-backend:latest .

# 2. Ejecutar el contenedor
docker run -d \
  --name financial-backend \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/savinco_financial \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  savinco/financial-backend:latest

# 3. Ver logs
docker logs -f financial-backend

# 4. Detener y eliminar
docker stop financial-backend
docker rm financial-backend
```

##### Opción 2: Docker Compose (aplicación + PostgreSQL)

```bash
# 1. Crear archivo .env (opcional, usa valores por defecto si no existe)
cp .env.example .env
# Editar .env si necesitas personalizar

# 2. Iniciar todos los servicios
docker-compose up -d

# 3. Ver logs
docker-compose logs -f financial-backend

# 4. Verificar que funciona
curl http://localhost:8080/api/v1/health

# 5. Detener servicios
docker-compose down

# 6. Detener y eliminar volúmenes (elimina datos de PostgreSQL)
docker-compose down -v
```

#### Características del Dockerfile

- **Multi-stage build**: Reduce el tamaño final de la imagen
- **Imagen base ligera**: Eclipse Temurin 17 JRE Alpine (~150MB)
- **Usuario no-root**: Ejecuta como usuario `spring` para seguridad
- **Health check**: Verifica automáticamente el estado de la aplicación
- **Optimización JVM**: Configurado para contenedores con límites de memoria
- **Variables de entorno**: Soporta todas las variables configuradas

#### Comandos Docker Útiles

```bash
# Construir sin cache
docker build --no-cache -t savinco/financial-backend:latest .

# Construir con tag específico
docker build -t savinco/financial-backend:1.0.0 .

# Ejecutar en modo interactivo (debugging)
docker run -it --rm \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/savinco_financial \
  savinco/financial-backend:latest

# Ver logs en tiempo real
docker-compose logs -f

# Ejecutar comandos dentro del contenedor
docker-compose exec financial-backend sh

# Reiniciar un servicio específico
docker-compose restart financial-backend

# Ver uso de recursos
docker stats financial-backend
```

#### Variables de Entorno en Docker

Todas las variables de entorno están disponibles en Docker. Puedes configurarlas en:

1. **Archivo `.env`** (para docker-compose)
2. **Línea de comandos** con `-e VARIABLE=valor`
3. **docker-compose.yml** en la sección `environment:`

Ejemplo con variables personalizadas:

```bash
docker run -d \
  --name financial-backend \
  -p 9090:9090 \
  -e SERVER_PORT=9090 \
  -e DATABASE_URL=jdbc:postgresql://db:5432/mi_base \
  -e DATABASE_USERNAME=mi_usuario \
  -e DATABASE_PASSWORD=mi_password \
  -e LOG_LEVEL_APP=INFO \
  -e SWAGGER_ENABLED=false \
  savinco/financial-backend:latest
```

---

## 📝 Decisiones de Diseño

### 0. **BDD con Cucumber + Gherkin - INVARIABLE**

⚠️ **DECISIÓN INVARIABLE:** Todo desarrollo debe seguir BDD con Cucumber y Gherkin.

**Razón:**

- Garantiza que el código cumple con los requerimientos del negocio
- Facilita comunicación entre desarrolladores y stakeholders
- Documentación viva y ejecutable
- Tests E2E que validan comportamiento completo

**Reglas obligatorias:**

- ❌ **NUNCA** escribir código sin archivo `.feature` primero
- ✅ **SIEMPRE** escribir escenarios Gherkin antes de implementar
- ✅ **SIEMPRE** validar escenarios con Product Owner
- ✅ **SIEMPRE** crear step definitions que fallen primero (RED)
- ✅ **SIEMPRE** implementar hasta que tests pasen (GREEN)

**Estructura:**

```bash
src/test/resources/features/
├── financial-data-management.feature
└── currency-conversion.feature

src/test/java/.../stepdefinitions/
├── FinancialDataSteps.java
└── CurrencyConversionSteps.java
```

### 1. **Conversión de Monedas en Backend**

✅ **Decisión:** Toda la lógica de conversión se ejecuta en el servidor.

**Razón:**

- Seguridad: El cliente no puede manipular tipos de cambio
- Consistencia: Todos los clientes ven los mismos valores
- Mantenibilidad: Un solo lugar para actualizar lógica

### 2. **Almacenamiento en Moneda Original**

✅ **Decisión:** Guardar valores originales, convertir al consultar.

**Razón:**

- Trazabilidad: Se mantiene el valor histórico original
- Flexibilidad: Si cambian tipos de cambio, se recalcula automáticamente
- Auditoría: Se puede verificar conversiones

### 3. **Arquitectura de 3 Capas (no Hexagonal completa)**

✅ **Decisión:** Layered Architecture simple pero profesional.

**Razón:**

- Suficiente para demostrar separación de responsabilidades
- Mantenible y testeable
- No over-engineering para el scope del reto

### 4. **React 18+ con Hooks**

✅ **Decisión:** React 18+ con Functional Components y Hooks.

**Razón:**

- Framework moderno y ampliamente adoptado
- Hooks permiten reutilización de lógica
- Componentes funcionales más simples y testeables
- Gran ecosistema y comunidad

### 5. **PostgreSQL como Base de Datos**

✅ **Decisión:** PostgreSQL (base de datos relacional robusta).

**Razón:**

- Base de datos relacional robusta y confiable
- Excelente soporte para tipos numéricos precisos (NUMERIC)
- Ampliamente usado en producción
- Open source y bien documentado

---

## ✅ Checklist de Implementación

> **📋 Nota:** El checklist completo y detallado se encuentra en `TODO.md` (archivo local, no versionado).

**Resumen:**
- ✅ Configuración base y health check completados
- ⏳ CRUD de datos financieros (en progreso)
- ⏳ Frontend React (pendiente)

Para ver el estado detallado y marcar progreso, consulta `TODO.md` localmente.

---

## 📊 Datos de Prueba (Anexo)

Según el Anexo del reto, los valores esperados en USD son:

- **Capital Ahorrado Total:** $33,666,477
- **Capital Prestado Total:** $274,878,091
- **Utilidades Generadas Total:** $39,581,411

Estos valores deben obtenerse después de aplicar las conversiones desde las monedas originales.

---

## 🔒 Consideraciones de Seguridad

Para un reto de recruitment, se mantiene simple pero profesional:

- ✅ Validación de datos de entrada
- ✅ Manejo de excepciones apropiado
- ✅ CORS configurado correctamente
- ⚠️ Autenticación/Autorización: **No implementada** (no requerida en el reto)

---

## 📚 Documentación Adicional

- **Swagger UI:** Disponible en `http://localhost:8080/swagger-ui.html` cuando el backend esté ejecutándose
- **API Docs:** Generadas automáticamente en `http://localhost:8080/api-docs`
- **Health Check:** `http://localhost:8080/api/v1/health`

### Ejemplo de Feature File (BDD)

```gherkin
Feature: Health Check Endpoint
  As a system administrator
  I want to check the health status of the API
  So that I can verify the service is running and operational

  Scenario: Successfully check API health status
    Given the API is running
    When I request the health check endpoint
    Then I should receive status code 200 immediately
    And the response should contain status "UP"
    And the response should contain timestamp
```

### Configuración de Tests BDD

Los tests de Cucumber están configurados para:

- Usar H2 in-memory database automáticamente
- Levantar Spring Boot Test context
- Ejecutar en puerto aleatorio para evitar conflictos
- Generar reportes HTML en `target/cucumber-reports`

---

## 🎯 Criterios de Éxito

> **📋 Nota:** Los criterios de éxito detallados y el estado actual se encuentran en `TODO.md` (archivo local, no versionado).

La solución se considerará exitosa cuando:
- ✅ Backend funcional con todos los endpoints
- ✅ Tests BDD pasando
- ✅ Frontend consumiendo la API correctamente
- ✅ Conversiones de moneda precisas
- ✅ Totales consolidados coinciden con el Anexo

---

## 👤 Autor

Desarrollado como parte del proceso de selección para el puesto de **Desarrollador Full Stack** en Savinco.

---

## 📄 Licencia

Este proyecto es parte de un reto técnico y no está destinado para uso comercial.
