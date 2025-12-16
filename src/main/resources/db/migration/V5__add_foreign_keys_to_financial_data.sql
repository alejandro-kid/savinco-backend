-- Migration: Add foreign keys to financial_data table
-- Description: Updates financial_data table to use foreign keys to countries and currencies tables
-- Author: System
-- Date: 2025-12-14

-- Step 1: Add new columns for foreign keys (nullable initially)
ALTER TABLE financial_data
ADD COLUMN country_id BIGINT,
ADD COLUMN currency_id BIGINT;

-- Step 2: Migrate existing data from codes to IDs
-- Update country_id based on country_code
UPDATE financial_data fd
SET country_id = (
    SELECT c.id 
    FROM countries c 
    WHERE c.code = fd.country_code
)
WHERE fd.country_code IS NOT NULL;

-- Update currency_id based on currency_code
UPDATE financial_data fd
SET currency_id = (
    SELECT cu.id 
    FROM currencies cu 
    WHERE cu.code = fd.currency_code
)
WHERE fd.currency_code IS NOT NULL;

-- Step 3: Verify all rows were migrated
DO $$
DECLARE
    unmigrated_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO unmigrated_count 
    FROM financial_data 
    WHERE country_id IS NULL OR currency_id IS NULL;
    
    IF unmigrated_count > 0 THEN
        RAISE EXCEPTION 'Found % rows with unmigrated foreign keys', unmigrated_count;
    END IF;
END $$;

-- Step 4: Make columns NOT NULL
ALTER TABLE financial_data
ALTER COLUMN country_id SET NOT NULL,
ALTER COLUMN currency_id SET NOT NULL;

-- Step 5: Add foreign key constraints
ALTER TABLE financial_data
ADD CONSTRAINT fk_financial_data_country 
    FOREIGN KEY (country_id) REFERENCES countries(id),
ADD CONSTRAINT fk_financial_data_currency 
    FOREIGN KEY (currency_id) REFERENCES currencies(id);

-- Step 6: Create indexes on foreign keys for better join performance
CREATE INDEX idx_financial_data_country_id ON financial_data(country_id);
CREATE INDEX idx_financial_data_currency_id ON financial_data(currency_id);

-- Step 7: Drop old columns (country_code and currency_code)
-- Note: We keep them for now to maintain backward compatibility during transition
-- They will be removed in a future migration after all code is updated
-- ALTER TABLE financial_data DROP COLUMN country_code;
-- ALTER TABLE financial_data DROP COLUMN currency_code;

-- Add comments
COMMENT ON COLUMN financial_data.country_id IS 'Foreign key to countries table';
COMMENT ON COLUMN financial_data.currency_id IS 'Foreign key to currencies table';

