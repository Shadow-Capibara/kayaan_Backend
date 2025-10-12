# Study Streak API Documentation

## 📋 **Overview**

Study Streak API ใหม่ตาม flowchart logic ที่ใช้ Freezing Count system

## 🔗 **Base URL**
```
https://your-api-domain.com/api/users
```

## 🔐 **Authentication**
All endpoints require Bearer token authentication:
```
Authorization: Bearer {your-jwt-token}
```

## 📚 **API Endpoints**

### **1. Complete Daily Task**
บันทึกการทำ daily task (CREATED_CONTENT หรือ INTERACTIVE_MODE)

```http
POST /api/users/{userId}/streak/complete-task
```

**Request Body:**
```json
{
  "taskType": "CREATED_CONTENT", // หรือ "INTERACTIVE_MODE"
  "contentId": 123,
  "metadata": "optional additional data"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Daily task completed successfully",
  "streakCount": 5,
  "freezingCount": 0,
  "lastActivityTime": "2024-01-15T10:30:00"
}
```

**Error Responses:**
- `403 Forbidden` - Access denied
- `500 Internal Server Error` - Server error

---

### **2. Get Streak Information**
ดูข้อมูล streak พื้นฐาน

```http
GET /api/users/{userId}/streak
```

**Response (200 OK):**
```json
{
  "streakCount": 5,
  "freezingCount": 0,
  "lastActivityTime": "2024-01-15T10:30:00",
  "lastFreezeDate": null,
  "hasCompletedToday": true,
  "daysSinceLastActivity": 0
}
```

**Error Responses:**
- `403 Forbidden` - Access denied
- `500 Internal Server Error` - Server error

---

### **3. Get Detailed Streak Status**
ดูสถานะ streak รายละเอียดสำหรับ dashboard

```http
GET /api/users/{userId}/streak/status
```

**Response (200 OK):**
```json
{
  "streakCount": 5,
  "freezingCount": 0,
  "lastActivityTime": "2024-01-15T10:30:00",
  "lastFreezeDate": null,
  "hasCompletedToday": true,
  "daysSinceLastActivity": 0,
  "statusMessage": "Great job! You've completed your daily task today.",
  "motivationalQuote": null
}
```

**Error Responses:**
- `403 Forbidden` - Access denied
- `500 Internal Server Error` - Server error

---

### **4. Process Daily Check (Testing)**
เรียกใช้ daily check manually (สำหรับ testing)

```http
POST /api/users/{userId}/streak/daily-check
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Daily check processed successfully",
  "streakCount": 5,
  "freezingCount": 0,
  "lastActivityTime": "2024-01-15T10:30:00"
}
```

**Error Responses:**
- `403 Forbidden` - Access denied
- `500 Internal Server Error` - Server error

---

### **5. Reset Streak (Admin Only)**
รีเซ็ต streak (admin เท่านั้น)

```http
DELETE /api/users/{userId}/streak
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Streak reset successfully",
  "streakCount": 0,
  "freezingCount": 0
}
```

**Error Responses:**
- `403 Forbidden` - Admin access required
- `500 Internal Server Error` - Server error

---

## 📊 **Data Models**

### **StudyStreak Entity**
```json
{
  "id": 1,
  "userId": 123,
  "streakCount": 5,
  "lastActivityTime": "2024-01-15T10:30:00",
  "freezingCount": 0,
  "lastFreezeDate": null,
  "createdAt": "2024-01-10T08:00:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### **TaskCompletionRequest**
```json
{
  "taskType": "CREATED_CONTENT", // หรือ "INTERACTIVE_MODE"
  "contentId": 123,
  "metadata": "optional additional data"
}
```

### **StreakStatus**
```json
{
  "streakCount": 5,
  "freezingCount": 0,
  "lastActivityTime": "2024-01-15T10:30:00",
  "lastFreezeDate": null,
  "hasCompletedToday": true,
  "daysSinceLastActivity": 0,
  "statusMessage": "Great job! You've completed your daily task today.",
  "motivationalQuote": null
}
```

## 🔄 **Business Logic**

### **Daily Task Types**
- **CREATED_CONTENT** - สร้าง content (Manual หรือ AI Generation)
- **INTERACTIVE_MODE** - ทำ interactive content (Quiz, Flashcard, Note)

### **Freezing Count System**
- **Freezing Count** = จำนวนวันที่ไม่ได้ทำ daily task
- **Reset Conditions:**
  - 2 freezes ใน 1 สัปดาห์ → Reset Streak
  - 3 freezes ใน 1 เดือน → Reset Streak

### **Scheduled Jobs**
- **Daily Check** - 00:01 ทุกวัน (Start of Day Check)
- **Weekly Reset** - วันอาทิตย์ 01:00
- **Monthly Reset** - วันที่ 1 ของเดือน 02:00

## 🧪 **Testing Examples**

### **cURL Examples**

#### **Complete Daily Task**
```bash
curl -X POST "https://your-api-domain.com/api/users/123/streak/complete-task" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "taskType": "CREATED_CONTENT",
    "contentId": 456,
    "metadata": "Created new quiz"
  }'
```

#### **Get Streak Information**
```bash
curl -X GET "https://your-api-domain.com/api/users/123/streak" \
  -H "Authorization: Bearer your-jwt-token"
```

#### **Get Detailed Status**
```bash
curl -X GET "https://your-api-domain.com/api/users/123/streak/status" \
  -H "Authorization: Bearer your-jwt-token"
```

### **JavaScript Examples**

#### **Complete Daily Task**
```javascript
const response = await fetch('/api/users/123/streak/complete-task', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your-jwt-token',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    taskType: 'CREATED_CONTENT',
    contentId: 456,
    metadata: 'Created new quiz'
  })
});

const result = await response.json();
console.log('Streak updated:', result.streakCount);
```

#### **Get Streak Status**
```javascript
const response = await fetch('/api/users/123/streak/status', {
  headers: {
    'Authorization': 'Bearer your-jwt-token'
  }
});

const status = await response.json();
console.log('Current streak:', status.streakCount);
```

## 🔒 **Security**

### **Authentication**
- All endpoints require JWT token
- Users can only access their own streak data
- Admins can access any user's streak data

### **Authorization**
- `GET /users/{userId}/streak` - User can access own data
- `POST /users/{userId}/streak/complete-task` - User can complete own tasks
- `DELETE /users/{userId}/streak` - Admin only

### **Rate Limiting**
- No specific rate limits implemented
- Consider implementing if needed

## 📈 **Performance**

### **Database Indexes**
- `idx_user_id` - User ID lookup
- `idx_last_activity_time` - Activity time queries
- `idx_freezing_count` - Freezing count queries
- `idx_last_freeze_date` - Freeze date queries

### **Caching**
- No caching implemented
- Consider implementing Redis cache for frequent queries

## 🚨 **Error Handling**

### **Common Error Codes**
- `400 Bad Request` - Invalid request data
- `403 Forbidden` - Access denied
- `404 Not Found` - User not found
- `500 Internal Server Error` - Server error

### **Error Response Format**
```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/users/123/streak/complete-task"
}
```

## 📝 **Changelog**

### **v1.0.0 (2024-01-15)**
- Initial implementation
- Freezing Count system
- Daily task completion
- Scheduled jobs
- API endpoints

---

**Study Streak API พร้อมใช้งานแล้ว!** 🎉
