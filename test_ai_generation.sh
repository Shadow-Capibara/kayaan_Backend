#!/bin/bash

# AI Generation Test Script

# ตั้งค่าตัวแปร
TOKEN="your-jwt-token-here"
BASE="http://localhost:8080/api"

echo "🤖 ทดสอบ AI Generation Feature..."

# ตรวจสอบ OpenAI Configuration
echo "🔧 ตรวจสอบ OpenAI Configuration..."
CONFIG_RESPONSE=$(curl -s -X GET "$BASE/ai/config/test" \
  -H "Authorization: Bearer $TOKEN")

echo "Config Response: $CONFIG_RESPONSE"

# 1. ทดสอบสร้าง Flashcard
echo "📝 ทดสอบสร้าง Flashcard..."
FLASHCARD_RESPONSE=$(curl -s -X POST "$BASE/ai/generation/requests" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "promptText": "สร้าง Flashcard เกี่ยวกับการเขียนโปรแกรม",
    "outputFormat": "flashcard",
    "maxRetries": 3
  }')

echo "Flashcard Response: $FLASHCARD_RESPONSE"

# แยก request ID
REQUEST_ID=$(echo "$FLASHCARD_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)

if [ -n "$REQUEST_ID" ]; then
    echo "✅ ได้ Request ID: $REQUEST_ID"
    
    # 2. เริ่มการสร้างเนื้อหา
    echo "�� เริ่มการสร้างเนื้อหา..."
    GENERATION_RESPONSE=$(curl -s -X POST "$BASE/ai/generation/requests/$REQUEST_ID/generate" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Generation Response: $GENERATION_RESPONSE"
    
    # 3. ตรวจสอบสถานะ
    echo "📊 ตรวจสอบสถานะ..."
    STATUS_RESPONSE=$(curl -s -X GET "$BASE/ai/generation/requests/$REQUEST_ID/status" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Status Response: $STATUS_RESPONSE"
    
else
    echo "❌ ไม่สามารถสร้าง Request ได้"
fi

# 4. ทดสอบสร้าง Quiz
echo "❓ ทดสอบสร้าง Quiz..."
QUIZ_RESPONSE=$(curl -s -X POST "$BASE/ai/generation/requests" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "promptText": "สร้าง Quiz เกี่ยวกับการเขียนโปรแกรม",
    "outputFormat": "quiz",
    "maxRetries": 3
  }')

echo "Quiz Response: $QUIZ_RESPONSE"

# 5. ทดสอบสร้าง Note
echo "📖 ทดสอบสร้าง Note..."
NOTE_RESPONSE=$(curl -s -X POST "$BASE/ai/generation/requests" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "promptText": "สร้าง Note สรุปเกี่ยวกับการเขียนโปรแกรม",
    "outputFormat": "note",
    "maxRetries": 3
  }')

echo "Note Response: $NOTE_RESPONSE"

echo "✅ การทดสอบเสร็จสิ้น!"
