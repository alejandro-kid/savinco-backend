-- Migration: Create countries table
-- Description: Creates the countries table to store country information and their associated currencies
-- Author: System
-- Date: 2025-12-14

CREATE TABLE countries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL,
    name VARCHAR(100) NOT NULL,
    currency_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_countries_code UNIQUE (code),
    CONSTRAINT fk_countries_currency FOREIGN KEY (currency_id) REFERENCES currencies(id)
);

-- Create index on code for faster lookups
CREATE INDEX idx_countries_code ON countries(code);

-- Create index on currency_id for joins
CREATE INDEX idx_countries_currency_id ON countries(currency_id);

-- Add comments
COMMENT ON TABLE countries IS 'Stores country information and their associated currencies';
COMMENT ON COLUMN countries.code IS 'ISO country code (ECU, ESP, PER, NPL)';
COMMENT ON COLUMN countries.name IS 'Country name (e.g., Ecuador, España)';
COMMENT ON COLUMN countries.currency_id IS 'Foreign key to currencies table. Each country has one currency.';
COMMENT ON COLUMN countries.created_at IS 'Timestamp when country was created';
COMMENT ON COLUMN countries.updated_at IS 'Timestamp when country was last updated';

