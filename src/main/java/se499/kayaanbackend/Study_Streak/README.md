# Study Streak System

ระบบ Study Streak สำหรับแอปพลิเคชันการเรียนรู้ที่ช่วยให้ผู้ใช้ติดตามและรักษา streak การเรียนต่อเนื่อง

## ฟีเจอร์หลัก

### 1. Study Session Tracking
- บันทึกการเริ่มต้นและสิ้นสุด session การเรียน
- คำนวณระยะเวลาการเรียน (นาที/ชั่วโมง)
- ตรวจสอบ session ที่ถูกต้อง (ไม่น้อยกว่า 5 นาที, ไม่เกิน 8 ชั่วโมงต่อวัน)

### 2. Streak Calculation
- คำนวณ consecutive days ของการเรียน
- รีเซ็ต streak เมื่อไม่ได้เรียนติดต่อกัน 1 วัน
- แสดง streak ปัจจุบันและ streak สูงสุด

### 3. Daily Goals & Achievements
- ตั้งเป้าหมายการเรียนรายวัน (นาที/ชั่วโมง)
- แสดงสถานะการบรรลุเป้าหมาย
- ระบบ achievements และ badges

### 4. Statistics & Analytics
- สถิติการเรียนรายวัน/สัปดาห์/เดือน
- กราฟแสดงแนวโน้มการเรียน
- รายงานสรุป performance

## โครงสร้าง Database

### Study Sessions Table
```sql
CREATE TABLE study_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    duration_minutes INTEGER,
    subject VARCHAR(100),
    session_type VARCHAR(50),
    is_completed BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### User Streaks Table
```sql
CREATE TABLE user_streaks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    last_study_date DATE,
    total_study_days INTEGER DEFAULT 0,
    total_study_minutes INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Daily Goals Table
```sql
CREATE TABLE daily_goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_minutes INTEGER NOT NULL,
    goal_date DATE NOT NULL,
    achieved_minutes INTEGER DEFAULT 0,
    is_achieved BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## API Endpoints

### Study Session Management
- `POST /api/v1/study-sessions/start` - เริ่มต้น session การเรียน
- `POST /api/v1/study-sessions/end` - สิ้นสุด session การเรียน
- `GET /api/v1/study-sessions/current` - ดู session ปัจจุบัน
- `GET /api/v1/study-sessions/history` - ดูประวัติ session
- `PUT /api/v1/study-sessions/{id}` - แก้ไข session
- `DELETE /api/v1/study-sessions/{id}` - ลบ session

### Streak Management
- `GET /api/v1/streaks/current` - ดู streak ปัจจุบัน
- `GET /api/v1/streaks/statistics` - ดูสถิติ streak
- `GET /api/v1/streaks/leaderboard` - ดู leaderboard

### Daily Goals
- `POST /api/v1/goals/daily` - สร้างเป้าหมายรายวัน
- `GET /api/v1/goals/daily/current` - ดูเป้าหมายปัจจุบัน
- `PUT /api/v1/goals/daily/{id}` - แก้ไขเป้าหมาย
- `GET /api/v1/goals/daily/history` - ดูประวัติเป้าหมาย

### Analytics & Reports
- `GET /api/v1/analytics/daily` - สถิติรายวัน
- `GET /api/v1/analytics/weekly` - สถิติรายสัปดาห์
- `GET /api/v1/analytics/monthly` - สถิติรายเดือน
- `GET /api/v1/analytics/trends` - แนวโน้มการเรียน

## Business Logic

### Streak Calculation Rules
- ต้องมีการเรียนอย่างน้อย 5 นาทีต่อวันเพื่อนับเป็น streak
- ถ้าไม่ได้เรียนติดต่อกัน 1 วัน จะรีเซ็ต streak เป็น 0
- เก็บสถิติ longest streak แยกต่างหาก

### Session Validation
- Session ต้องมีระยะเวลาอย่างน้อย 5 นาที
- ไม่เกิน 8 ชั่วโมงต่อวัน
- ไม่สามารถมี active session มากกว่า 1 session ต่อ user

### Timezone Handling
- ใช้ UTC สำหรับการเก็บข้อมูล
- แสดงผลตาม timezone ของ user

## การใช้งาน

### 1. เริ่มต้น Session การเรียน
```bash
POST /api/v1/study-sessions/start
{
    "subject": "Mathematics",
    "sessionType": "Study"
}
```

### 2. สิ้นสุด Session การเรียน
```bash
POST /api/v1/study-sessions/end?sessionId=1
```

### 3. ดู Streak ปัจจุบัน
```bash
GET /api/v1/streaks/current
```

### 4. สร้างเป้าหมายรายวัน
```bash
POST /api/v1/goals/daily
{
    "targetMinutes": 120
}
```

### 5. ดูสถิติรายวัน
```bash
GET /api/v1/analytics/daily?date=2024-01-15
```

## การทดสอบ

รัน unit tests:
```bash
mvn test -Dtest=StudySessionServiceTest
```

## การ Deploy

1. สร้าง database tables ตาม schema ที่กำหนด
2. ตั้งค่า environment variables
3. Deploy application
4. ตรวจสอบ API endpoints ผ่าน Swagger UI

## การพัฒนาเพิ่มเติม

### Features ที่อาจเพิ่มในอนาคต
- Notifications system
- Social features (แชร์ streak, leaderboard)
- Achievement badges
- Study group challenges
- Advanced analytics และ reporting

### Performance Optimizations
- Database indexing
- Caching strategies
- Pagination สำหรับ large datasets
- Background job processing

## การสนับสนุน

หากมีปัญหาหรือคำถามเกี่ยวกับ Study Streak System กรุณาติดต่อทีมพัฒนา 