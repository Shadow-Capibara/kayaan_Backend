-- Add content type and content data fields to group_content table
ALTER TABLE group_content 
ADD COLUMN content_type VARCHAR(50),
ADD COLUMN content_data LONGTEXT;
