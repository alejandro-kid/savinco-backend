# Database Scripts

## Migraciones Flyway

El proyecto utiliza **Flyway** para gestionar las migraciones de base de datos. Las migraciones se encuentran en `src/main/resources/db/migration/` y se ejecutan manualmente.

**Migraciones disponibles:**

- `V1__create_financial_data_table.sql` - Crea la tabla inicial de datos financieros
- `V2__create_currencies_table.sql` - Crea la tabla de monedas
- `V3__create_countries_table.sql` - Crea la tabla de países
- `V4__migrate_enum_data_to_tables.sql` - Migra datos iniciales de enums a tablas
- `V5__add_foreign_keys_to_financial_data.sql` - Agrega foreign keys a financial_data
- `V6__increase_exchange_rate_precision.sql` - Aumenta precisión de tasas de cambio

**Ejecutar migraciones:**

```bash
# Aplicar todas las migraciones pendientes
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/savinco_financial \
  -Dflyway.user=postgres \
  -Dflyway.password=your_password

# Ver estado de migraciones
mvn flyway:info \
  -Dflyway.url=jdbc:postgresql://localhost:5432/savinco_financial \
  -Dflyway.user=postgres \
  -Dflyway.password=your_password
```

**Nota:** Las migraciones NO se ejecutan automáticamente al iniciar el backend. Deben aplicarse manualmente antes de iniciar la aplicación.

---

## create_schema.sql

Script SQL para crear el esquema de la base de datos manualmente (alternativa a Flyway).

**Uso:**

```bash
psql -U postgres -d savinco_financial -f create_schema.sql
```

**Nota:** Este script se ejecuta automáticamente cuando se usa docker-compose y la base de datos se crea por primera vez (volumen vacío).

---

## seed_financial_data.sql

Script SQL para poblar la base de datos con datos ficticios pero válidos para desarrollo y testing.

**Contenido:**

- Datos financieros para los 4 países soportados:
  - **ECU** (Ecuador) con moneda **USD**
  - **ESP** (España) con moneda **EUR**
  - **PER** (Perú) con moneda **PEN**
  - **NPL** (Nepal) con moneda **NPR**

**Características:**

- Usa `ON CONFLICT DO UPDATE` para evitar duplicados (idempotente)
- Valores realistas de capital ahorrado, capital prestado y ganancias generadas
- Actualiza automáticamente los timestamps
- **Importante:** Requiere que las tablas `currencies` y `countries` ya existan (ejecutar migraciones Flyway primero)

**Uso:**

```bash
# Opción 1: Ejecutar directamente con psql
psql -U postgres -d savinco_financial -f src/main/resources/db/scripts/seed_financial_data.sql

# Opción 2: Desde el directorio del proyecto
psql -U postgres -d savinco_financial -f src/main/resources/db/scripts/seed_financial_data.sql

# Opción 3: Con docker-compose (si la BD está en un contenedor)
docker exec -i financial-postgres psql -U postgres -d savinco_financial < src/main/resources/db/scripts/seed_financial_data.sql
```

**Nota:** Este script es idempotente - puedes ejecutarlo múltiples veces sin problemas. Si ya existen datos para un país, los actualizará en lugar de crear duplicados.

**Prerequisitos:** Asegúrate de haber ejecutado las migraciones Flyway (V1-V6) antes de ejecutar este script, ya que requiere que las tablas `currencies` y `countries` existan.
