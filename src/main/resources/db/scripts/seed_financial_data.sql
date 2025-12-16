-- Script: Seed Financial Data
-- Description: Inserts fictional but valid financial data for all supported countries
-- Author: System
-- Date: 2025-12-15
--
-- This script populates the financial_data table with realistic sample data
-- for testing and development purposes.
--
-- Countries and their currencies:
-- - ECU (Ecuador) -> USD (US Dollar)
-- - ESP (España) -> EUR (Euro)
-- - PER (Perú) -> PEN (Peruvian Sol)
-- - NPL (Nepal) -> NPR (Nepalese Rupee)

-- Clear existing data (optional - comment out if you want to keep existing data)
-- TRUNCATE TABLE financial_data RESTART IDENTITY CASCADE;

-- Insert financial data for Ecuador (USD)
INSERT INTO financial_data (
    country_code,
    currency_code,
    capital_saved,
    capital_loaned,
    profits_generated,
    created_at,
    updated_at
) VALUES (
    'ECU',
    'USD',
    1250000.00,    -- $1.25M capital saved
    850000.00,     -- $850K capital loaned
    125000.00,     -- $125K profits generated
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (country_code) DO UPDATE SET
    currency_code = EXCLUDED.currency_code,
    capital_saved = EXCLUDED.capital_saved,
    capital_loaned = EXCLUDED.capital_loaned,
    profits_generated = EXCLUDED.profits_generated,
    updated_at = CURRENT_TIMESTAMP;

-- Insert financial data for España (EUR)
INSERT INTO financial_data (
    country_code,
    currency_code,
    capital_saved,
    capital_loaned,
    profits_generated,
    created_at,
    updated_at
) VALUES (
    'ESP',
    'EUR',
    2100000.00,    -- €2.1M capital saved
    1500000.00,    -- €1.5M capital loaned
    210000.00,     -- €210K profits generated
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (country_code) DO UPDATE SET
    currency_code = EXCLUDED.currency_code,
    capital_saved = EXCLUDED.capital_saved,
    capital_loaned = EXCLUDED.capital_loaned,
    profits_generated = EXCLUDED.profits_generated,
    updated_at = CURRENT_TIMESTAMP;

-- Insert financial data for Perú (PEN)
INSERT INTO financial_data (
    country_code,
    currency_code,
    capital_saved,
    capital_loaned,
    profits_generated,
    created_at,
    updated_at
) VALUES (
    'PER',
    'PEN',
    3500000.00,    -- S/ 3.5M capital saved
    2800000.00,    -- S/ 2.8M capital loaned
    350000.00,     -- S/ 350K profits generated
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (country_code) DO UPDATE SET
    currency_code = EXCLUDED.currency_code,
    capital_saved = EXCLUDED.capital_saved,
    capital_loaned = EXCLUDED.capital_loaned,
    profits_generated = EXCLUDED.profits_generated,
    updated_at = CURRENT_TIMESTAMP;

-- Insert financial data for Nepal (NPR)
INSERT INTO financial_data (
    country_code,
    currency_code,
    capital_saved,
    capital_loaned,
    profits_generated,
    created_at,
    updated_at
) VALUES (
    'NPL',
    'NPR',
    45000000.00,   -- रू 45M capital saved (Nepalese Rupees)
    32000000.00,   -- रू 32M capital loaned
    4500000.00,    -- रू 4.5M profits generated
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (country_code) DO UPDATE SET
    currency_code = EXCLUDED.currency_code,
    capital_saved = EXCLUDED.capital_saved,
    capital_loaned = EXCLUDED.capital_loaned,
    profits_generated = EXCLUDED.profits_generated,
    updated_at = CURRENT_TIMESTAMP;

-- Verify inserted data
SELECT 
    id,
    country_code,
    currency_code,
    capital_saved,
    capital_loaned,
    profits_generated,
    created_at,
    updated_at
FROM financial_data
ORDER BY country_code;

-- Summary statistics
SELECT 
    COUNT(*) as total_countries,
    SUM(capital_saved) as total_capital_saved,
    SUM(capital_loaned) as total_capital_loaned,
    SUM(profits_generated) as total_profits_generated
FROM financial_data;

