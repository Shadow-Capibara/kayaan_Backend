# 🔥 New Study Streak Logic Documentation

## 📋 **Overview**

Updated Study Streak system ตาม diagram ใหม่ที่ใช้ logic แบบง่ายและชัดเจน

## 🎯 **New Logic Summary**

### **Default Behavior**
- **streak_count** เริ่มต้นที่ **0** เสมอ
- **freezing_count** เริ่มต้นที่ **0**

### **เมื่อ User ทำกิจกรรม (Complete Daily Task)**
1. **streak_count** เพิ่มขึ้น **+1**
2. **freezing_count** รีเซ็ตเป็น **0**
3. **last_activity_time** อัพเดทเป็นเวลาปัจจุบัน

### **เมื่อ User ไม่ได้ทำกิจกรรม (Miss Daily Task)**
1. **freezing_count** เพิ่มขึ้น **+1**
2. **streak_count** คงค่าเดิม (ไม่เปลี่ยนแปลง)
3. **last_freeze_date** อัพเดทเป็นวันปัจจุบัน

### **เงื่อนไขการ Reset Streak**
- เมื่อ **freezing_count == 2** → **streak_count** จะถูกรีเซ็ตเป็น **0**
- **freezing_count** จะถูกรีเซ็ตเป็น **0** ด้วย

## 🔄 **Flow Diagram Logic**

```
Start
  ↓
Check if user complete daily task?
  ↓                    ↓
Yes                   No
  ↓                    ↓
streak_count +1    freezing_count +1
freezing_count = 0   ↓
  ↓              Check if freezing_count == 2?
  ↓                    ↓
End                   Yes
                      ↓
                  streak_count = 0
                  freezing_count = 0
                      ↓
                     End
```

## 📊 **State Transitions**

### **State 1: Normal Operation**
- **streak_count**: 1, 2, 3, ...
- **freezing_count**: 0
- **Action**: User ทำกิจกรรมทุกวัน

### **State 2: Warning State**
- **streak_count**: 1, 2, 3, ... (คงเดิม)
- **freezing_count**: 1
- **Action**: User พลาด 1 วัน

### **State 3: Reset State**
- **streak_count**: 0
- **freezing_count**: 0
- **Action**: User พลาด 2 วันติดต่อกัน

## 🧪 **Test Scenarios**

### **Scenario 1: Perfect Streak**
```
Day 1: Complete task → streak_count = 1, freezing_count = 0
Day 2: Complete task → streak_count = 2, freezing_count = 0
Day 3: Complete task → streak_count = 3, freezing_count = 0
```

### **Scenario 2: One Miss**
```
Day 1: Complete task → streak_count = 1, freezing_count = 0
Day 2: Miss task    → streak_count = 1, freezing_count = 1
Day 3: Complete task → streak_count = 2, freezing_count = 0
```

### **Scenario 3: Two Misses (Reset)**
```
Day 1: Complete task → streak_count = 1, freezing_count = 0
Day 2: Miss task    → streak_count = 1, freezing_count = 1
Day 3: Miss task    → streak_count = 0, freezing_count = 0
Day 4: Complete task → streak_count = 1, freezing_count = 0
```

## 🔧 **Code Changes Made**

### **1. StudyStreakService.java**
- **`shouldResetStreak()`**: เปลี่ยนเป็น `freezing_count == 2`
- **`completeDailyTask()`**: เพิ่ม `streak.resetFreezingCount()`
- **`getResetReason()`**: เปลี่ยนเป็น "2 consecutive missed days"

### **2. StudyStreak.java**
- **ลบ**: `hasMoreThanOneFreezeInPastWeek()`, `hasMoreThanTwoFreezesInCurrentMonth()`
- **เพิ่ม**: `isFreezingCountAtReset()`, `isFreezingCountAtWarning()`

### **3. Status Messages**
- **Warning**: "Complete your daily task today to maintain it. (1 missed day)"
- **Reset**: "Start your learning journey today!"

## 📈 **API Endpoints (Unchanged)**

### **Complete Daily Task**
```http
POST /api/users/{userId}/streak/complete-task
```
**Body:**
```json
{
  "taskType": "CREATED_CONTENT",
  "contentId": 123
}
```

### **Get Streak Status**
```http
GET /api/users/{userId}/streak/status
```

### **Process Daily Check**
```http
POST /api/users/{userId}/streak/daily-check
```

## 🎮 **Frontend Integration**

### **React Hook Usage**
```javascript
const { streakData, completeTask } = useStudyStreak(userId);

// Complete task
await completeTask('CREATED_CONTENT', contentId);

// Check status
console.log('Streak:', streakData.streakCount);
console.log('Freezing:', streakData.freezingCount);
```

### **Status Display**
```javascript
// Warning state
if (streakData.freezingCount === 1) {
  showWarning("Complete your task today to maintain streak!");
}

// Reset state
if (streakData.streakCount === 0 && streakData.freezingCount === 0) {
  showMessage("Start your learning journey today!");
}
```

## 🚀 **Benefits of New Logic**

1. **Simple & Clear**: Logic ง่ายต่อการเข้าใจ
2. **Fair**: ให้โอกาส user 1 วันในการพลาด
3. **Motivating**: ไม่ reset ทันทีเมื่อพลาด 1 วัน
4. **Consistent**: Behavior ที่คาดเดาได้

## 🔍 **Testing**

ใช้ไฟล์ `test_new_streak_logic.sh` เพื่อทดสอบ:

```bash
./test_new_streak_logic.sh
```

## 📝 **Migration Notes**

- **Database**: ไม่ต้องเปลี่ยน schema
- **API**: Endpoints เหมือนเดิม
- **Frontend**: อาจต้องปรับ status messages
- **Scheduled Jobs**: ทำงานเหมือนเดิม

---

**🎉 New Study Streak Logic พร้อมใช้งานแล้ว!**

Logic ใหม่นี้จะทำให้ระบบ Study Streak ใช้งานง่ายและเข้าใจได้มากขึ้น โดยให้โอกาสผู้ใช้พลาด 1 วันโดยไม่ต้องเริ่ม streak ใหม่ แต่ถ้าพลาด 2 วันติดต่อกันก็จะ reset streak ตามที่ออกแบบไว้
