-- Add additional_context column to ai_generation_request table
-- This will store file content or other context data for AI generation

ALTER TABLE ai_generation_request 
ADD COLUMN additional_context LONGTEXT COMMENT 'File content or other context data for AI generation';
