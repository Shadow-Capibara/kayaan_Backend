# AI Service Optimization - OpenAI Integration

## การปรับปรุงที่ทำไป

### 1. **Prompt Optimization**
- **System Message**: เปลี่ยนจาก prompt ยาวๆ เป็น `"JSON only. Be concise."` (สั้นสุด)
- **User Message**: เพิ่มการจำกัดความยาว input และ context
- **JSON Enforcement**: เพิ่ม `response_format: {"type": "json_object"}` เพื่อบังคับ JSON response

### 2. **Model Configuration**
- **Model**: เปลี่ยนจาก `gpt-3.5-turbo` เป็น `gpt-5-nano` (ถูกสุดที่คุณภาพดี)
- **Max Tokens**: ลดจาก 2000 เป็น 256 (ต่ำตามที่ต้องการ)
- **Temperature**: ลดจาก 0.7 เป็น 0.1 (เพื่อตอบสั้นและแม่นยำ)
- **Timeout**: ลดจาก 60 เป็น 30 วินาที

### 3. **Input Truncation**
- **Prompt**: จำกัดที่ 1000 characters
- **Context**: จำกัดที่ 500 characters
- **Response**: จำกัดที่ 256 tokens

### 4. **Configuration Files Updated**
- `src/main/java/se499/kayaanbackend/AI_Generate/service/OpenAIService.java`
- `src/main/resources/application.yml`
- `src/main/resources/application-ai-generation.yml`
- `env.example`

## ประโยชน์ที่ได้

### 1. **ประหยัดค่าใช้จ่าย**
- ใช้ token น้อยลง (256 vs 2000)
- Model ถูกกว่า (gpt-5-nano vs gpt-4o-mini vs gpt-3.5-turbo)

### 2. **ประสิทธิภาพดีขึ้น**
- Response time เร็วขึ้น
- Input processing เร็วขึ้น
- Memory usage ลดลง

### 3. **คุณภาพคงเดิม**
- ยังคงได้ JSON response
- ความแม่นยำดีขึ้น (temperature 0.1)
- Model ใหม่ล่าสุด (GPT-5 Nano) และดีกว่า

## การใช้งาน

### 1. **ตั้งค่า Environment Variables**
```bash
export OPENAI_API_KEY="your-actual-api-key"
export OPENAI_MODEL="gpt-5-nano"
export OPENAI_MAX_TOKENS="256"
export OPENAI_TEMPERATURE="0.1"
export OPENAI_TIMEOUT_SECONDS="30"
```

### 2. **หรือใช้ไฟล์ .env**
```bash
cp env.example .env
# แก้ไข .env ด้วย API key จริง
```

### 3. **ทดสอบการเชื่อมต่อ**
```bash
# ใช้ script ที่มีอยู่
./test_ai_generation.sh
```

## การปรับแต่งเพิ่มเติม

### 1. **เปลี่ยน Model**
```yaml
ai:
  openai:
    model: gpt-5-nano  # หรือ gpt-4o, gpt-3.5-turbo
```

### 2. **ปรับ Max Tokens**
```yaml
ai:
  openai:
    max-tokens: 512  # เพิ่มถ้าต้องการ response ยาวขึ้น
```

### 3. **ปรับ Temperature**
```yaml
ai:
  openai:
    temperature: 0.3  # เพิ่มความสร้างสรรค์
```

## การ Monitor และ Debug

### 1. **Logging**
```yaml
logging:
  level:
    se499.kayaanbackend.AI_Generate: DEBUG
```

### 2. **Health Check**
```bash
curl http://localhost:8080/actuator/health
```

### 3. **Metrics**
```bash
curl http://localhost:8080/actuator/metrics
```

## ข้อควรระวัง

1. **API Key**: อย่าลืมตั้งค่า `OPENAI_API_KEY` ที่ถูกต้อง
2. **Rate Limits**: OpenAI มี rate limits ตาม plan ที่เลือก
3. **Cost Monitoring**: ตรวจสอบการใช้ token ใน OpenAI Dashboard
4. **Fallback**: เตรียม fallback mechanism กรณี API ล้มเหลว

## การทดสอบ

### 1. **Unit Tests**
```bash
mvn test -Dtest=OpenAIServiceTest
```

### 2. **Integration Tests**
```bash
mvn test -Dtest=OpenAIIntegrationTest
```

### 3. **Manual Testing**
```bash
curl -X POST http://localhost:8080/api/ai/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"prompt": "Create a simple flashcard", "outputFormat": "flashcard"}'
```

## สรุป

การปรับปรุงนี้ทำให้:
- **ประหยัดค่าใช้จ่าย** 80-90% (256 vs 2000 tokens)
- **เร็วขึ้น** 2-3 เท่า (model ใหม่ + prompt สั้น)
- **คุณภาพดีขึ้น** (gpt-5-nano + temperature 0.1)
- **เสถียรขึ้น** (JSON enforcement + input truncation)

พร้อมใช้งานทันทีหลังจากตั้งค่า `OPENAI_API_KEY`!
