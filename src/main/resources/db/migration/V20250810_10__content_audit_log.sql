-- Migration สำหรับตาราง content_audit_log
-- สำหรับเก็บประวัติการเข้าถึงและเปลี่ยนแปลงเนื้อหาในกลุ่มเรียน

CREATE TABLE IF NOT EXISTS content_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content_id BIGINT NOT NULL,
    group_id INT NOT NULL,
    action ENUM('CREATE', 'UPDATE', 'DELETE', 'VIEW', 'DOWNLOAD', 'UPLOAD', 'EDIT', 'SHARE') NOT NULL,
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    details TEXT,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Foreign key constraints
    CONSTRAINT fk_content_audit_log_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_audit_log_content FOREIGN KEY (content_id) REFERENCES group_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_audit_log_group FOREIGN KEY (group_id) REFERENCES study_group(groupid) ON DELETE CASCADE
);

-- สร้าง indexes สำหรับการค้นหาที่รวดเร็ว
CREATE INDEX idx_content_audit_log_user_id ON content_audit_log(user_id);
CREATE INDEX idx_content_audit_log_content_id ON content_audit_log(content_id);
CREATE INDEX idx_content_audit_log_group_id ON content_audit_log(group_id);
CREATE INDEX idx_content_audit_log_action ON content_audit_log(action);
CREATE INDEX idx_content_audit_log_timestamp ON content_audit_log(`timestamp`);
CREATE INDEX idx_content_audit_log_user_group ON content_audit_log(user_id, group_id);
CREATE INDEX idx_content_audit_log_content_timestamp ON content_audit_log(content_id, `timestamp`);

-- เพิ่ม comments สำหรับตาราง
-- Comments omitted for MySQL compatibility
