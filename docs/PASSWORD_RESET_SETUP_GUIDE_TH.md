# 🔐 คู่มือการตั้งค่าระบบ Forgot Password (ภาษาไทย)

## 📖 ภาพรวม

เอกสารนี้เป็นคู่มือสำหรับการตั้งค่าและใช้งานระบบ **Forgot Password** ที่พัฒนาด้วย Spring Boot และ Gmail SMTP

---

## 🎯 ฟีเจอร์ที่มี

1. ส่งรหัส reset password 6 ตัว (a-z, 0-9) ผ่านอีเมล Gmail
2. รหัสมีอายุการใช้งาน 15 นาที
3. รหัสใช้งานได้ครั้งเดียว (one-time use)
4. อีเมล HTML สวยงาม
5. ความปลอดภัยสูง

---

## ⚙️ วิธีการตั้งค่า (สำหรับ Developer)

### ขั้นตอนที่ 1: ตั้งค่า Gmail App Password

1. เปิด [Google Account](https://myaccount.google.com/)
2. ไปที่ **ความปลอดภัย (Security)**
3. เปิดใช้งาน **การยืนยันตัวตน 2 ขั้นตอน (2-Step Verification)**
4. กลับไปที่หน้า Security อีกครั้ง
5. ค้นหา **รหัสผ่านแอป (App Passwords)**
6. สร้าง App Password ใหม่:
   - เลือก **Mail**
   - เลือก **Other (Custom name)**
   - ตั้งชื่อเป็น "Kayaan Backend"
7. คัดลอกรหัส 16 ตัวที่ได้มา

### ขั้นตอนที่ 2: ตั้งค่า Environment Variables

สร้างไฟล์ `.env` ในโปรเจค หรือตั้งค่าใน environment variables:

```env
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop
```

> **หมายเหตุ**: `MAIL_PASSWORD` คือรหัส 16 ตัวที่ได้จากขั้นตอนที่ 1 (ไม่ใช่รหัสผ่าน Gmail ปกติ)

### ขั้นตอนที่ 3: ตรวจสอบการตั้งค่า

ตรวจสอบว่าไฟล์ `application.yml` มีการตั้งค่าดังนี้:

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

### ขั้นตอนที่ 4: Build และ Run

```bash
# Build โปรเจค
mvn clean install

# Run โปรเจค
mvn spring-boot:run
```

---

## 🧪 วิธีทดสอบ

### ทดสอบด้วย cURL

**1. ทดสอบขอรหัส reset:**

```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\"}"
```

**2. ตรวจสอบอีเมล:**
- เปิดอีเมลของคุณ
- จะได้รับอีเมลที่มีรหัส 6 ตัว เช่น `a1b2c3`

**3. ทดสอบ reset password:**

```bash
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d "{\"resetCode\":\"a1b2c3\",\"newPassword\":\"newPassword123\"}"
```

### ทดสอบด้วย Postman

**1. Forgot Password:**
- Method: `POST`
- URL: `http://localhost:8080/api/auth/forgot-password`
- Headers: `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "email": "your-email@gmail.com"
  }
  ```

**2. Reset Password:**
- Method: `POST`
- URL: `http://localhost:8080/api/auth/reset-password`
- Headers: `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "resetCode": "a1b2c3",
    "newPassword": "newSecurePassword123"
  }
  ```

---

## 📱 API Endpoints

### 1. ขอรหัส Reset Password

```
POST /api/auth/forgot-password
```

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "หากอีเมลนี้มีในระบบ เราได้ส่งรหัส reset ไปยังอีเมลของคุณแล้ว กรุณาตรวจสอบอีเมล",
  "email": "user@example.com"
}
```

### 2. Reset Password

```
POST /api/auth/reset-password
```

**Request:**
```json
{
  "resetCode": "a1b2c3",
  "newPassword": "newPassword123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "เปลี่ยนรหัสผ่านสำเร็จ คุณสามารถเข้าสู่ระบบด้วยรหัสผ่านใหม่ได้แล้ว"
}
```

---

## 🎨 ตัวอย่างอีเมลที่ผู้ใช้จะได้รับ

```
🔐 รีเซ็ตรหัสผ่าน Kayaan

สวัสดีครับ,

คุณได้ทำการร้องขอรีเซ็ตรหัสผ่านสำหรับบัญชี Kayaan ของคุณ 
กรุณาใช้รหัสด้านล่างนี้เพื่อดำเนินการต่อ:

┌─────────────────┐
│   a 1 b 2 c 3   │
└─────────────────┘

⏰ สำคัญ: รหัสนี้จะหมดอายุภายใน 15 นาที

หากคุณไม่ได้ทำการร้องขอรีเซ็ตรหัสผ่าน กรุณาเพิกเฉยต่ออีเมลนี้ 
บัญชีของคุณยังคงปลอดภัยอยู่
```

---

## 🔒 ความปลอดภัย

- ✅ รหัส reset หมดอายุภายใน 15 นาที
- ✅ รหัสใช้งานได้ครั้งเดียว
- ✅ รหัสเก่าจะถูก invalidate เมื่อขอรหัสใหม่
- ✅ รหัสผ่านถูก hash ด้วย BCrypt
- ✅ ไม่เปิดเผยว่าอีเมลมีในระบบหรือไม่
- ✅ ใช้ SecureRandom สำหรับสร้างรหัส
- ✅ STARTTLS สำหรับการส่งอีเมล

---

## ❓ FAQ (คำถามที่พบบ่อย)

### Q: ทำไมส่งอีเมลไม่ได้?

**A:** ตรวจสอบดังนี้:
1. ตรวจสอบว่าได้สร้าง App Password แล้ว (ไม่ใช่รหัสผ่าน Gmail ปกติ)
2. ตรวจสอบว่า 2-Step Verification เปิดอยู่
3. ตรวจสอบว่า Environment Variables ตั้งค่าถูกต้อง
4. ตรวจสอบ logs ใน console

### Q: รหัส reset หมดอายุเมื่อไหร่?

**A:** รหัสจะหมดอายุภายใน **15 นาที** หลังจากส่งอีเมล

### Q: รหัส reset ใช้ได้กี่ครั้ง?

**A:** รหัสแต่ละตัวใช้ได้ **ครั้งเดียวเท่านั้น** หลังจากใช้แล้วจะไม่สามารถใช้ซ้ำได้

### Q: ถ้าขอรหัสใหม่ รหัสเก่าจะเป็นอย่างไร?

**A:** รหัสเก่าทั้งหมดจะถูก **invalidate ทันที** เมื่อขอรหัสใหม่

### Q: รหัส reset มีรูปแบบอย่างไร?

**A:** รหัสมี **6 ตัวอักษร** ประกอบด้วย:
- ตัวอักษร a-z (พิมพ์เล็ก)
- ตัวเลข 0-9
- ตัวอย่าง: `a1b2c3`, `x9y8z7`, `abc123`

---

## 🐛 การแก้ปัญหา (Troubleshooting)

### ปัญหา: ไม่ได้รับอีเมล

**วิธีแก้:**
1. ตรวจสอบ Spam/Junk folder
2. ตรวจสอบว่าอีเมลถูกต้อง
3. ตรวจสอบ logs ว่าส่งสำเร็จหรือไม่
4. ตรวจสอบ App Password ถูกต้อง

### ปัญหา: Error "Invalid or expired reset code"

**วิธีแก้:**
1. ตรวจสอบว่ารหัสพิมพ์ถูกต้อง (6 ตัว)
2. ตรวจสอบว่ารหัสยังไม่หมดอายุ (< 15 นาที)
3. ตรวจสอบว่ารหัสยังไม่ถูกใช้ไปแล้ว
4. ขอรหัสใหม่ถ้าจำเป็น

### ปัญหา: Error "Failed to send email"

**วิธีแก้:**
1. ตรวจสอบ internet connection
2. ตรวจสอบ Gmail App Password
3. ตรวจสอบว่า Gmail account ไม่ถูก lock
4. ตรวจสอบ application.yml configuration

---

## 📞 ติดต่อ

หากมีปัญหาหรือข้อสงสัย:
1. ตรวจสอบ logs ใน console
2. ตรวจสอบ database table `password_reset_token`
3. อ่านเอกสารฉบับเต็ม: `PASSWORD_RESET_API_DOCUMENTATION.md`

---

## ✅ Checklist การตั้งค่า

- [ ] สร้าง Gmail App Password แล้ว
- [ ] ตั้งค่า Environment Variables แล้ว (`MAIL_USERNAME`, `MAIL_PASSWORD`)
- [ ] Build project สำเร็จ
- [ ] Run project สำเร็จ
- [ ] ทดสอบส่งอีเมลสำเร็จ
- [ ] ทดสอบ reset password สำเร็จ
- [ ] ได้รับอีเมลพร้อมรหัส 6 ตัว
- [ ] เปลี่ยนรหัสผ่านสำเร็จ
- [ ] Login ด้วยรหัสผ่านใหม่สำเร็จ

---

**พร้อมใช้งานแล้ว! 🚀**

*อัพเดทล่าสุด: 19 ตุลาคม 2025*

