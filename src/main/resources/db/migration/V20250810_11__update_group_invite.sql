-- Migration สำหรับปรับปรุงตาราง group_invite
-- เพิ่มฟิลด์ความปลอดภัยสำหรับระบบรหัสเชิญ

-- เพิ่มฟิลด์ใหม่
ALTER TABLE group_invite 
ADD COLUMN IF NOT EXISTS max_uses INTEGER,
ADD COLUMN IF NOT EXISTS current_uses INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS created_by_ip VARCHAR(45),
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS invite_code VARCHAR(50) UNIQUE;

-- สร้าง index สำหรับ invite_code
CREATE INDEX IF NOT EXISTS idx_group_invite_invite_code ON group_invite(invite_code);

-- สร้าง index สำหรับการค้นหาที่รวดเร็ว
CREATE INDEX IF NOT EXISTS idx_group_invite_active_expired ON group_invite(is_active, expires_at);
CREATE INDEX IF NOT EXISTS idx_group_invite_group_active ON group_invite(group_id, is_active);

-- เพิ่ม constraints
ALTER TABLE group_invite 
ADD CONSTRAINT chk_max_uses CHECK (max_uses IS NULL OR max_uses > 0),
ADD CONSTRAINT chk_current_uses CHECK (current_uses >= 0),
ADD CONSTRAINT chk_current_uses_limit CHECK (max_uses IS NULL OR current_uses <= max_uses);

-- อัปเดตข้อมูลที่มีอยู่
UPDATE group_invite 
SET invite_code = token 
WHERE invite_code IS NULL;

-- เพิ่ม comments สำหรับฟิลด์ใหม่
COMMENT ON COLUMN group_invite.max_uses IS 'จำนวนครั้งสูงสุดที่ใช้รหัสเชิญได้ (NULL = ไม่จำกัด)';
COMMENT ON COLUMN group_invite.current_uses IS 'จำนวนครั้งที่ใช้รหัสเชิญไปแล้ว';
COMMENT ON COLUMN group_invite.created_by_ip IS 'IP address ของผู้สร้างรหัสเชิญ';
COMMENT ON COLUMN group_invite.is_active IS 'สถานะการใช้งานของรหัสเชิญ';
COMMENT ON COLUMN group_invite.invite_code IS 'รหัสเชิญที่ปลอดภัย (unique)';
