-- สร้าง table manual_generated_content สำหรับ JSON storage
-- รัน script นี้ใน phpMyAdmin

USE kayaan_db;

-- สร้าง table
CREATE TABLE IF NOT EXISTS manual_generated_content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content_title VARCHAR(255) NOT NULL,
    content_type ENUM('FLASHCARD', 'QUIZ', 'NOTE') NOT NULL,
    content_data JSON NOT NULL COMMENT 'Manual content stored in JSON format',
    subject VARCHAR(100) NULL,
    difficulty VARCHAR(50) NULL,
    tags TEXT NULL COMMENT 'Comma-separated tags',
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
COMMENT = 'Manual generation content stored in JSON format';

-- อัปเดต flyway_schema_history เพื่อบันทึกว่า migration สำเร็จแล้ว
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

-- ตรวจสอบว่าสร้างสำเร็จ
SELECT 'Table manual_generated_content created successfully!' as result;
SHOW CREATE TABLE manual_generated_content;
