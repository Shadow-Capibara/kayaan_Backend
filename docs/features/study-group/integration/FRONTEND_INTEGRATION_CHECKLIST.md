# Frontend Integration Checklist - Study Group

## ✅ Pre-flight Checklist

### Environment Setup
- [ ] `.env` ฝั่ง BE กรอกครบ:
  ```
  SUPABASE_URL=https://your-project.supabase.co
  SUPABASE_SERVICE_KEY=your_service_key_here
  SUPABASE_BUCKET_LIBRARY=library
  ```

### Database
- [ ] Flyway รันครบ 4 ไฟล์:
  - `V20250810_06__study_group.sql`
  - `V20250810_07__group_member.sql`
  - `V20250810_08__group_content.sql`
  - `V20250810_09__group_invite.sql`

### Security
- [ ] ทุก `/api/groups/**` ต้องการ JWT (401 เมื่อไม่มี/หมดอายุ)
- [ ] Role guard ทำงานถูกต้อง:
  - Owner คนเดียว leave → 409
  - ห้ามลดสิทธิ์ Owner / ห้าม self-promote เกินนโยบาย → 403/409

### CORS
- [ ] อนุญาต origin ของ FE (dev/prod)
- [ ] Methods: GET, POST, PUT, DELETE, OPTIONS
- [ ] Headers: *
- [ ] Credentials: true

### WebSocket
- [ ] WebSocket endpoint ใช้งานได้ (เช่น `/ws`)
- [ ] Authentication ผ่าน WebSocket

## 🧪 Smoke Tests

### 1. Authentication Test
```bash
# ควรได้ 401
curl -H "Authorization: Bearer invalid_token" http://localhost:8080/api/groups/my

# ควรได้ 200 + data
curl -H "Authorization: $TOKEN" http://localhost:8080/api/groups/my
```

### 2. Group Creation Test
```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Group","description":"Test description"}'
```

### 3. File Upload Test
```bash
# Init upload
curl -X POST http://localhost:8080/api/groups/1/resources/upload-url \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"test.pdf","mimeType":"application/pdf","size":1024}'
```

## 🔗 FE Integration Quick Map

### Base URL Configuration
```javascript
// Frontend .env
VITE_API_BASE_URL=http://localhost:8080/api

// หรือ
VITE_API_BASE_URL=https://your-backend.com/api
```

### API Service Structure
```javascript
// GroupService.js
const baseURL = import.meta.env.VITE_API_BASE_URL;

// ✅ ถูกต้อง - ไม่ใส่ /api ซ้ำ
const createGroup = (data) => axios.post(`${baseURL}/groups`, data);

// ❌ ผิด - ใส่ /api ซ้ำ
const createGroup = (data) => axios.post(`${baseURL}/api/groups`, data);
```

### Key API Endpoints
```javascript
// Group Management
POST   /groups                    → createGroup()
GET    /groups/my                 → fetchMyGroups()
GET    /groups/:id                → fetchGroupDetails()
POST   /groups/join               → joinGroupByToken()
POST   /groups/:id/leave          → leaveGroup()
DELETE /groups/:id                → deleteGroup()

// Member Management
GET    /groups/:id/members        → fetchGroupMembers()
PATCH  /groups/:id/members/:userId → updateMemberRole()
DELETE /groups/:id/members/:userId → removeMember()

// Resource Management
GET    /groups/:id/resources      → fetchGroupResources()
POST   /groups/:id/resources/upload-url → initUpload()
POST   /groups/:id/resources      → completeUpload()
DELETE /groups/:id/resources/:id  → deleteResource()

// Invites
POST   /groups/:id/invites        → generateInviteCode()
POST   /groups/:id/invite-by-email → inviteByEmail()
```

### Authentication Setup
```javascript
// Axios interceptor
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 responses
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Redirect to login
      router.push('/login');
    }
    return Promise.reject(error);
  }
);
```

### Router Guards
```javascript
// Vue Router
{
  path: '/study-groups',
  component: StudyGroups,
  meta: { requiresAuth: true }
},
{
  path: '/study-groups/:id',
  component: StudyGroupDetail,
  meta: { requiresAuth: true }
}

// Router guard
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !isAuthenticated()) {
    next('/login');
  } else {
    next();
  }
});
```

## 🧰 Debug Tips

### Common Issues & Solutions

#### 1. Signed URL PUT แล้ว 403/415
```bash
# ✅ ถูกต้อง - Content-Type ตรงกับ mimeType
curl -X PUT "UPLOAD_URL" \
  -H "Content-Type: application/pdf" \
  --data-binary @file.pdf

# ❌ ผิด - Content-Type ไม่ตรง
curl -X PUT "UPLOAD_URL" \
  -H "Content-Type: application/octet-stream" \
  --data-binary @file.pdf
```

#### 2. 401 จาก FE
```javascript
// ✅ ตรวจสอบ Axios interceptor
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ✅ ตรวจสอบ token format
console.log('Token:', `Bearer ${token}`);
```

#### 3. 409 Leave Group (Owner เดี่ยว)
```javascript
// Frontend ควรแสดง modal อธิบาย
if (error.response?.status === 409 && error.response?.data?.error?.includes('Owner cannot leave')) {
  showModal({
    title: 'ไม่สามารถออกจากกลุ่มได้',
    message: 'คุณเป็น Owner คนเดียวในกลุ่ม กรุณาโอน Owner หรือลบกลุ่มก่อน'
  });
}
```

#### 4. Token เชิญ "หมดอายุ/ซ้ำ/เพิกถอน"
```javascript
// Frontend แสดง Toast ข้อความชัดเจน
const handleJoinByToken = async (token) => {
  try {
    await joinGroupByToken(token);
    showToast('เข้าร่วมกลุ่มสำเร็จ', 'success');
  } catch (error) {
    const message = error.response?.data?.error || 'เกิดข้อผิดพลาด';
    showToast(message, 'error');
  }
};
```

### Error Handling Patterns
```javascript
// Standard error handling
const handleApiCall = async (apiCall) => {
  try {
    const response = await apiCall();
    return response.data;
  } catch (error) {
    const status = error.response?.status;
    const message = error.response?.data?.error || 'เกิดข้อผิดพลาด';
    
    switch (status) {
      case 401:
        router.push('/login');
        break;
      case 403:
        showToast('ไม่มีสิทธิ์เข้าถึง', 'error');
        break;
      case 404:
        showToast('ไม่พบข้อมูล', 'error');
        break;
      case 409:
        showToast(message, 'warning');
        break;
      default:
        showToast(message, 'error');
    }
    
    throw error;
  }
};
```

## 🚀 Ready for Production

### Checklist ก่อน Deploy
- [ ] ทดสอบ API endpoints ทั้งหมด
- [ ] ทดสอบ file upload flow
- [ ] ทดสอบ role-based access control
- [ ] ทดสอบ error handling
- [ ] ตั้งค่า CORS สำหรับ production domain
- [ ] ตั้งค่า environment variables
- [ ] ทดสอบ WebSocket connection
- [ ] ทดสอบ authentication flow

### Performance Considerations
- [ ] Implement pagination สำหรับ groups/resources
- [ ] Add loading states
- [ ] Implement optimistic updates
- [ ] Add error boundaries
- [ ] Implement retry logic สำหรับ failed requests

---

## 🎉 Success Criteria

Study Group feature พร้อมใช้งานเมื่อ:
- ✅ ทุก API endpoints ตอบ 200/201/204 ถูกต้อง
- ✅ Error responses ตรงตาม specification
- ✅ File upload ทำงานได้จริง
- ✅ Role-based access control ทำงานถูกต้อง
- ✅ Frontend สามารถเชื่อมต่อและใช้งานได้
- ✅ WebSocket chat ทำงานได้ (ถ้ามี)

**Study Group feature พร้อมใช้งานกับ Frontend แล้ว! 🚀**
