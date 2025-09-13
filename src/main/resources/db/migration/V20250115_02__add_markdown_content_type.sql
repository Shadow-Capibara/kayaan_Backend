-- Add MARKDOWN to content_type enum
ALTER TABLE group_posts MODIFY COLUMN content_type ENUM('TEXT', 'IMAGE', 'FILE', 'MIXED', 'MARKDOWN') NOT NULL;
