#!/bin/bash

echo "🔥 แก้ไขปัญหา Flyway Migration เวอร์ชัน 05 🔥"
echo "================================================"

# 1. เชื่อมต่อ MySQL และลบของที่ค้าง
echo "📋 ขั้นตอนที่ 1: ลบของที่ค้างจาก migration 05"
echo "กรุณาเข้า MySQL และรันคำสั่งต่อไปนี้:"
echo ""
echo "mysql -h localhost -P 3307 -u YOUR_USER -p kayaan_db"
echo ""
echo "แล้วรันคำสั่ง SQL ต่อไปนี้:"
echo ""
echo "-- 1. ดูสถานะ history"
echo "SELECT installed_rank, version, description, script, checksum, success"
echo "FROM flyway_schema_history"
echo "ORDER BY installed_rank;"
echo ""
echo "-- 2. ลบ row ของเวอร์ชันที่ fail (success=0) = 20250810.05"
echo "DELETE FROM flyway_schema_history WHERE version = '20250810.05';"
echo ""
echo "-- 3. ลบออบเจกต์ที่สร้างค้าง (ถ้ามี)"
echo "DROP TABLE IF EXISTS group_message;"
echo ""
echo "-- ออกจาก MySQL"
echo "exit"
echo ""

# 2. แก้ไขลำดับไฟล์ migration
echo "📋 ขั้นตอนที่ 2: แก้ไขลำดับไฟล์ migration"
echo "กำลังเปลี่ยนชื่อไฟล์เพื่อแก้ลำดับ..."

# สร้าง backup directory
mkdir -p migration_backup

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

# 3. รัน repair และ start application
echo "📋 ขั้นตอนที่ 3: รัน repair และ start application"
echo ""

echo "ตัวเลือก A - ใช้ Maven plugin (แนะนำ):"
echo "1. เพิ่ม plugin ใน pom.xml (ถ้ายังไม่มี):"
echo ""
echo "<plugin>"
echo "  <groupId>org.flywaydb</groupId>"
echo "  <artifactId>flyway-maven-plugin</artifactId>"
echo "  <version>10.20.1</version>"
echo "  <configuration>"
echo "    <url>jdbc:mysql://localhost:3307/kayaan_db</url>"
echo "    <user>YOUR_USER</user>"
echo "    <password>YOUR_PASS</password>"
echo "  </configuration>"
echo "</plugin>"
echo ""
echo "2. รันคำสั่ง:"
echo "./mvnw -Dflyway.cleanDisabled=false flyway:repair"
echo "./mvnw spring-boot:run"
echo ""

echo "ตัวเลือก B - ไม่ใช้ plugin (manual):"
echo "./mvnw spring-boot:run"
echo ""

echo "📋 ทดสอบหลังระบบขึ้น:"
echo "curl -i -X POST http://localhost:8080/api/groups \\"
echo " -H \"Authorization: Bearer <eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImVtYWlsIjoiZnhyZWQ3MjdAZ21haWwuY29tIiwidXNlcm5hbWUiOiJTYW0xIiwic3ViIjoiMTAzIiwiaWF0IjoxNzU0OTcxNjQ4LCJleHAiOjE3NTUwNTgwNDh9.Q0ZUF84nJzs-J1wkdwKVdo8VvoKpHMESO3mv44xGP6Q>\" -H \"Content-Type: application/json\" \\"
echo " -d '{\"name\":\"Test\",\"description\":\"test\"}'"
echo ""
echo "curl -i -H \"Authorization: Bearer <eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImVtYWlsIjoiZnhyZWQ3MjdAZ21haWwuY29tIiwidXNlcm5hbWUiOiJTYW0xIiwic3ViIjoiMTAzIiwiaWF0IjoxNzU0OTcxNjQ4LCJleHAiOjE3NTUwNTgwNDh9.Q0ZUF84nJzs-J1wkdwKVdo8VvoKpHMESO3mv44xGP6Q>\" http://localhost:8080/api/groups/my"
echo ""

echo "🎯 ถ้ายังมีปัญหา ให้ใช้ทางลัดชั่วคราว:"
echo "เพิ่มใน application-dev.yml:"
echo "spring:"
echo "  flyway:"
echo "    validate-on-migrate: false"
echo ""

echo "✅ เสร็จแล้ว! กรุณาทำตามขั้นตอนข้างต้นครับ"
