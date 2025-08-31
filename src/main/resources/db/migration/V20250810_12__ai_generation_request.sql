CREATE TABLE IF NOT EXISTS ai_generation_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    prompt_text TEXT NOT NULL,
    output_format VARCHAR(50) NOT NULL, -- e.g., "flashcard", "quiz", "note", "summary"
    status VARCHAR(50) NOT NULL DEFAULT 'pending', -- pending, processing, completed, failed, cancelled
    progress INT DEFAULT 0, -- 0-100
    error_message TEXT,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_status_created (status, created_at)
);
