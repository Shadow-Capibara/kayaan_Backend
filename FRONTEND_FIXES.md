# 🔧 Frontend Fixes for Group Posts API

## 🚨 **ปัญหาที่พบ**

### 1. **CORS Policy Error**
- Frontend (localhost:5173) ไม่สามารถเข้าถึง Backend (localhost:8080) ได้
- Preflight request (OPTIONS) ไม่ผ่าน access control check

### 2. **URL Path Error** 
- URL มี double slash: `http://localhost:8080/api/groups//posts/`
- เกิดจาก `groupId` เป็น empty string หรือ null

### 3. **Request Format ไม่ตรงกัน**
- Frontend ส่ง FormData แต่ Backend รับ JSON สำหรับบาง endpoints

## ✅ **วิธีแก้ไข**

### 1. **แก้ไข GroupService.ts**

```typescript
// ใน GroupService.ts - แก้ไข likePost method
async likePost(groupId: string, postId: string) {
  try {
    console.log('🚀 Liking post:', groupId, 'post:', postId)
    
    // ตรวจสอบว่า groupId ไม่เป็น empty string
    if (!groupId || groupId.trim() === '') {
      throw new Error('Group ID is required')
    }
    
    const response = await api.post(`/groups/${groupId}/posts/${postId}/like`)
    console.log('✅ Post liked successfully:', response.data)
    return response.data
  } catch (error: unknown) {
    const axiosError = error as { response?: { status?: number; data?: unknown }; message?: string }
    console.error('❌ Backend API failed:', axiosError.response?.status, axiosError.message)
    throw axiosError
  }
}
```

### 2. **แก้ไข Group Store (group.ts)**

```typescript
// ใน group.ts - แก้ไข likePost method
const likePost = async (postId: string) => {
  try {
    // ตรวจสอบว่า currentGroupId มีค่า
    if (!currentGroupId.value) {
      throw new Error('No current group selected')
    }
    
    console.log('🔍 Current group ID:', currentGroupId.value)
    console.log('🔍 Post ID:', postId)
    
    // Use real API call
    const response = await GroupService.likePost(currentGroupId.value, postId)

    // Update local state
    if (groupPosts.value) {
      const postIndex = groupPosts.value.findIndex(p => p.id === postId)
      if (postIndex !== -1) {
        groupPosts.value[postIndex].likes = response.likesCount || groupPosts.value[postIndex].likes + 1
      }
    }

    return response
  } catch (err) {
    console.error('likePost error:', err)
    throw err
  }
}
```

### 3. **แก้ไข createPost method ใน GroupService.ts**

```typescript
// ใน GroupService.ts - แก้ไข createPost method
async createPost(groupId: string, payload: {
  title: string;
  description: string;
  content: string;
  contentType: 'TEXT' | 'IMAGE' | 'FILE' | 'MIXED';
  attachments?: File[];
  tags?: string[];
}) {
  try {
    console.log('🚀 Creating post in group:', groupId, 'with payload:', payload)

    // ตรวจสอบว่า groupId ไม่เป็น empty string
    if (!groupId || groupId.trim() === '') {
      throw new Error('Group ID is required')
    }

    // ใช้ endpoint ใหม่ที่รองรับ FormData
    const formData = new FormData()
    formData.append('title', payload.title)
    formData.append('description', payload.description)
    formData.append('content', payload.content)
    formData.append('contentType', payload.contentType)

    if (payload.tags && payload.tags.length > 0) {
      formData.append('tags', JSON.stringify(payload.tags))
    }

    if (payload.attachments && payload.attachments.length > 0) {
      payload.attachments.forEach((file, index) => {
        formData.append(`attachments`, file)
      })
    }

    // ใช้ endpoint /form-data แทน /posts
    const response = await api.post(`/groups/${groupId}/posts/form-data`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    console.log('✅ Post created successfully:', response.data)
    return response.data
  } catch (error: unknown) {
    const axiosError = error as { response?: { status?: number; data?: unknown }; message?: string }
    console.error('❌ Backend API failed:', axiosError.response?.status, axiosError.message)
    throw axiosError
  }
}
```

### 4. **แก้ไข GroupContent.vue**

```vue
<!-- ใน GroupContent.vue - แก้ไข handleLikePost method -->
<script setup>
const handleLikePost = async (postId: string) => {
  try {
    console.log('🔍 Attempting to like post:', postId)
    console.log('🔍 Current group ID from store:', groupStore.currentGroupId)
    
    // ตรวจสอบว่า currentGroupId มีค่า
    if (!groupStore.currentGroupId) {
      console.error('❌ No current group ID available')
      notificationStore.addNotification({
        type: 'error',
        title: 'Error',
        message: 'No group selected'
      })
      return
    }
    
    await groupStore.likePost(postId)
    
    notificationStore.addNotification({
      type: 'success',
      title: 'Success',
      message: 'Post liked successfully'
    })
  } catch (error) {
    console.error('Failed to like post:', error)
    notificationStore.addNotification({
      type: 'error',
      title: 'Error',
      message: 'Failed to like post'
    })
  }
}
</script>
```

### 5. **ตรวจสอบ setCurrentGroup**

```typescript
// ใน group.ts - แก้ไข setCurrentGroup method
const setCurrentGroup = (group: StudyGroup | null) => {
  currentGroup.value = group;
  currentGroupId.value = group?.id || null;
  console.log('🔍 Set current group:', group);
  console.log('🔍 Current group ID:', currentGroupId.value);
  
  // ตรวจสอบว่า group ID ไม่เป็น empty string
  if (currentGroupId.value === '') {
    console.warn('⚠️ Group ID is empty string, setting to null')
    currentGroupId.value = null
  }
};
```

## 🔍 **การ Debug เพิ่มเติม**

### 1. **เพิ่ม Logging ใน API Calls**

```typescript
// เพิ่มใน GroupService.ts
const logApiCall = (method: string, url: string, data?: any) => {
  console.log(`🚀 API Call: ${method} ${url}`)
  if (data) {
    console.log('📤 Request data:', data)
  }
}

const logApiResponse = (response: any) => {
  console.log('📥 API Response:', response.data)
  console.log('📊 Response status:', response.status)
}
```

### 2. **ตรวจสอบ Network Tab**
- เปิด Developer Tools > Network Tab
- ดูว่า request ถูกส่งไปที่ URL ที่ถูกต้องหรือไม่
- ตรวจสอบ response status และ headers

### 3. **ตรวจสอบ CORS Headers**
- ดูว่า response มี CORS headers ที่ถูกต้องหรือไม่
- ตรวจสอบ Access-Control-Allow-Origin header

## 🎯 **สรุปการแก้ไข**

1. **แก้ไข URL path** - ตรวจสอบ groupId ไม่เป็น empty string
2. **ใช้ endpoint ที่ถูกต้อง** - ใช้ `/form-data` สำหรับ FormData requests
3. **เพิ่ม error handling** - ตรวจสอบ parameters ก่อนส่ง request
4. **เพิ่ม logging** - เพื่อ debug ปัญหาได้ง่ายขึ้น

หลังจากแก้ไขแล้ว Frontend ควรจะสามารถเรียกใช้ Group Posts API ได้อย่างถูกต้อง!
