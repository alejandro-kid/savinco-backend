-- Migration: Create currencies table
-- Description: Creates the currencies table to store currency information and exchange rates
-- Author: System
-- Date: 2025-12-14

CREATE TABLE currencies (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_base BOOLEAN NOT NULL DEFAULT FALSE,
    exchange_rate_to_base NUMERIC(19,6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_currencies_code UNIQUE (code)
);

-- Create index on code for faster lookups
CREATE INDEX idx_currencies_code ON currencies(code);

-- Create index on is_base for finding base currency
CREATE INDEX idx_currencies_is_base ON currencies(is_base);

-- Constraint: Only one currency can be base (USD)
-- This will be enforced by application logic, but we add a partial unique index
CREATE UNIQUE INDEX idx_currencies_base_unique ON currencies(is_base) WHERE is_base = TRUE;

-- Add comments
COMMENT ON TABLE currencies IS 'Stores currency information and exchange rates to base currency (USD)';
COMMENT ON COLUMN currencies.code IS 'ISO currency code (USD, EUR, PEN, NPR)';
COMMENT ON COLUMN currencies.name IS 'Currency name (e.g., US Dollar, Euro)';
COMMENT ON COLUMN currencies.is_base IS 'Indicates if this is the base currency (USD). Only one currency can be base.';
COMMENT ON COLUMN currencies.exchange_rate_to_base IS 'Exchange rate to convert 1 unit of this currency to base currency (USD). Base currency rate is always 1.00';
COMMENT ON COLUMN currencies.created_at IS 'Timestamp when currency was created';
COMMENT ON COLUMN currencies.updated_at IS 'Timestamp when currency was last updated';

