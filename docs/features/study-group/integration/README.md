# Study Group - Integration Guide

## 📋 Overview

This folder contains integration guides and checklists for connecting Study Group feature with Frontend applications.

## 📁 Files

- **[FRONTEND_INTEGRATION_CHECKLIST.md](./FRONTEND_INTEGRATION_CHECKLIST.md)** - Complete integration checklist

---

## 🚀 Quick Integration

### 1. Environment Setup
```bash
# Frontend .env
VITE_API_BASE_URL=http://localhost:8080/api
```

### 2. Authentication Setup
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

### 3. API Service
```javascript
// GroupService.js
const baseURL = import.meta.env.VITE_API_BASE_URL;

const createGroup = (data) => axios.post(`${baseURL}/groups`, data);
const getMyGroups = () => axios.get(`${baseURL}/groups/my`);
const getGroup = (id) => axios.get(`${baseURL}/groups/${id}`);
```

---

## 🔗 Key Integration Points

### Authentication
- JWT Bearer token required
- Token stored in localStorage
- Automatic token injection via Axios interceptor

### File Upload
- 3-step process: init → upload → complete
- Supabase Storage integration
- Signed URL upload flow

### Real-time Features
- WebSocket for chat
- Role-based UI updates
- Member status changes

---

## 📊 Integration Checklist

### ✅ Pre-flight Checklist
- [ ] Environment variables configured
- [ ] CORS settings correct
- [ ] Authentication working
- [ ] API endpoints accessible

### ✅ Core Features
- [ ] Group creation
- [ ] Member management
- [ ] File upload
- [ ] Role-based access

### ✅ Error Handling
- [ ] 401 Unauthorized
- [ ] 403 Forbidden
- [ ] 404 Not Found
- [ ] 409 Conflict

---

## 🐛 Common Integration Issues

### 1. CORS Errors
```javascript
// Backend CORS configuration
config.setAllowedOrigins(List.of("http://localhost:5173"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowCredentials(true);
```

### 2. Authentication Issues
```javascript
// Check token format
console.log('Token:', `Bearer ${token}`);

// Check token expiration
const tokenData = JSON.parse(atob(token.split('.')[1]));
console.log('Expires:', new Date(tokenData.exp * 1000));
```

### 3. File Upload Issues
```javascript
// Check Content-Type
const uploadFile = async (file, uploadUrl) => {
  await axios.put(uploadUrl, file, {
    headers: {
      'Content-Type': file.type // Must match mimeType
    }
  });
};
```

---

## 📈 Performance Considerations

### Optimization Tips
- Implement pagination for large datasets
- Add loading states
- Use optimistic updates
- Implement retry logic
- Add error boundaries

### Monitoring
- Track API response times
- Monitor error rates
- Log user interactions
- Track file upload success rates

---

## 🔗 Related Documentation

- **[API Reference](../api/STUDY_GROUP_API.md)**
- **[Testing Guide](../testing/README.md)**
- **[Main README](../README.md)**

---

## 📞 Support

For integration issues:
1. Check [Integration Checklist](./FRONTEND_INTEGRATION_CHECKLIST.md)
2. Review [API Documentation](../api/STUDY_GROUP_API.md)
3. Run [Test Script](../testing/test_study_group.sh)
4. Contact development team

---

**Happy integrating! 🔗**
