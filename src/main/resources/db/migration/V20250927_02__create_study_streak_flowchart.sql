-- Create study_streak table for Study Streak functionality
-- Implements flowchart logic with Freezing Count system

CREATE TABLE IF NOT EXISTS study_streak (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    streak_count INT DEFAULT 0 NOT NULL,
    last_activity_time TIMESTAMP NULL,
    freezing_count INT DEFAULT 0 NOT NULL,
    last_freeze_date DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_last_activity_time (last_activity_time),
    INDEX idx_freezing_count (freezing_count),
    INDEX idx_last_freeze_date (last_freeze_date)
);
