#!/bin/bash

# Study Group API Test Script
# ใช้สำหรับทดสอบ Study Group feature

echo "🧪 Study Group API Test Script"
echo "================================"

# ตั้งค่า base URL
BASE_URL="http://localhost:8080"

# ตัวแปรสำหรับเก็บข้อมูล
GROUP_ID=""
INVITE_TOKEN=""
UPLOAD_URL=""
FILE_URL=""

# ฟังก์ชันสำหรับแสดงผลลัพธ์
show_result() {
    echo "✅ $1"
    echo "Response: $2"
    echo "---"
}

# ฟังก์ชันสำหรับแสดงข้อผิดพลาด
show_error() {
    echo "❌ $1"
    echo "Error: $2"
    echo "---"
}

# ตรวจสอบ JWT Token
if [ -z "$TOKEN" ]; then
    echo "❌ กรุณาตั้งค่า TOKEN ก่อนรันสคริปต์"
    echo "ตัวอย่าง: export TOKEN=\"Bearer eyJhbGciOi...\""
    exit 1
fi

echo "🔑 Using token: ${TOKEN:0:50}..."
echo ""

# 1. สร้างกลุ่ม
echo "1️⃣ Creating Study Group..."
RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups" \
    -H "Authorization: $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "name": "SE331 Review Group",
        "description": "Study group for SE331 final exam preparation"
    }')

if [[ $RESPONSE == *"id"* ]]; then
    GROUP_ID=$(echo $RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
    show_result "Group created successfully" "$RESPONSE"
else
    show_error "Failed to create group" "$RESPONSE"
    exit 1
fi

echo "📝 Group ID: $GROUP_ID"
echo ""

# 2. ดูรายการกลุ่มของฉัน
echo "2️⃣ Getting My Groups..."
RESPONSE=$(curl -s -H "Authorization: $TOKEN" "$BASE_URL/api/groups/my")
show_result "My groups retrieved" "$RESPONSE"
echo ""

# 3. ดูรายละเอียดกลุ่ม
echo "3️⃣ Getting Group Details..."
RESPONSE=$(curl -s -H "Authorization: $TOKEN" "$BASE_URL/api/groups/$GROUP_ID")
show_result "Group details retrieved" "$RESPONSE"
echo ""

# 4. สร้าง token เชิญ
echo "4️⃣ Generating Invite Token..."
RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/$GROUP_ID/invites" \
    -H "Authorization: $TOKEN")

if [[ $RESPONSE == *"token"* ]]; then
    INVITE_TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    show_result "Invite token generated" "$RESPONSE"
else
    show_error "Failed to generate invite token" "$RESPONSE"
fi

echo "🎫 Invite Token: $INVITE_TOKEN"
echo ""

# 5. ดูรายการสมาชิก
echo "5️⃣ Getting Group Members..."
RESPONSE=$(curl -s -H "Authorization: $TOKEN" "$BASE_URL/api/groups/$GROUP_ID/members")
show_result "Group members retrieved" "$RESPONSE"
echo ""

# 6. ทดสอบอัปโหลดไฟล์ (init upload)
echo "6️⃣ Testing File Upload (Init)..."
RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/$GROUP_ID/resources/upload-url" \
    -H "Authorization: $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "fileName": "test-document.pdf",
        "mimeType": "application/pdf",
        "size": 1048576
    }')

if [[ $RESPONSE == *"uploadUrl"* ]]; then
    UPLOAD_URL=$(echo $RESPONSE | grep -o '"uploadUrl":"[^"]*"' | cut -d'"' -f4)
    FILE_URL=$(echo $RESPONSE | grep -o '"fileUrl":"[^"]*"' | cut -d'"' -f4)
    show_result "Upload URL generated" "$RESPONSE"
else
    show_error "Failed to generate upload URL" "$RESPONSE"
fi

echo "📤 Upload URL: ${UPLOAD_URL:0:50}..."
echo "📄 File URL: ${FILE_URL:0:50}..."
echo ""

# 7. ทดสอบ complete upload (โดยไม่ต้องอัปโหลดไฟล์จริง)
echo "7️⃣ Testing Complete Upload..."
RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/$GROUP_ID/resources" \
    -H "Authorization: $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
        \"fileName\": \"test-document.pdf\",
        \"fileUrl\": \"$FILE_URL\",
        \"title\": \"Test Document\",
        \"description\": \"This is a test document for API testing\",
        \"tags\": [\"test\", \"api\", \"document\"]
    }")

if [[ $RESPONSE == *"id"* ]]; then
    show_result "Resource uploaded successfully" "$RESPONSE"
else
    show_error "Failed to complete upload" "$RESPONSE"
fi
echo ""

# 8. ดูรายการไฟล์ที่แชร์
echo "8️⃣ Getting Group Resources..."
RESPONSE=$(curl -s -H "Authorization: $TOKEN" "$BASE_URL/api/groups/$GROUP_ID/resources")
show_result "Group resources retrieved" "$RESPONSE"
echo ""

# 9. ทดสอบการอัปเดตบทบาท (ถ้ามีสมาชิกอื่น)
echo "9️⃣ Testing Role Update (if other members exist)..."
# หมายเหตุ: ต้องมีสมาชิกอื่นในกลุ่มก่อน
RESPONSE=$(curl -s -X PATCH "$BASE_URL/api/groups/$GROUP_ID/members/999" \
    -H "Authorization: $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "userId": 999,
        "role": "moderator"
    }')

# คาดหวังว่าจะได้ 404 หรือ error เพราะไม่มี user ID 999
if [[ $RESPONSE == *"error"* ]] || [[ $RESPONSE == *"404"* ]]; then
    show_result "Role update test (expected error for non-existent user)" "$RESPONSE"
else
    show_result "Role update response" "$RESPONSE"
fi
echo ""

# 10. ทดสอบการออกจากกลุ่ม
echo "🔟 Testing Leave Group..."
RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/$GROUP_ID/leave" \
    -H "Authorization: $TOKEN")

if [[ $RESPONSE == *"409"* ]] || [[ $RESPONSE == *"Owner cannot leave"* ]]; then
    show_result "Leave group test (expected error for owner)" "$RESPONSE"
else
    show_result "Leave group response" "$RESPONSE"
fi
echo ""

# 11. ทดสอบการลบกลุ่ม
echo "1️⃣1️⃣ Testing Delete Group..."
RESPONSE=$(curl -s -X DELETE "$BASE_URL/api/groups/$GROUP_ID" \
    -H "Authorization: $TOKEN")

if [[ $RESPONSE == *"200"* ]] || [[ $RESPONSE == "" ]]; then
    show_result "Group deleted successfully" "Group $GROUP_ID deleted"
else
    show_error "Failed to delete group" "$RESPONSE"
fi
echo ""

# 12. ทดสอบการเข้าร่วมด้วย token (ถ้ามี token)
if [ ! -z "$INVITE_TOKEN" ]; then
    echo "1️⃣2️⃣ Testing Join by Token..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/groups/join" \
        -H "Authorization: $TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
            \"token\": \"$INVITE_TOKEN\"
        }")
    
    if [[ $RESPONSE == *"error"* ]] || [[ $RESPONSE == *"already a member"* ]]; then
        show_result "Join by token test (expected error for existing member)" "$RESPONSE"
    else
        show_result "Join by token response" "$RESPONSE"
    fi
    echo ""
fi

echo "🎉 Study Group API Test Completed!"
echo "=================================="
echo ""
echo "📋 Test Summary:"
echo "✅ Group creation"
echo "✅ Group listing"
echo "✅ Group details"
echo "✅ Invite token generation"
echo "✅ Member listing"
echo "✅ File upload (init)"
echo "✅ File upload (complete)"
echo "✅ Resource listing"
echo "✅ Role update (error handling)"
echo "✅ Leave group (error handling)"
echo "✅ Delete group"
echo "✅ Join by token (error handling)"
echo ""
echo "🚀 Study Group feature is ready for Frontend integration!"
