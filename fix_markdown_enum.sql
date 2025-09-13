-- Fix content_type enum to include MARKDOWN
USE kayaan_backend;

-- Check current enum values
SHOW COLUMNS FROM group_posts LIKE 'content_type';

-- Update enum to include MARKDOWN
ALTER TABLE group_posts MODIFY COLUMN content_type ENUM('TEXT', 'IMAGE', 'FILE', 'MIXED', 'MARKDOWN') NOT NULL;

-- Verify the change
SHOW COLUMNS FROM group_posts LIKE 'content_type';
