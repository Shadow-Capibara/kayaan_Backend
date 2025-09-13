-- Add MARKDOWN to content_type enum for group_posts table
ALTER TABLE group_posts MODIFY COLUMN content_type ENUM('TEXT', 'IMAGE', 'FILE', 'MIXED', 'MARKDOWN') NOT NULL;
