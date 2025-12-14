# Database Scripts

## create_schema.sql

Script SQL para crear el esquema de la base de datos manualmente.

**Uso:**

```bash
psql -U postgres -d savinco_financial -f create_schema.sql
```

**Nota:** Este script se ejecuta automáticamente cuando se usa docker-compose y la base de datos se crea por primera vez (volumen vacío).
