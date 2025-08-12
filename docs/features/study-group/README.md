# Study Group Feature

## 🎉 Status: COMPLETED ✅

Study Group feature ได้รับการพัฒนาเสร็จสิ้นแล้วและพร้อมใช้งานกับ Frontend!

---

## 📚 Documentation

- **[API Reference](./api/STUDY_GROUP_API.md)** - Complete API documentation
- **[Testing Guide](./testing/)** - Curl examples and test scripts
- **[Frontend Integration](./integration/FRONTEND_INTEGRATION_CHECKLIST.md)** - Integration checklist

---

## 🚀 Quick Start

### 1. Environment Setup
```bash
# .env file
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_KEY=your_service_key_here
SUPABASE_BUCKET_LIBRARY=library
```

### 2. Test API
```bash
# ตั้งค่า JWT token
export TOKEN="Bearer eyJhbGciOi..."

# รันสคริปต์ทดสอบ
chmod +x ./testing/test_study_group.sh
./testing/test_study_group.sh
```

---

## 🔧 Key Features

### Role System
- **Owner**: จัดการทุกอย่าง, ลบกลุ่ม, จัดการสมาชิก
- **Moderator**: จัดการสมาชิก (ยกเว้น Owner), จัดการเนื้อหา
- **Member**: ดูเนื้อหา, อัปโหลดไฟล์, ออกจากกลุ่ม

### File Upload Flow
1. **Init**: `POST /api/groups/{id}/resources/upload-url`
2. **Upload**: PUT file ไปที่ signed URL
3. **Complete**: `POST /api/groups/{id}/resources`

### API Endpoints
```
POST   /api/groups                        - สร้างกลุ่ม
GET    /api/groups/my                     - ดูกลุ่มของฉัน
GET    /api/groups/{id}                   - ดูรายละเอียดกลุ่ม
POST   /api/groups/join                   - เข้าร่วมด้วย token
POST   /api/groups/{id}/leave             - ออกจากกลุ่ม
DELETE /api/groups/{id}                   - ลบกลุ่ม (Owner only)
POST   /api/groups/{id}/invites           - สร้าง token เชิญ
GET    /api/groups/{id}/members           - ดูสมาชิก
PATCH  /api/groups/{id}/members/{userId}  - อัปเดตบทบาท
GET    /api/groups/{id}/resources         - ดูไฟล์ที่แชร์
POST   /api/groups/{id}/resources/upload-url - สร้าง signed URL
POST   /api/groups/{id}/resources         - บันทึก metadata
```

---

## 🔗 Frontend Integration

### Base URL
```javascript
// Frontend .env
VITE_API_BASE_URL=http://localhost:8080/api
```

### Authentication
```javascript
// Axios interceptor
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

---

## 📁 File Structure

```
src/main/java/se499/kayaanbackend/Study_Group/
├── controller/          # REST Controllers
├── dto/                # Data Transfer Objects
├── entity/             # JPA Entities
├── repository/         # Data Access Layer
├── service/            # Business Logic
├── security/           # Authorization
└── exception/          # Error Handling
```

---

## 🎯 Success Criteria

Study Group feature พร้อมใช้งานเมื่อ:
- ✅ ทุก API endpoints ตอบถูกต้อง
- ✅ File upload ทำงานได้
- ✅ Role-based access ทำงานถูกต้อง
- ✅ Error handling ครบถ้วน
- ✅ Frontend สามารถเชื่อมต่อได้

---

## 📞 Support

หากมีปัญหาหรือคำถาม:
1. ตรวจสอบ [API Documentation](./api/STUDY_GROUP_API.md)
2. รัน [Test Script](./testing/test_study_group.sh)
3. ดู [Integration Guide](./integration/FRONTEND_INTEGRATION_CHECKLIST.md)
4. ติดต่อทีม Backend

---

**Study Group feature พร้อมใช้งานแล้ว! 🚀**
