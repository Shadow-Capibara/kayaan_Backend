# 🔐 Password Reset API Documentation

## 📋 สรุปภาพรวม (Overview)

เอกสารนี้อธิบายระบบ **Forgot Password** และ **Reset Password** ที่พัฒนาขึ้นสำหรับ Kayaan Backend API

### ✨ Features ที่ทำเสร็จแล้ว

1. ✅ ระบบส่งรหัส reset password ผ่าน Gmail SMTP
2. ✅ รหัส reset แบบ 6 ตัวอักษร (a-z, 0-9)
3. ✅ ระบบบันทึกรหัสลงฐานข้อมูลก่อนส่งอีเมล
4. ✅ รหัสมีอายุการใช้งาน 15 นาที
5. ✅ รหัสใช้งานได้เพียงครั้งเดียว (one-time use)
6. ✅ Email Template สวยงามในรูปแบบ HTML
7. ✅ Security & Validation ครบถ้วน

---

## 🔧 สิ่งที่ Backend ทำไปแล้ว

### 1. Database Schema

สร้างตาราง `password_reset_token` ในฐานข้อมูล:

```sql
CREATE TABLE password_reset_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    reset_code VARCHAR(6) NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);
```

### 2. Java Classes ที่สร้างแล้ว

- **Entity**: `PasswordResetToken.java`
- **Repository**: `PasswordResetTokenRepository.java`
- **DTOs**:
  - `ForgotPasswordRequest.java`
  - `ResetPasswordRequest.java`
  - `PasswordResetResponse.java`
- **Services**:
  - `EmailService.java` - สำหรับส่งอีเมล
  - `PasswordResetService.java` - Logic หลัก
- **Controller**: `PasswordResetController.java`

### 3. Dependencies ที่เพิ่ม

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 4. Configuration

เพิ่มการตั้งค่า Gmail SMTP ใน `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

---

## 🌐 API Endpoints

### Base URL
```
http://localhost:8080/api/auth
```

### 1. 📧 ขอรหัส Reset Password

**Endpoint**: `POST /api/auth/forgot-password`

**Description**: ส่งรหัส reset password 6 ตัวไปยังอีเมลของผู้ใช้

**Authentication**: ไม่ต้อง (Public endpoint)

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Request Example (JavaScript/Fetch)**:
```javascript
const forgotPassword = async (email) => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/forgot-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email })
    });

    const data = await response.json();
    
    if (data.success) {
      console.log('รหัส reset ถูกส่งไปยังอีเมลแล้ว');
      return data;
    } else {
      console.error('Error:', data.message);
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Failed to send reset code:', error);
    throw error;
  }
};

// การใช้งาน
await forgotPassword('user@example.com');
```

**Success Response (200 OK)**:
```json
{
  "success": true,
  "message": "หากอีเมลนี้มีในระบบ เราได้ส่งรหัส reset ไปยังอีเมลของคุณแล้ว กรุณาตรวจสอบอีเมล",
  "email": "user@example.com"
}
```

**Error Response (500 Internal Server Error)**:
```json
{
  "success": false,
  "message": "เกิดข้อผิดพลาด: Failed to send email. Please try again later."
}
```

**Validation Errors (400 Bad Request)**:
```json
{
  "success": false,
  "message": "Email must be valid"
}
```

---

### 2. 🔑 Reset Password ด้วยรหัส 6 ตัว

**Endpoint**: `POST /api/auth/reset-password`

**Description**: เปลี่ยนรหัสผ่านใหม่โดยใช้รหัส reset 6 ตัวที่ได้รับทางอีเมล

**Authentication**: ไม่ต้อง (Public endpoint)

**Request Body**:
```json
{
  "resetCode": "a1b2c3",
  "newPassword": "newSecurePassword123"
}
```

**Validation Rules**:
- `resetCode`: ต้องเป็น 6 ตัวอักษร (a-z, 0-9 เท่านั้น)
- `newPassword`: ต้องมีความยาวอย่างน้อย 6 ตัวอักษร

**Request Example (JavaScript/Fetch)**:
```javascript
const resetPassword = async (resetCode, newPassword) => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/reset-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ 
        resetCode, 
        newPassword 
      })
    });

    const data = await response.json();
    
    if (data.success) {
      console.log('เปลี่ยนรหัสผ่านสำเร็จ');
      return data;
    } else {
      console.error('Error:', data.message);
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Failed to reset password:', error);
    throw error;
  }
};

// การใช้งาน
await resetPassword('a1b2c3', 'newPassword123');
```

**Success Response (200 OK)**:
```json
{
  "success": true,
  "message": "เปลี่ยนรหัสผ่านสำเร็จ คุณสามารถเข้าสู่ระบบด้วยรหัสผ่านใหม่ได้แล้ว"
}
```

**Error Responses**:

รหัสไม่ถูกต้องหรือหมดอายุ (400 Bad Request):
```json
{
  "success": false,
  "message": "Invalid or expired reset code"
}
```

รหัสหมดอายุ (400 Bad Request):
```json
{
  "success": false,
  "message": "Reset code has expired. Please request a new one."
}
```

รหัสถูกใช้ไปแล้ว (400 Bad Request):
```json
{
  "success": false,
  "message": "Invalid reset code"
}
```

**Validation Errors (400 Bad Request)**:
```json
{
  "success": false,
  "message": "Reset code must be exactly 6 characters"
}
```

---

### 3. 🏥 Health Check

**Endpoint**: `GET /api/auth/password-reset/health`

**Description**: ตรวจสอบว่า Password Reset API ทำงานปกติหรือไม่

**Request Example**:
```javascript
const healthCheck = async () => {
  const response = await fetch('http://localhost:8080/api/auth/password-reset/health');
  const data = await response.json();
  console.log(data);
};
```

**Success Response (200 OK)**:
```json
{
  "success": true,
  "message": "Password Reset API is running"
}
```

---

## 💻 Frontend Integration Guide

### ขั้นตอนการทำงานของระบบ Forgot Password

```
1. User คลิก "ลืมรหัสผ่าน"
   ↓
2. Frontend แสดงหน้าให้กรอกอีเมล
   ↓
3. User กรอกอีเมลและกด submit
   ↓
4. Frontend เรียก POST /api/auth/forgot-password
   ↓
5. Backend ส่งรหัส 6 ตัวไปยังอีเมล
   ↓
6. Frontend แสดงหน้าให้กรอกรหัส 6 ตัวและรหัสผ่านใหม่
   ↓
7. User กรอกรหัส 6 ตัว + รหัสผ่านใหม่
   ↓
8. Frontend เรียก POST /api/auth/reset-password
   ↓
9. Backend เปลี่ยนรหัสผ่านและส่ง response กลับ
   ↓
10. Frontend redirect ไปหน้า login
```

### 📱 UI/UX Flow สำหรับ Frontend

#### หน้า 1: Forgot Password Form (กรอกอีเมล)

```jsx
// React Example
import { useState } from 'react';

function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const response = await fetch('http://localhost:8080/api/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });
      
      const data = await response.json();
      
      if (data.success) {
        setMessage(data.message);
        // Navigate to reset password page
        window.location.href = '/reset-password';
      } else {
        setMessage(data.message);
      }
    } catch (error) {
      setMessage('เกิดข้อผิดพลาด กรุณาลองใหม่อีกครั้ง');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <h1>ลืมรหัสผ่าน</h1>
      <p>กรุณากรอกอีเมลของคุณเพื่อรับรหัส reset password</p>
      
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="อีเมลของคุณ"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        
        <button type="submit" disabled={loading}>
          {loading ? 'กำลังส่ง...' : 'ส่งรหัส reset'}
        </button>
      </form>
      
      {message && <p className="message">{message}</p>}
    </div>
  );
}

export default ForgotPasswordPage;
```

#### หน้า 2: Reset Password Form (กรอกรหัส 6 ตัว + รหัสผ่านใหม่)

```jsx
// React Example
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function ResetPasswordPage() {
  const [resetCode, setResetCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate passwords match
    if (newPassword !== confirmPassword) {
      setMessage('รหัสผ่านไม่ตรงกัน');
      return;
    }
    
    // Validate reset code format (6 characters, a-z and 0-9 only)
    if (!/^[a-z0-9]{6}$/.test(resetCode)) {
      setMessage('รหัส reset ต้องเป็นตัวอักษร a-z และตัวเลข 0-9 จำนวน 6 ตัวเท่านั้น');
      return;
    }
    
    setLoading(true);
    
    try {
      const response = await fetch('http://localhost:8080/api/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          resetCode: resetCode.toLowerCase(),
          newPassword 
        })
      });
      
      const data = await response.json();
      
      if (data.success) {
        setMessage(data.message);
        // Wait 2 seconds then redirect to login
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setMessage(data.message);
      }
    } catch (error) {
      setMessage('เกิดข้อผิดพลาด กรุณาลองใหม่อีกครั้ง');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reset-password-container">
      <h1>รีเซ็ตรหัสผ่าน</h1>
      <p>กรุณากรอกรหัส 6 ตัวที่ได้รับทางอีเมลและรหัสผ่านใหม่</p>
      
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="รหัส 6 ตัว (เช่น a1b2c3)"
          value={resetCode}
          onChange={(e) => setResetCode(e.target.value.toLowerCase())}
          maxLength={6}
          pattern="[a-z0-9]{6}"
          required
        />
        
        <input
          type="password"
          placeholder="รหัสผ่านใหม่"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          minLength={6}
          required
        />
        
        <input
          type="password"
          placeholder="ยืนยันรหัสผ่านใหม่"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          minLength={6}
          required
        />
        
        <button type="submit" disabled={loading}>
          {loading ? 'กำลังเปลี่ยนรหัสผ่าน...' : 'เปลี่ยนรหัสผ่าน'}
        </button>
      </form>
      
      {message && <p className={message.includes('สำเร็จ') ? 'success' : 'error'}>{message}</p>}
    </div>
  );
}

export default ResetPasswordPage;
```

### 🎨 UI Components ที่แนะนำให้สร้าง

1. **ForgotPasswordPage** - หน้ากรอกอีเมล
2. **ResetPasswordPage** - หน้ากรอกรหัส 6 ตัวและรหัสผ่านใหม่
3. **EmailSentNotification** - แจ้งเตือนว่าส่งอีเมลแล้ว
4. **PasswordResetSuccess** - แจ้งเตือนว่าเปลี่ยนรหัสผ่านสำเร็จ

### 📧 ตัวอย่างอีเมลที่ผู้ใช้จะได้รับ

อีเมลจะมีรูปแบบสวยงามแบบ HTML พร้อม:
- หัวข้อ: "รหัสสำหรับรีเซ็ตรหัสผ่าน - Kayaan"
- รหัส 6 ตัวแสดงในกล่องที่เด่นชัด
- คำเตือนว่ารหัสจะหมดอายุใน 15 นาที
- คำแนะนำในการใช้งาน

ตัวอย่างเนื้อหาอีเมล:
```
🔐 รีเซ็ตรหัสผ่าน Kayaan

สวัสดีครับ,

คุณได้ทำการร้องขอรีเซ็ตรหัสผ่านสำหรับบัญชี Kayaan ของคุณ 
กรุณาใช้รหัสด้านล่างนี้เพื่อดำเนินการต่อ:

┌─────────────────┐
│   a 1 b 2 c 3   │  <- รหัส reset ของคุณ
└─────────────────┘

⏰ สำคัญ: รหัสนี้จะหมดอายุภายใน 15 นาที

หากคุณไม่ได้ทำการร้องขอรีเซ็ตรหัสผ่าน กรุณาเพิกเฉยต่ออีเมลนี้
```

---

## ⚙️ การตั้งค่าสำหรับ Developer

### 1. ตั้งค่า Gmail SMTP

**ขั้นตอนการสร้าง Gmail App Password:**

1. ไปที่ [Google Account](https://myaccount.google.com/)
2. คลิก **Security** (ความปลอดภัย)
3. เปิด **2-Step Verification** (การยืนยันตัวตน 2 ขั้นตอน)
4. หลังจากเปิด 2FA แล้ว กลับไปที่ Security
5. ค้นหา **App Passwords** (รหัสผ่านแอป)
6. เลือก **Mail** และ **Other (Custom name)**
7. ตั้งชื่อเป็น "Kayaan Backend"
8. คัดลอกรหัส 16 ตัวที่ได้

**ตั้งค่า Environment Variables:**

สร้างไฟล์ `.env` หรือตั้งค่าใน environment:

```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-character-app-password
```

### 2. ทดสอบระบบ

#### ทดสอบส่งอีเมล:

```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

#### ทดสอบ reset password:

```bash
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"resetCode":"a1b2c3","newPassword":"newPassword123"}'
```

#### ทดสอบ health check:

```bash
curl http://localhost:8080/api/auth/password-reset/health
```

---

## 🔒 Security Features

### ความปลอดภัยที่มีอยู่:

1. ✅ รหัส reset มีอายุการใช้งานเพียง 15 นาที
2. ✅ รหัสใช้งานได้เพียงครั้งเดียว (one-time use)
3. ✅ รหัสเก่าทั้งหมดจะถูก invalidate เมื่อขอรหัสใหม่
4. ✅ ไม่บอกผู้ใช้ว่าอีเมลมีในระบบหรือไม่ (ป้องกัน email enumeration)
5. ✅ รหัสผ่านใหม่ถูก hash ก่อนบันทึกลงฐานข้อมูล
6. ✅ Validation ครบถ้วนทั้ง Backend และแนะนำให้ทำใน Frontend
7. ✅ รหัส reset สุ่มแบบ secure (SecureRandom)
8. ✅ HTTPS/TLS สำหรับการส่งอีเมล (STARTTLS)

### Best Practices สำหรับ Frontend:

1. ✅ Validate email format ก่อนส่งไป Backend
2. ✅ Validate reset code format (6 ตัว, a-z, 0-9)
3. ✅ Validate password strength
4. ✅ แสดง loading state ขณะรอ response
5. ✅ แสดง error messages ที่เข้าใจง่าย
6. ✅ Redirect ไป login page หลัง reset สำเร็จ
7. ✅ แสดง countdown timer 15 นาทีให้ผู้ใช้เห็น (optional)

---

## 🐛 Error Handling

### Common Errors และวิธีแก้ไข:

| Error Message | สาเหตุ | วิธีแก้ไข (Frontend) |
|---------------|--------|---------------------|
| `Email must be valid` | อีเมลไม่ถูกต้อง | แสดง error: "กรุณากรอกอีเมลที่ถูกต้อง" |
| `Failed to send email` | ปัญหาการส่งอีเมล | แสดง error: "ไม่สามารถส่งอีเมลได้ กรุณาลองใหม่" |
| `Invalid or expired reset code` | รหัสไม่ถูกต้อง/หมดอายุ | แสดง error: "รหัสไม่ถูกต้องหรือหมดอายุ กรุณาขอรหัสใหม่" |
| `Reset code has expired` | รหัสหมดอายุ (>15 นาที) | แสดง error พร้อมปุ่ม "ขอรหัสใหม่" |
| `Password must be at least 6 characters` | รหัสผ่านสั้นเกินไป | แสดง error: "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร" |

---

## 📊 Database Cleanup

Backend มี method สำหรับลบ token ที่หมดอายุและถูกใช้แล้ว:

```java
passwordResetService.cleanupExpiredTokens();
```

**แนะนำ**: ควรตั้ง Scheduled Task ให้รันทุกวันเพื่อ cleanup ฐานข้อมูล

---

## 📝 Testing Checklist สำหรับ Frontend

- [ ] ทดสอบกรอกอีเมลที่ถูกต้อง
- [ ] ทดสอบกรอกอีเมลที่ไม่ถูกต้อง
- [ ] ทดสอบกรอกอีเมลที่ไม่มีในระบบ
- [ ] ทดสอบรับอีเมลพร้อมรหัส 6 ตัว
- [ ] ทดสอบกรอกรหัส 6 ตัวที่ถูกต้อง
- [ ] ทดสอบกรอกรหัส 6 ตัวที่ผิด
- [ ] ทดสอบใช้รหัสที่หมดอายุ (>15 นาที)
- [ ] ทดสอบใช้รหัสซ้ำ (ครั้งที่ 2 ต้อง error)
- [ ] ทดสอบเปลี่ยนรหัสผ่านสำเร็จ
- [ ] ทดสอบ login ด้วยรหัสผ่านใหม่
- [ ] ทดสอบ validation ต่างๆ (password length, format, etc.)
- [ ] ทดสอบ UI/UX ในกรณี loading, success, error

---

## 🎯 Next Steps สำหรับ Frontend Team

### หน้าที่ Frontend ต้องทำ:

1. **สร้างหน้า Forgot Password**
   - Input field สำหรับกรอกอีเมล
   - ปุ่ม Submit
   - แสดง loading state
   - แสดง success/error message

2. **สร้างหน้า Reset Password**
   - Input field สำหรับรหัส 6 ตัว (ควรทำเป็น uppercase/lowercase flexible)
   - Input field สำหรับรหัสผ่านใหม่
   - Input field สำหรับยืนยันรหัสผ่าน
   - Validation ทั้งหมด
   - แสดง loading state
   - แสดง success/error message

3. **เพิ่มลิงก์ "ลืมรหัสผ่าน?" ในหน้า Login**
   - เมื่อคลิกให้ไปที่หน้า Forgot Password

4. **UX Enhancements (Optional)**
   - Countdown timer 15 นาที
   - OTP-style input boxes สำหรับรหัส 6 ตัว
   - Password strength indicator
   - Show/Hide password toggle
   - Auto-redirect หลัง reset สำเร็จ

### API Endpoints ที่ต้องเชื่อมต่อ:

```javascript
// 1. Forgot Password
POST http://localhost:8080/api/auth/forgot-password
Body: { "email": "user@example.com" }

// 2. Reset Password
POST http://localhost:8080/api/auth/reset-password
Body: { 
  "resetCode": "a1b2c3", 
  "newPassword": "newPassword123" 
}
```

### Environment Variables สำหรับ Frontend:

```env
REACT_APP_API_BASE_URL=http://localhost:8080
```

---

## 📞 Support & Questions

หากมีคำถามหรือพบปัญหา:
1. ตรวจสอบ Console logs (Backend)
2. ตรวจสอบ Network tab (Frontend)
3. ตรวจสอบว่าตั้งค่า Gmail SMTP ถูกต้อง
4. ตรวจสอบว่าฐานข้อมูล migration รันสำเร็จ

---

## ✅ Summary

### Backend Ready:
- ✅ Database table created
- ✅ API endpoints ready
- ✅ Email service configured
- ✅ Security implemented
- ✅ Validation complete

### Frontend TODO:
- [ ] Create Forgot Password page
- [ ] Create Reset Password page  
- [ ] Add "Forgot Password?" link on Login page
- [ ] Test full flow
- [ ] Deploy

**ระบบพร้อมใช้งานแล้ว! Frontend สามารถเริ่มพัฒนาได้เลย** 🚀

---

*Last Updated: October 19, 2025*
*Documentation Version: 1.0*

