-- Migration สำหรับตาราง content_audit_log
-- สำหรับเก็บประวัติการเข้าถึงและเปลี่ยนแปลงเนื้อหาในกลุ่มเรียน

CREATE TABLE IF NOT EXISTS content_audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    details TEXT,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Foreign key constraints
    CONSTRAINT fk_content_audit_log_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_audit_log_content FOREIGN KEY (content_id) REFERENCES group_content(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_audit_log_group FOREIGN KEY (group_id) REFERENCES study_group(id) ON DELETE CASCADE,
    
    -- Indexes สำหรับการค้นหาที่รวดเร็ว
    CONSTRAINT chk_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'VIEW', 'DOWNLOAD', 'UPLOAD', 'EDIT', 'SHARE'))
);

-- สร้าง indexes สำหรับการค้นหาที่รวดเร็ว
CREATE INDEX IF NOT EXISTS idx_content_audit_log_user_id ON content_audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_content_audit_log_content_id ON content_audit_log(content_id);
CREATE INDEX IF NOT EXISTS idx_content_audit_log_group_id ON content_audit_log(group_id);
CREATE INDEX IF NOT EXISTS idx_content_audit_log_action ON content_audit_log(action);
CREATE INDEX IF NOT EXISTS idx_content_audit_log_timestamp ON content_audit_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_content_audit_log_user_group ON content_audit_log(user_id, group_id);
CREATE INDEX IF NOT EXISTS idx_content_audit_log_content_timestamp ON content_audit_log(content_id, timestamp);

-- เพิ่ม comments สำหรับตาราง
COMMENT ON TABLE content_audit_log IS 'ตารางสำหรับเก็บประวัติการเข้าถึงและเปลี่ยนแปลงเนื้อหาในกลุ่มเรียน';
COMMENT ON COLUMN content_audit_log.id IS 'Primary key ของตาราง';
COMMENT ON COLUMN content_audit_log.user_id IS 'ID ของผู้ใช้ที่ทำการกระทำ';
COMMENT ON COLUMN content_audit_log.content_id IS 'ID ของเนื้อหาที่เกี่ยวข้อง';
COMMENT ON COLUMN content_audit_log.group_id IS 'ID ของกลุ่มที่เกี่ยวข้อง';
COMMENT ON COLUMN content_audit_log.action IS 'ประเภทการกระทำ (CREATE, UPDATE, DELETE, VIEW, DOWNLOAD, UPLOAD, EDIT, SHARE)';
COMMENT ON COLUMN content_audit_log.timestamp IS 'เวลาที่ทำการกระทำ';
COMMENT ON COLUMN content_audit_log.ip_address IS 'IP address ของผู้ใช้';
COMMENT ON COLUMN content_audit_log.user_agent IS 'User agent ของ browser';
COMMENT ON COLUMN content_audit_log.details IS 'รายละเอียดเพิ่มเติมของการกระทำ';
COMMENT ON COLUMN content_audit_log.success IS 'สถานะการดำเนินการ (สำเร็จหรือไม่)';
