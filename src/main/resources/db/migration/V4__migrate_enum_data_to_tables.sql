-- Migration: Migrate enum data to tables
-- Description: Inserts initial currency and country data from enums into database tables
-- Author: System
-- Date: 2025-12-14

-- Insert currencies
-- Exchange rates are based on original CurrencyConverterService values:
-- EUR: 1 EUR = 1.111111... USD (from 1 USD = 0.90 EUR)
-- PEN: 1 PEN = 0.303030... USD (from 1 USD = 3.3 PEN)
-- NPR: 1 NPR = 0.007518... USD (from 1 USD = 133 NPR)
-- USD: Base currency, rate = 1.00

INSERT INTO currencies (code, name, is_base, exchange_rate_to_base, created_at, updated_at) VALUES
('USD', 'US Dollar', TRUE, 1.000000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('EUR', 'Euro', FALSE, 1.111111111111111111, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PEN', 'Peruvian Sol', FALSE, 0.303030303030303030, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NPR', 'Nepalese Rupee', FALSE, 0.007518796992481203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert countries
-- Note: currency_id references are resolved by code lookup since we just inserted them
-- We use subqueries to get the currency IDs

INSERT INTO countries (code, name, currency_id, created_at, updated_at) VALUES
('ECU', 'Ecuador', (SELECT id FROM currencies WHERE code = 'USD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ESP', 'España', (SELECT id FROM currencies WHERE code = 'EUR'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PER', 'Perú', (SELECT id FROM currencies WHERE code = 'PEN'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NPL', 'Nepal', (SELECT id FROM currencies WHERE code = 'NPR'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Verify data was inserted correctly
DO $$
DECLARE
    currency_count INTEGER;
    country_count INTEGER;
    base_currency_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO currency_count FROM currencies;
    SELECT COUNT(*) INTO country_count FROM countries;
    SELECT COUNT(*) INTO base_currency_count FROM currencies WHERE is_base = TRUE;
    
    IF currency_count != 4 THEN
        RAISE EXCEPTION 'Expected 4 currencies, found %', currency_count;
    END IF;
    
    IF country_count != 4 THEN
        RAISE EXCEPTION 'Expected 4 countries, found %', country_count;
    END IF;
    
    IF base_currency_count != 1 THEN
        RAISE EXCEPTION 'Expected 1 base currency, found %', base_currency_count;
    END IF;
END $$;

