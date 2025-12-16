-- Migration: Increase exchange_rate_to_base precision
-- Description: Increases the precision of exchange_rate_to_base column from NUMERIC(19,6) to NUMERIC(19,10) to support more precise exchange rates
-- Author: Auto-generated
-- Date: 2025-12-16

-- Increase precision of exchange_rate_to_base column
ALTER TABLE currencies 
ALTER COLUMN exchange_rate_to_base TYPE NUMERIC(19,10);

-- Update comment
COMMENT ON COLUMN currencies.exchange_rate_to_base IS 'Exchange rate to convert 1 unit of this currency to base currency (USD). Base currency rate is always 1.00. Precision increased to 10 decimal places for accurate conversions.';
