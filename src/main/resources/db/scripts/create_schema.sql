-- SQL Script to create financial_data table manually
-- Use this script if you prefer to create the schema manually instead of using Hibernate DDL
-- Execute this script in your PostgreSQL database before running the application

-- Create database (if it doesn't exist)
-- CREATE DATABASE savinco_financial;

-- Connect to the database
-- \c savinco_financial;

-- Create table
CREATE TABLE IF NOT EXISTS financial_data (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    capital_saved NUMERIC(19,2) NOT NULL,
    capital_loaned NUMERIC(19,2) NOT NULL,
    profits_generated NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_financial_data_country UNIQUE (country_code)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_financial_data_country_code ON financial_data(country_code);

-- Add comments
COMMENT ON TABLE financial_data IS 'Stores financial data (capital saved, loaned, profits) by country with original currency';
COMMENT ON COLUMN financial_data.country_code IS 'ISO country code (ECU, ESP, PER, NPL)';
COMMENT ON COLUMN financial_data.currency_code IS 'ISO currency code (USD, EUR, PEN, NPR)';
COMMENT ON COLUMN financial_data.capital_saved IS 'Capital saved in original currency';
COMMENT ON COLUMN financial_data.capital_loaned IS 'Capital loaned in original currency';
COMMENT ON COLUMN financial_data.profits_generated IS 'Profits generated in original currency';
