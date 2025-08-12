#!/bin/bash

echo "🧪 ทดสอบ Study Group API (ทางเลือก A)"
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
