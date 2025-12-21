-- Add content type and content data fields to group_content table
-- Check and add content_type column if it doesn't exist
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'group_content' 
  AND COLUMN_NAME = 'content_type';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE group_content ADD COLUMN content_type VARCHAR(50)', 
    'SELECT 1 AS skip');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add content_data column if it doesn't exist
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'group_content' 
  AND COLUMN_NAME = 'content_data';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE group_content ADD COLUMN content_data LONGTEXT', 
    'SELECT 1 AS skip');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
