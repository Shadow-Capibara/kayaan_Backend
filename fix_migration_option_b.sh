#!/bin/bash

echo "🔥 แก้ไข Migration ทางเลือก B (สร้างไฟล์ใหม่) 🔥"
echo "================================================"

# ตั้งค่าสี
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# ตรวจสอบว่าอยู่ใน directory ที่ถูกต้อง
if [ ! -f "pom.xml" ]; then
    print_error "ไม่พบไฟล์ pom.xml กรุณารันสคริปต์นี้ใน directory ของ Spring Boot project"
    exit 1
fi

MIGRATION_DIR="src/main/resources/db/migration"

print_info "ขั้นตอนที่ 1: สร้างไฟล์ migration ใหม่สำหรับแก้ไข FK และ types..."

# สร้างไฟล์ migration ใหม่
cat > "$MIGRATION_DIR/V20250812_10__fix_study_group_fk_and_types.sql" << 'EOF'
-- 🔥 แก้ไข Foreign Key และ Data Types สำหรับ Study Group 🔥
-- Migration นี้จะแก้ไขปัญหาที่เกิดจาก foreign key reference ไม่ตรงกัน

-- 1) แก้ไข owner_userid ใน study_group ให้เป็น BIGINT ตรงกับ _user.id
ALTER TABLE study_group
  MODIFY owner_userid BIGINT NOT NULL;

-- 2) แก้ไข FK ของตารางทั้งหมดให้ชี้ study_group(groupid)
-- หมายเหตุ: ชื่อ constraint จะถูกสร้างโดย MySQL อัตโนมัติ

-- group_member: แก้ FK group_id → study_group(groupid)
-- ตรวจสอบ constraint name ก่อน
-- SHOW CREATE TABLE group_member;

-- ลบ FK เดิมและสร้างใหม่ (ใช้ชื่อ constraint ที่ถูกต้อง)
ALTER TABLE group_member
  DROP FOREIGN KEY group_member_ibfk_1,
  ADD CONSTRAINT fk_group_member_group_id
    FOREIGN KEY (group_id) REFERENCES study_group(groupid) ON DELETE CASCADE;

-- group_content: แก้ FK group_id → study_group(groupid)
ALTER TABLE group_content
  DROP FOREIGN KEY group_content_ibfk_1,
  ADD CONSTRAINT fk_group_content_group_id
    FOREIGN KEY (group_id) REFERENCES study_group(groupid) ON DELETE CASCADE;

-- group_invite: แก้ created_by เป็น BIGINT และ FK group_id → study_group(groupid)
ALTER TABLE group_invite
  MODIFY created_by BIGINT NOT NULL;

ALTER TABLE group_invite
  DROP FOREIGN KEY group_invite_ibfk_1,
  ADD CONSTRAINT fk_group_invite_group_id
    FOREIGN KEY (group_id) REFERENCES study_group(groupid) ON DELETE CASCADE;

-- 3) สร้าง group_message แบบถูกต้อง (ถ้ายังไม่มี)
CREATE TABLE IF NOT EXISTS group_message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_id INT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT fk_group_message_group
    FOREIGN KEY (group_id) REFERENCES study_group(groupid) ON DELETE CASCADE,
  CONSTRAINT fk_group_message_user
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

-- 4) ตรวจสอบผลลัพธ์
-- SHOW CREATE TABLE study_group;
-- SHOW CREATE TABLE group_member;
-- SHOW CREATE TABLE group_content;
-- SHOW CREATE TABLE group_invite;
-- SHOW CREATE TABLE group_message;
EOF

print_success "สร้างไฟล์ V20250812_10__fix_study_group_fk_and_types.sql"

print_info "ขั้นตอนที่ 2: สร้าง SQL script สำหรับลบของที่ค้าง..."

cat > clean_failed_migration_b.sql << 'EOF'
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

-- 5. ตรวจสอบ constraint names ที่มีอยู่
SHOW CREATE TABLE group_member;
SHOW CREATE TABLE group_content;
SHOW CREATE TABLE group_invite;
EOF

print_success "สร้างไฟล์ clean_failed_migration_b.sql"

print_info "ขั้นตอนที่ 3: สร้าง script สำหรับรัน repair..."

cat > run_repair_b.sh << 'EOF'
#!/bin/bash

echo "🛠️  รัน Flyway Repair และ Start Application (ทางเลือก B)"
echo "======================================================"

# ตั้งค่าสี
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info "ขั้นตอนที่ 1: รัน Flyway Repair"
./mvnw -Dflyway.cleanDisabled=false flyway:repair

if [ $? -eq 0 ]; then
    print_success "Flyway Repair สำเร็จ"
else
    echo "❌ Flyway Repair ล้มเหลว"
    exit 1
fi

print_info "ขั้นตอนที่ 2: รัน Application"
./mvnw spring-boot:run
EOF

chmod +x run_repair_b.sh
print_success "สร้างไฟล์ run_repair_b.sh"

print_info "ขั้นตอนที่ 4: สร้าง script ทดสอบ API..."

cat > test_api_b.sh << 'EOF'
#!/bin/bash

echo "🧪 ทดสอบ Study Group API (ทางเลือก B)"
echo "====================================="

# ตั้งค่า URL และ Token
API_BASE="http://localhost:8080/api"
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImVtYWlsIjoiZnhyZWQ3MjdAZ21haWwuY29tIiwidXNlcm5hbWUiOiJTYW0xIiwic3ViIjoiMTAzIiwiaWF0IjoxNzU0OTcxNjQ4LCJleHAiOjE3NTUwNTgwNDh9.Q0ZUF84nJzs-J1wkdwKVdo8VvoKpHMESO3mv44xGP6Q"

echo "📋 ขั้นตอนการทดสอบ:"
echo "1. สร้าง Study Group ใหม่"
echo "2. ดูรายการ My Groups"
echo "3. ตรวจสอบว่าข้อมูลถูกบันทึกในฐานข้อมูล"
echo ""

# ฟังก์ชันทดสอบ API
test_create_group() {
    echo "📝 ทดสอบสร้าง Study Group..."
    curl -i -X POST "$API_BASE/groups" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "name": "Study SE",
            "description": "notes"
        }'
    echo ""
    echo "----------------------------------------"
}

test_get_my_groups() {
    echo "📋 ทดสอบดูรายการ My Groups..."
    curl -i -H "Authorization: Bearer $TOKEN" "$API_BASE/groups/my"
    echo ""
    echo "----------------------------------------"
}

test_get_all_groups() {
    echo "📋 ทดสอบดูรายการ All Groups..."
    curl -i -H "Authorization: Bearer $TOKEN" "$API_BASE/groups"
    echo ""
    echo "----------------------------------------"
}

echo "🚀 เริ่มทดสอบ..."
test_create_group
sleep 2
test_get_my_groups
sleep 1
test_get_all_groups

echo "✅ การทดสอบเสร็จสิ้น!"
echo ""
echo "📊 ตรวจสอบผลลัพธ์:"
echo "- Status code ควรเป็น 200 หรือ 201"
echo "- Response body ควรมีข้อมูล group ที่สร้าง"
echo "- ข้อมูลควรถูกบันทึกในฐานข้อมูล"
echo ""
echo "🔍 ถ้าทดสอบผ่าน ให้กลับไปหน้า FE และทดสอบฟลว์ Create อีกครั้ง"
EOF

chmod +x test_api_b.sh
print_success "สร้างไฟล์ test_api_b.sh"

print_info "ขั้นตอนที่ 5: สร้าง script ตรวจสอบ constraint names..."

cat > check_constraints.sql << 'EOF'
-- 🔍 ตรวจสอบ Constraint Names ที่มีอยู่ 🔍
-- รันคำสั่งนี้เพื่อดูชื่อ constraint ที่ถูกต้องก่อนแก้ไข

-- ตรวจสอบ constraint names ของตารางต่างๆ
SHOW CREATE TABLE group_member;
SHOW CREATE TABLE group_content;
SHOW CREATE TABLE group_invite;

-- ดู foreign key constraints ทั้งหมด
SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE REFERENCED_TABLE_SCHEMA = 'kayaan_db' 
  AND TABLE_NAME IN ('group_member', 'group_content', 'group_invite')
ORDER BY TABLE_NAME, CONSTRAINT_NAME;
EOF

print_success "สร้างไฟล์ check_constraints.sql"

# แสดงสรุป
echo ""
echo "🎯 สรุปการแก้ไข (ทางเลือก B):"
echo "=============================="
print_success "✅ สร้างไฟล์ migration ใหม่: V20250812_10__fix_study_group_fk_and_types.sql"
print_success "✅ สร้างไฟล์ SQL สำหรับลบของที่ค้าง: clean_failed_migration_b.sql"
print_success "✅ สร้างไฟล์ script สำหรับรัน repair: run_repair_b.sh"
print_success "✅ สร้างไฟล์ script ทดสอบ API: test_api_b.sh"
print_success "✅ สร้างไฟล์ตรวจสอบ constraint names: check_constraints.sql"

echo ""
echo "📋 ขั้นตอนต่อไป:"
echo "================"
echo "1. ตรวจสอบ constraint names ที่มีอยู่:"
echo "   mysql -h localhost -P 3307 -u root -p kayaan_db"
echo "   source check_constraints.sql"
echo ""
echo "2. แก้ไขชื่อ constraint ในไฟล์ V20250812_10__fix_study_group_fk_and_types.sql"
echo "   ให้ตรงกับผลลัพธ์จากขั้นตอนที่ 1"
echo ""
echo "3. ลบของที่ค้าง:"
echo "   source clean_failed_migration_b.sql"
echo ""
echo "4. รัน repair และ start application:"
echo "   ./run_repair_b.sh"
echo ""
echo "5. ทดสอบ API:"
echo "   ./test_api_b.sh"
echo ""
print_success "🎉 เสร็จสิ้น! กรุณาทำตามขั้นตอนข้างต้นครับ"
