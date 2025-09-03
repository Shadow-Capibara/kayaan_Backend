-- Script to repair Flyway schema history
-- Run this manually in database to fix failed migration

USE kayaan_db;

-- Check current flyway_schema_history
SELECT * FROM flyway_schema_history WHERE success = 0;

-- Delete failed migration record for version 20250810.11
DELETE FROM flyway_schema_history WHERE version = '20250810.11' AND success = 0;

-- Verify deletion
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;

-- After running this script, restart Spring Boot with Flyway enabled
