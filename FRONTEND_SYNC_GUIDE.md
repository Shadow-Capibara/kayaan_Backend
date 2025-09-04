# 🚀 Frontend Sync Guide - Study Group Feature

## 📋 Overview
คู่มือการ sync ระหว่าง Backend และ Frontend สำหรับ Study Group feature ที่เพิ่งได้รับการแก้ไข

---

## 🔧 Backend Changes ที่เพิ่งทำ

### 1. **แก้ไข StudyGroupServiceImpl.java**
- ✅ สร้าง invite token อัตโนมัติเมื่อสร้าง group ใหม่
- ✅ สร้าง invite code สั้น 6 ตัวอักษร (แทน UUID ที่ยาว)
- ✅ รองรับการ join ด้วย invite code และ token
- ✅ ปรับปรุง validation ให้รองรับทั้งสองแบบ

### 2. **สร้าง Invite Tokens สำหรับ Groups ที่มีอยู่**
- ✅ รัน SQL script เพื่อสร้าง invite tokens สำหรับ groups ทั้งหมด
- ✅ ได้ 7 invite codes สำหรับ groups ที่มีอยู่แล้ว
- ✅ หมดอายุใน 30 วัน

---

## 🎯 Frontend Changes ที่ต้องทำ

### **1. Authentication & Authorization**

#### **Login Flow:**
```javascript
// 1. Login เพื่อรับ JWT token
const login = async (credentials) => {
  try {
    const response = await axios.post('/api/auth/login', credentials);
    const { accessToken, refreshToken } = response.data;
    
    // เก็บ token ใน localStorage หรือ auth store
    localStorage.setItem('token', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    
    // เก็บ user info
    localStorage.setItem('user', JSON.stringify(response.data.user));
    
    return response.data;
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
};
```

#### **Axios Interceptor Setup:**
```javascript
// ตั้งค่า Axios interceptor เพื่อส่ง JWT token อัตโนมัติ
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle 401 responses
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired - redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      router.push('/login');
    }
    return Promise.reject(error);
  }
);
```

### **2. Study Group API Integration**

#### **API Base Configuration:**
```javascript
// .env file
VITE_API_BASE_URL=http://localhost:8080/api

// API service configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// Study Group API service
class StudyGroupService {
  // Create new group
  async createGroup(groupData) {
    return axios.post(`${API_BASE_URL}/groups`, groupData);
  }

  // Get user's groups
  async getMyGroups() {
    return axios.get(`${API_BASE_URL}/groups/my`);
  }

  // Get group details
  async getGroup(groupId) {
    return axios.get(`${API_BASE_URL}/groups/${groupId}`);
  }

  // Join group by invite code
  async joinGroup(inviteCode) {
    return axios.post(`${API_BASE_URL}/groups/join`, {
      token: inviteCode
    });
  }

  // Get group invite code
  async getGroupInviteCode(groupId) {
    return axios.get(`${API_BASE_URL}/groups/${groupId}/invite-code`);
  }

  // Generate new invite
  async generateInvite(groupId, expiryDays = 30) {
    return axios.post(`${API_BASE_URL}/groups/${groupId}/invites?expiryDays=${expiryDays}`);
  }

  // Leave group
  async leaveGroup(groupId) {
    return axios.post(`${API_BASE_URL}/groups/${groupId}/leave?confirm=true`);
  }

  // Delete group (owner only)
  async deleteGroup(groupId) {
    return axios.delete(`${API_BASE_URL}/groups/${groupId}?confirm=true`);
  }
}
```

### **3. Study Groups Page Updates**

#### **Replace Mock Data with Real API:**
```vue
<template>
  <div class="study-groups-page">
    <!-- Header -->
    <div class="page-header">
      <h1>Study Groups</h1>
      <p>Collaborate, share resources, and learn together with your study groups</p>
    </div>

    <!-- Statistics -->
    <div class="stats-grid">
      <div class="stat-card">
        <h3>{{ stats.totalGroups }}</h3>
        <p>Total Groups</p>
      </div>
      <div class="stat-card">
        <h3>{{ stats.ownedGroups }}</h3>
        <p>Owned Groups</p>
      </div>
      <div class="stat-card">
        <h3>{{ stats.memberGroups }}</h3>
        <p>Member Groups</p>
      </div>
      <div class="stat-card">
        <h3>{{ stats.totalMembers }}</h3>
        <p>Total Members</p>
      </div>
    </div>

    <!-- Create New Group -->
    <div class="create-group-section">
      <h2>Create New Study Group</h2>
      <form @submit.prevent="createGroup">
        <input 
          v-model="newGroup.name" 
          type="text" 
          placeholder="Group Name" 
          required 
        />
        <textarea 
          v-model="newGroup.description" 
          placeholder="Description" 
          rows="3"
        ></textarea>
        <button type="submit" :disabled="isCreating">
          {{ isCreating ? 'Creating...' : 'Create Group' }}
        </button>
      </form>
    </div>

    <!-- Join by Code -->
    <div class="join-group-section">
      <h2>Join Study Group</h2>
      <form @submit.prevent="joinGroup">
        <input 
          v-model="inviteCode" 
          type="text" 
          placeholder="Enter Invite Code" 
          required 
        />
        <button type="submit" :disabled="isJoining">
          {{ isJoining ? 'Joining...' : 'Join Group' }}
        </button>
      </form>
    </div>

    <!-- My Groups -->
    <div class="my-groups-section">
      <h2>My Groups</h2>
      <div v-if="isLoading" class="loading">Loading groups...</div>
      <div v-else-if="groups.length === 0" class="no-groups">
        <p>You haven't joined any groups yet.</p>
        <p>Create a new group or join an existing one to get started!</p>
      </div>
      <div v-else class="groups-grid">
        <div 
          v-for="group in groups" 
          :key="group.id" 
          class="group-card"
        >
          <h3>{{ group.name }}</h3>
          <p>{{ group.description }}</p>
          <div class="group-meta">
            <span>{{ group.memberCount || 0 }} members</span>
            <span>{{ formatDate(group.createdAt) }}</span>
          </div>
          <div class="group-actions">
            <button @click="viewGroup(group.id)">View Group</button>
            <button @click="getInviteCode(group.id)">Get Invite Code</button>
            <button 
              v-if="group.ownerId === currentUser.id"
              @click="deleteGroup(group.id)"
              class="danger"
            >
              Delete Group
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { StudyGroupService } from '@/services/StudyGroupService';
import { useAuthStore } from '@/stores/auth';

// Services & Stores
const studyGroupService = new StudyGroupService();
const authStore = useAuthStore();
const router = useRouter();

// Reactive data
const groups = ref([]);
const isLoading = ref(false);
const isCreating = ref(false);
const isJoining = ref(false);
const inviteCode = ref('');
const newGroup = ref({
  name: '',
  description: ''
});

// Computed properties
const currentUser = computed(() => authStore.user);
const stats = computed(() => {
  const ownedGroups = groups.value.filter(g => g.ownerId === currentUser.value?.id).length;
  const memberGroups = groups.value.filter(g => g.ownerId !== currentUser.value?.id).length;
  const totalMembers = groups.value.reduce((sum, g) => sum + (g.memberCount || 0), 0);
  
  return {
    totalGroups: groups.value.length,
    ownedGroups,
    memberGroups,
    totalMembers
  };
});

// Methods
const loadGroups = async () => {
  try {
    isLoading.value = true;
    const response = await studyGroupService.getMyGroups();
    groups.value = response.data;
  } catch (error) {
    console.error('Failed to load groups:', error);
    // Show error message to user
  } finally {
    isLoading.value = false;
  }
};

const createGroup = async () => {
  try {
    isCreating.value = true;
    await studyGroupService.createGroup(newGroup.value);
    
    // Reset form
    newGroup.value = { name: '', description: '' };
    
    // Reload groups
    await loadGroups();
    
    // Show success message
    alert('Group created successfully!');
  } catch (error) {
    console.error('Failed to create group:', error);
    alert('Failed to create group: ' + error.response?.data?.message || error.message);
  } finally {
    isCreating.value = false;
  }
};

const joinGroup = async () => {
  try {
    isJoining.value = true;
    await studyGroupService.joinGroup(inviteCode.value);
    
    // Reset form
    inviteCode.value = '';
    
    // Reload groups
    await loadGroups();
    
    // Show success message
    alert('Successfully joined the group!');
  } catch (error) {
    console.error('Failed to join group:', error);
    alert('Failed to join group: ' + error.response?.data?.message || error.message);
  } finally {
    isJoining.value = false;
  }
};

const getInviteCode = async (groupId) => {
  try {
    const response = await studyGroupService.getGroupInviteCode(groupId);
    const { token } = response.data;
    
    // Copy to clipboard
    await navigator.clipboard.writeText(token);
    alert(`Invite code copied to clipboard: ${token}`);
  } catch (error) {
    console.error('Failed to get invite code:', error);
    alert('Failed to get invite code: ' + error.response?.data?.message || error.message);
  }
};

const viewGroup = (groupId) => {
  router.push(`/study-groups/${groupId}`);
};

const deleteGroup = async (groupId) => {
  if (!confirm('Are you sure you want to delete this group? This action cannot be undone.')) {
    return;
  }
  
  try {
    await studyGroupService.deleteGroup(groupId);
    await loadGroups();
    alert('Group deleted successfully!');
  } catch (error) {
    console.error('Failed to delete group:', error);
    alert('Failed to delete group: ' + error.response?.data?.message || error.message);
  }
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString();
};

// Lifecycle
onMounted(() => {
  loadGroups();
});
</script>
```

### **4. Router Guards & Authentication**

#### **Router Configuration:**
```javascript
// router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const routes = [
  {
    path: '/study-groups',
    name: 'StudyGroups',
    component: () => import('@/views/StudyGroups.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/study-groups/:id',
    name: 'StudyGroupDetail',
    component: () => import('@/views/StudyGroupDetail.vue'),
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});

export default router;
```

#### **Auth Store (Pinia):**
```javascript
// stores/auth.js
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref(null);
  const token = ref(localStorage.getItem('token') || null);
  const refreshToken = ref(localStorage.getItem('refreshToken') || null);

  // Getters
  const isAuthenticated = computed(() => !!token.value);
  const userRole = computed(() => user.value?.roles?.[0] || 'USER');

  // Actions
  const setUser = (userData) => {
    user.value = userData;
  };

  const setToken = (newToken) => {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  };

  const setRefreshToken = (newRefreshToken) => {
    refreshToken.value = newRefreshToken;
    localStorage.setItem('refreshToken', newRefreshToken);
  };

  const login = (authData) => {
    setUser(authData.user);
    setToken(authData.accessToken);
    setRefreshToken(authData.refreshToken);
  };

  const logout = () => {
    user.value = null;
    token.value = null;
    refreshToken.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  };

  const refreshAuth = async () => {
    try {
      const response = await axios.post('/api/auth/refresh', {
        refreshToken: refreshToken.value
      });
      
      setToken(response.data.accessToken);
      setRefreshToken(response.data.refreshToken);
      
      return true;
    } catch (error) {
      logout();
      return false;
    }
  };

  return {
    user,
    token,
    refreshToken,
    isAuthenticated,
    userRole,
    setUser,
    setToken,
    setRefreshToken,
    login,
    logout,
    refreshAuth
  };
});
```

### **5. Error Handling & User Feedback**

#### **Toast/Notification System:**
```javascript
// composables/useToast.js
import { ref } from 'vue';

export function useToast() {
  const toasts = ref([]);

  const showToast = (message, type = 'info', duration = 5000) => {
    const id = Date.now();
    const toast = {
      id,
      message,
      type,
      duration
    };

    toasts.value.push(toast);

    setTimeout(() => {
      removeToast(id);
    }, duration);

    return id;
  };

  const removeToast = (id) => {
    const index = toasts.value.findIndex(t => t.id === id);
    if (index > -1) {
      toasts.value.splice(index, 1);
    }
  };

  const success = (message) => showToast(message, 'success');
  const error = (message) => showToast(message, 'error');
  const warning = (message) => showToast(message, 'warning');
  const info = (message) => showToast(message, 'info');

  return {
    toasts,
    showToast,
    removeToast,
    success,
    error,
    warning,
    info
  };
}
```

#### **API Error Handling:**
```javascript
// utils/apiErrorHandler.js
export const handleApiError = (error, toast) => {
  let message = 'An unexpected error occurred';

  if (error.response) {
    // Server responded with error status
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        message = data.message || 'Invalid request data';
        break;
      case 401:
        message = 'Authentication required. Please login again.';
        // Redirect to login
        router.push('/login');
        break;
      case 403:
        message = 'Access denied. You don\'t have permission to perform this action.';
        break;
      case 404:
        message = 'Resource not found.';
        break;
      case 409:
        message = data.message || 'Conflict with current state.';
        break;
      case 500:
        message = 'Server error. Please try again later.';
        break;
      default:
        message = data.message || `Error ${status}: ${data.error || 'Unknown error'}`;
    }
  } else if (error.request) {
    // Request was made but no response received
    message = 'Network error. Please check your connection.';
  } else {
    // Something else happened
    message = error.message || 'An unexpected error occurred';
  }

  // Show error toast
  toast.error(message);
  
  // Log error for debugging
  console.error('API Error:', error);
  
  return message;
};
```

---

## 🧪 Testing Checklist

### **Authentication Testing:**
- [ ] Login flow ทำงานได้
- [ ] JWT token ถูกเก็บและส่งไปกับทุก request
- [ ] 401 responses ถูก handle และ redirect ไป login
- [ ] Token refresh ทำงานได้

### **Study Group API Testing:**
- [ ] Create group ทำงานได้
- [ ] Get my groups ทำงานได้
- [ ] Join group ด้วย invite code ทำงานได้
- [ ] Get invite code ทำงานได้
- [ ] Generate new invite ทำงานได้
- [ ] Leave group ทำงานได้
- [ ] Delete group (owner only) ทำงานได้

### **Error Handling Testing:**
- [ ] Network errors แสดงข้อความที่เหมาะสม
- [ ] Validation errors แสดงข้อความที่ชัดเจน
- [ ] Permission errors แสดงข้อความที่เข้าใจง่าย
- [ ] Toast notifications ทำงานได้

### **UI/UX Testing:**
- [ ] Loading states แสดงระหว่าง API calls
- [ ] Form validation ทำงานได้
- [ ] Responsive design ทำงานได้บนทุก device
- [ ] Accessibility features ทำงานได้

---

## 🚀 Deployment Checklist

### **Environment Variables:**
```bash
# .env.production
VITE_API_BASE_URL=https://your-production-api.com/api
VITE_APP_NAME=Kayaan Learning Hub
VITE_APP_VERSION=1.0.0
```

### **Build & Deploy:**
```bash
# Build production
npm run build

# Deploy to hosting service
# (Vercel, Netlify, AWS S3, etc.)
```

### **Post-Deployment:**
- [ ] Test all features on production
- [ ] Monitor error logs
- [ ] Check performance metrics
- [ ] Verify API endpoints accessibility

---

## 📞 Support & Troubleshooting

### **Common Issues:**
1. **CORS errors** - ตรวจสอบ backend CORS configuration
2. **Authentication failures** - ตรวจสอบ JWT token format และ expiration
3. **API endpoint mismatches** - ตรวจสอบ URL paths และ HTTP methods
4. **Database connection issues** - ตรวจสอบ MySQL connection และ credentials

### **Debug Tools:**
- Browser DevTools (Network, Console)
- Vue DevTools
- API testing tools (Postman, Insomnia)
- Database management tools (phpMyAdmin)

---

## 🎉 Success Criteria

✅ **User สามารถ login และได้รับ JWT token**  
✅ **User สามารถสร้าง study group ใหม่ได้**  
✅ **User สามารถ join group ด้วย invite code ได้**  
✅ **User สามารถดู groups ที่เป็นสมาชิกได้**  
✅ **User สามารถแชร์ invite code ให้คนอื่นได้**  
✅ **Error handling ทำงานได้อย่างเหมาะสม**  
✅ **UI responsive และ user-friendly**  

---

**🎯 Goal: Study Group feature ทำงานได้อย่างสมบูรณ์และ user สามารถใช้งานได้โดยไม่มีปัญหา!**
