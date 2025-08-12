#!/bin/bash

# Avatar Upload Test Script
# ใช้สำหรับทดสอบ flow การอัปโหลด avatar ผ่าน backend proxy

# ========================================
# ตั้งค่าตัวแปร (แก้ไขตามต้องการ)
# ========================================
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImVtYWlsIjoiZnhyZWQ3MjdAZ21haWwuY29tIiwidXNlcm5hbWUiOiJTYW0xIiwic3ViIjoiMTAzIiwiaWF0IjoxNzU0OTcxNjQ4LCJleHAiOjE3NTUwNTgwNDh9.Q0ZUF84nJzs-J1wkdwKVdo8VvoKpHMESO3mv44xGP6Q"
BASE="http://localhost:8080/api"
USER_ID=103
FILE="/Users/wasanrenumat/kayaan_Backend/uploads/avatars/user_402.jpg"  # แก้ไขเป็นพาธไฟล์จริง

# ========================================
# ตรวจสอบตัวแปร
# ========================================
echo "🔍 ตรวจสอบการตั้งค่า..."
echo "BASE URL: $BASE"
echo "USER ID: $USER_ID"
echo "FILE: $FILE"

if [ "$TOKEN" = "<ใส่ JWT token ที่นี่>" ]; then
    echo "❌ กรุณาใส่ JWT token ในตัวแปร TOKEN"
    exit 1
fi

if [ ! -f "$FILE" ]; then
    echo "❌ ไม่พบไฟล์: $FILE"
    exit 1
fi

echo "✅ การตั้งค่าถูกต้อง"
echo ""

# ========================================
# ขั้นตอนที่ 1: ขอ signed URL
# ========================================
echo "📤 ขั้นตอนที่ 1: ขอ signed URL..."
RESPONSE=$(curl -s -X POST "$BASE/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"test.jpg","contentType":"image/jpeg"}')

echo "Response: $RESPONSE"

# แยก signedUrl และ path จาก response
SIGNED_URL=$(echo "$RESPONSE" | grep -o '"signedUrl":"[^"]*"' | cut -d'"' -f4)
PATH_IN_BUCKET=$(echo "$RESPONSE" | grep -o '"path":"[^"]*"' | cut -d'"' -f4)

if [ -z "$SIGNED_URL" ]; then
    echo "❌ ไม่สามารถขอ signed URL ได้"
    echo "Response: $RESPONSE"
    exit 1
fi

echo "✅ ได้ signed URL: $SIGNED_URL"
echo "✅ Path in bucket: $PATH_IN_BUCKET"
echo ""

# ========================================
# ขั้นตอนที่ 2: อัปโหลดไฟล์ผ่าน proxy
# ========================================
echo "�� ขั้นตอนที่ 2: อัปโหลดไฟล์ผ่าน proxy..."
UPLOAD_RESPONSE=$(curl -s -X POST "$BASE/avatar/upload-proxy" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@$FILE;type=image/jpeg" \
  -F "signedUrl=$SIGNED_URL")

echo "Upload Response: $UPLOAD_RESPONSE"

if echo "$UPLOAD_RESPONSE" | grep -q '"message":"Upload successful"'; then
    echo "✅ อัปโหลดสำเร็จ!"
else
    echo "❌ อัปโหลดล้มเหลว"
    echo "Response: $UPLOAD_RESPONSE"
    exit 1
fi
echo ""

# ========================================
# ขั้นตอนที่ 3: บันทึก URL (ถ้าต้องการ)
# ========================================
echo "📤 ขั้นตอนที่ 3: บันทึก URL..."
SAVE_RESPONSE=$(curl -s -X PUT "$BASE/users/$USER_ID/avatar-url" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"path\":\"$PATH_IN_BUCKET\"}")

echo "Save Response: $SAVE_RESPONSE"

if echo "$SAVE_RESPONSE" | grep -q '"id"'; then
    echo "✅ บันทึก URL สำเร็จ!"
else
    echo "❌ บันทึก URL ล้มเหลว"
    echo "Response: $SAVE_RESPONSE"
fi
echo ""

# ========================================
# สรุปผล
# ========================================
echo "🎉 การทดสอบเสร็จสิ้น!"
echo "📁 ไฟล์ที่อัปโหลด: $FILE"
echo "🗂️ Path ใน bucket: $PATH_IN_BUCKET"
echo ""
echo "💡 หากต้องการทดสอบอีกครั้ง ให้รันสคริปต์นี้อีกครั้ง"
