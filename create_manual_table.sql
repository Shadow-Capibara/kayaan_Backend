-- Create manual_generated_content table manually
-- Execute this in your MySQL database

USE kayaan_db;

CREATE TABLE IF NOT EXISTS manual_generated_content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content_title VARCHAR(255) NOT NULL,
    content_type ENUM('FLASHCARD', 'QUIZ', 'NOTE') NOT NULL,
    content_data JSON NOT NULL COMMENT 'Manual content stored in JSON format',
    content_version INT DEFAULT 1 COMMENT 'Version number for content updates',
    subject VARCHAR(100) NULL COMMENT 'Subject/topic of the content',
    difficulty VARCHAR(50) NULL COMMENT 'Difficulty level (easy, medium, hard)',
    tags TEXT NULL COMMENT 'Comma-separated tags',
    is_saved BOOLEAN DEFAULT TRUE COMMENT 'Whether content is saved (always true for manual)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL COMMENT 'Soft delete timestamp',
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
    
    -- Indexes for better performance
    INDEX idx_user_content_type (user_id, content_type),
    INDEX idx_created_at (created_at),
    INDEX idx_subject (subject),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT = 'Manual generation content stored in JSON format for consistency with AI generation';

-- Insert into flyway_schema_history to mark migration as complete
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES (
    (SELECT COALESCE(MAX(installed_rank), 0) + 1 FROM flyway_schema_history f),
    '20250831.01',
    'create manual generated content',
    'SQL',
    'V20250831_01__create_manual_generated_content.sql',
    NULL,
    'manual',
    0,
    1
);

-- Verify table creation
SELECT 'Table created successfully' as status;
SHOW CREATE TABLE manual_generated_content;
