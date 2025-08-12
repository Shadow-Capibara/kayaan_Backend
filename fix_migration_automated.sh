#!/bin/bash

echo "🔥 แก้ไขปัญหา Flyway Migration อัตโนมัติ 🔥"
echo "=========================================="

# ตั้งค่าสี
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ฟังก์ชันแสดงข้อความ
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

print_info "ตรวจสอบโครงสร้างไฟล์ migration..."

# ตรวจสอบไฟล์ migration ที่มีอยู่
MIGRATION_DIR="src/main/resources/db/migration"
if [ ! -d "$MIGRATION_DIR" ]; then
    print_error "ไม่พบ directory migration: $MIGRATION_DIR"
    exit 1
fi

# สร้าง backup directory
BACKUP_DIR="migration_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

print_info "สร้าง backup ที่: $BACKUP_DIR"

# Backup ไฟล์ migration ทั้งหมด
cp -r "$MIGRATION_DIR"/* "$BACKUP_DIR/"

print_success "Backup เสร็จสิ้น"

# ขั้นตอนที่ 1: แก้ไขลำดับไฟล์ migration
print_info "ขั้นตอนที่ 1: แก้ไขลำดับไฟล์ migration..."

# ตรวจสอบไฟล์ที่มีอยู่
FILES_TO_CHECK=(
    "V20250810_05__group_message.sql"
    "V20250810_06__study_group.sql"
    "V20250810_07__group_member.sql"
    "V20250810_08__group_content.sql"
    "V20250810_09__group_invite.sql"
)

for file in "${FILES_TO_CHECK[@]}"; do
    if [ ! -f "$MIGRATION_DIR/$file" ]; then
        print_warning "ไม่พบไฟล์: $file"
    else
        print_info "พบไฟล์: $file"
    fi
done

# ย้ายไฟล์ตามลำดับที่ถูกต้อง
print_info "กำลังย้ายไฟล์..."

# ย้ายไฟล์ 06 ไปเป็น 05 (สร้าง study_group ก่อน)
if [ -f "$MIGRATION_DIR/V20250810_06__study_group.sql" ]; then
    mv "$MIGRATION_DIR/V20250810_06__study_group.sql" "$MIGRATION_DIR/V20250810_05__study_group.sql"
    print_success "ย้าย V20250810_06__study_group.sql → V20250810_05__study_group.sql"
fi

# ย้ายไฟล์ 07 ไปเป็น 06
if [ -f "$MIGRATION_DIR/V20250810_07__group_member.sql" ]; then
    mv "$MIGRATION_DIR/V20250810_07__group_member.sql" "$MIGRATION_DIR/V20250810_06__group_member.sql"
    print_success "ย้าย V20250810_07__group_member.sql → V20250810_06__group_member.sql"
fi

# ย้ายไฟล์ 08 ไปเป็น 07
if [ -f "$MIGRATION_DIR/V20250810_08__group_content.sql" ]; then
    mv "$MIGRATION_DIR/V20250810_08__group_content.sql" "$MIGRATION_DIR/V20250810_07__group_content.sql"
    print_success "ย้าย V20250810_08__group_content.sql → V20250810_07__group_content.sql"
fi

# ย้ายไฟล์ 09 ไปเป็น 08
if [ -f "$MIGRATION_DIR/V20250810_09__group_invite.sql" ]; then
    mv "$MIGRATION_DIR/V20250810_09__group_invite.sql" "$MIGRATION_DIR/V20250810_08__group_invite.sql"
    print_success "ย้าย V20250810_09__group_invite.sql → V20250810_08__group_invite.sql"
fi

# ย้ายไฟล์ 05 ไปเป็น 09 (สร้าง group_message ทีหลัง)
if [ -f "$MIGRATION_DIR/V20250810_05__group_message.sql" ]; then
    mv "$MIGRATION_DIR/V20250810_05__group_message.sql" "$MIGRATION_DIR/V20250810_09__group_message.sql"
    print_success "ย้าย V20250810_05__group_message.sql → V20250810_09__group_message.sql"
fi

print_success "แก้ไขลำดับไฟล์เสร็จสิ้น"

# ขั้นตอนที่ 2: แก้ไข foreign key reference
print_info "ขั้นตอนที่ 2: แก้ไข foreign key reference..."

# แก้ไขไฟล์ V20250810_06__group_member.sql
if [ -f "$MIGRATION_DIR/V20250810_06__group_member.sql" ]; then
    sed -i '' 's/REFERENCES study_group(id)/REFERENCES study_group(groupid)/g' "$MIGRATION_DIR/V20250810_06__group_member.sql"
    print_success "แก้ไข foreign key ใน V20250810_06__group_member.sql"
fi

# แก้ไขไฟล์ V20250810_07__group_content.sql
if [ -f "$MIGRATION_DIR/V20250810_07__group_content.sql" ]; then
    sed -i '' 's/REFERENCES study_group(id)/REFERENCES study_group(groupid)/g' "$MIGRATION_DIR/V20250810_07__group_content.sql"
    print_success "แก้ไข foreign key ใน V20250810_07__group_content.sql"
fi

print_success "แก้ไข foreign key reference เสร็จสิ้น"

# ขั้นตอนที่ 3: สร้าง SQL script สำหรับลบของที่ค้าง
print_info "ขั้นตอนที่ 3: สร้าง SQL script สำหรับลบของที่ค้าง..."

cat > clean_failed_migration_auto.sql << 'EOF'
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
EOF

print_success "สร้างไฟล์ clean_failed_migration_auto.sql"

# ขั้นตอนที่ 4: สร้าง script สำหรับรัน repair
print_info "ขั้นตอนที่ 4: สร้าง script สำหรับรัน repair..."

cat > run_repair.sh << 'EOF'
#!/bin/bash

echo "🛠️  รัน Flyway Repair และ Start Application"
echo "=========================================="

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

chmod +x run_repair.sh
print_success "สร้างไฟล์ run_repair.sh"

# ขั้นตอนที่ 5: สร้าง script ทดสอบ API
print_info "ขั้นตอนที่ 5: สร้าง script ทดสอบ API..."

cat > test_api.sh << 'EOF'
#!/bin/bash

echo "🧪 ทดสอบ Study Group API"
echo "========================"

# ตั้งค่า URL และ Token (แก้ไขตามที่คุณใช้)
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
            "name": "Test Study Group",
            "description": "กลุ่มทดสอบหลังแก้ไข migration"
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

chmod +x test_api.sh
print_success "สร้างไฟล์ test_api.sh"

# แสดงสรุป
echo ""
echo "🎯 สรุปการแก้ไข:"
echo "=================="
print_success "✅ แก้ไขลำดับไฟล์ migration เสร็จสิ้น"
print_success "✅ แก้ไข foreign key reference เสร็จสิ้น"
print_success "✅ สร้าง backup ที่: $BACKUP_DIR"
print_success "✅ สร้างไฟล์ SQL สำหรับลบของที่ค้าง: clean_failed_migration_auto.sql"
print_success "✅ สร้างไฟล์ script สำหรับรัน repair: run_repair.sh"
print_success "✅ สร้างไฟล์ script ทดสอบ API: test_api.sh"

echo ""
echo "📋 ขั้นตอนต่อไป:"
echo "================"
echo "1. เข้า MySQL และรันคำสั่ง SQL:"
echo "   mysql -h localhost -P 3307 -u root -p kayaan_db"
echo "   source clean_failed_migration_auto.sql"
echo ""
echo "2. รัน repair และ start application:"
echo "   ./run_repair.sh"
echo ""
echo "3. ทดสอบ API:"
echo "   ./test_api.sh"
echo ""
echo "4. ถ้ายังมีปัญหา ให้ใช้ทางลัดชั่วคราว:"
echo "   เพิ่มใน application-dev.yml:"
echo "   spring:"
echo "     flyway:"
echo "       validate-on-migrate: false"
echo ""
print_success "🎉 เสร็จสิ้น! กรุณาทำตามขั้นตอนข้างต้นครับ"
