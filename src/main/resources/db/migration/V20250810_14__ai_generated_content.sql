CREATE TABLE IF NOT EXISTS ai_generated_content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    generation_request_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    content_title VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL, -- e.g., "flashcard", "quiz", "note", "summary"
    content_data JSON NOT NULL, -- AI-generated content in JSON format
    content_version INT DEFAULT 1,
    supabase_file_path VARCHAR(500), -- Path to JSON file in Supabase Storage
    file_size BIGINT, -- File size in bytes
    is_saved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (generation_request_id) REFERENCES ai_generation_request(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
    INDEX idx_user_saved (user_id, is_saved),
    INDEX idx_request_version (generation_request_id, content_version),
    INDEX idx_content_type (content_type)
);
