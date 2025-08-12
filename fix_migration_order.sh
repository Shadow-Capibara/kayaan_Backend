#!/bin/bash

echo "🔥 แก้ไขลำดับไฟล์ Migration 🔥"
echo "=============================="

# สร้าง backup directory
mkdir -p migration_backup

echo "📋 กำลังย้ายไฟล์เพื่อแก้ลำดับ..."

# ย้ายไฟล์ 05 ไปเป็น 06 (ชั่วคราว)
mv src/main/resources/db/migration/V20250810_05__group_message.sql migration_backup/

# ย้ายไฟล์ 06 ไปเป็น 05
mv src/main/resources/db/migration/V20250810_06__study_group.sql src/main/resources/db/migration/V20250810_05__study_group.sql

# ย้ายไฟล์ 07 ไปเป็น 06
mv src/main/resources/db/migration/V20250810_07__group_member.sql src/main/resources/db/migration/V20250810_06__group_member.sql

# ย้ายไฟล์ 08 ไปเป็น 07
mv src/main/resources/db/migration/V20250810_08__group_content.sql src/main/resources/db/migration/V20250810_07__group_content.sql

# ย้ายไฟล์ 09 ไปเป็น 08
mv src/main/resources/db/migration/V20250810_09__group_invite.sql src/main/resources/db/migration/V20250810_08__group_invite.sql

# ย้าย group_message กลับมาเป็น 09
mv migration_backup/V20250810_05__group_message.sql src/main/resources/db/migration/V20250810_09__group_message.sql

echo "✅ แก้ไขลำดับไฟล์เสร็จแล้ว:"
echo "   V20250810_05__study_group.sql"
echo "   V20250810_06__group_member.sql"
echo "   V20250810_07__group_content.sql"
echo "   V20250810_08__group_invite.sql"
echo "   V20250810_09__group_message.sql"
echo ""

echo "📋 ขั้นตอนต่อไป:"
echo "1. เข้า MySQL และลบ row ที่ fail:"
echo "   DELETE FROM flyway_schema_history WHERE version = '20250810.05';"
echo "   DROP TABLE IF EXISTS group_message;"
echo ""
echo "2. รัน application:"
echo "   ./mvnw spring-boot:run"
echo ""
