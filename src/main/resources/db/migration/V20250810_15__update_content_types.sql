-- Migration to update content types to support only 3 types: flashcard, quiz, note
-- This migration ensures consistency across the AI Generation feature

-- Add CHECK constraints to enforce content types
ALTER TABLE ai_generation_request 
ADD CONSTRAINT chk_output_format 
CHECK (output_format IN ('flashcard', 'quiz', 'note'));

ALTER TABLE ai_prompt_template 
ADD CONSTRAINT chk_template_output_format 
CHECK (output_format IN ('flashcard', 'quiz', 'note'));

ALTER TABLE ai_generated_content 
ADD CONSTRAINT chk_content_type 
CHECK (content_type IN ('flashcard', 'quiz', 'note'));

-- Update any existing data that doesn't match the new constraints
-- Convert 'summary' to 'note' and 'blog_post' to 'note' as they are closest
UPDATE ai_generation_request 
SET output_format = 'note' 
WHERE output_format NOT IN ('flashcard', 'quiz', 'note');

UPDATE ai_prompt_template 
SET output_format = 'note' 
WHERE output_format NOT IN ('flashcard', 'quiz', 'note');

UPDATE ai_generated_content 
SET content_type = 'note' 
WHERE content_type NOT IN ('flashcard', 'quiz', 'note');

-- Add comments to clarify the supported content types
ALTER TABLE ai_generation_request 
MODIFY COLUMN output_format VARCHAR(50) NOT NULL COMMENT 'Supported: flashcard, quiz, note';

ALTER TABLE ai_prompt_template 
MODIFY COLUMN output_format VARCHAR(50) NOT NULL COMMENT 'Supported: flashcard, quiz, note';

ALTER TABLE ai_generated_content 
MODIFY COLUMN content_type VARCHAR(50) NOT NULL COMMENT 'Supported: flashcard, quiz, note';
