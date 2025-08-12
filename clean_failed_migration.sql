-- 🔥 ลบของที่ค้างจาก Flyway Migration ที่ fail 🔥
-- รันคำสั่งนี้ใน MySQL เพื่อลบ row ที่ fail และตารางที่สร้างค้าง

-- 1. ดูสถานะ history ก่อนลบ
SELECT installed_rank, version, description, script, checksum, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- 2. ลบ row ของเวอร์ชันที่ fail (success=0) = 20250810.05
DELETE FROM flyway_schema_history WHERE version = '20250810.05';

-- 3. ลบออบเจกต์ที่สร้างค้าง (ถ้ามี)
DROP TABLE IF EXISTS group_message;

-- 4. ดูสถานะ history หลังลบ
SELECT installed_rank, version, description, script, checksum, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- 5. ตรวจสอบว่าตารางที่เกี่ยวข้องยังอยู่ครบ
SHOW TABLES LIKE '%group%';
SHOW TABLES LIKE '%user%';
