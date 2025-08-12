#!/bin/bash

echo "🧪 ทดสอบ Study Group API หลังแก้ไข Migration 🧪"
echo "================================================"

# ตั้งค่า URL และ Token (แก้ไขตามที่คุณใช้)
API_BASE="http://localhost:8080/api"
TOKEN="YOUR_JWT_TOKEN_HERE"  # แก้ไขเป็น token จริงของคุณ

echo "📋 ขั้นตอนการทดสอบ:"
echo "1. สร้าง Study Group ใหม่"
echo "2. ดูรายการ My Groups"
echo "3. ตรวจสอบว่าข้อมูลถูกบันทึกในฐานข้อมูล"
echo ""

echo "🔑 หมายเหตุ: กรุณาแก้ไข TOKEN ในไฟล์นี้ก่อนรัน"
echo "   TOKEN=\"YOUR_JWT_TOKEN_HERE\""
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

# รันการทดสอบ
if [ "$TOKEN" = "YOUR_JWT_TOKEN_HERE" ]; then
    echo "❌ กรุณาแก้ไข TOKEN ในไฟล์ก่อนรัน"
    echo "   เปิดไฟล์ test_study_group_api.sh และแก้ไขบรรทัด:"
    echo "   TOKEN=\"YOUR_JWT_TOKEN_HERE\""
    echo "   เป็น token จริงของคุณ"
    exit 1
fi

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
