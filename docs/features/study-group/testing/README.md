# Study Group - Testing Guide

## 📋 Overview

This folder contains all testing resources for the Study Group feature.

## 📁 Files

- **[curl_examples.md](./curl_examples.md)** - Manual curl commands for testing
- **[test_study_group.sh](./test_study_group.sh)** - Automated test script

---

## 🧪 Testing Options

### 1. Automated Testing
```bash
# ตั้งค่า JWT token
export TOKEN="Bearer eyJhbGciOi..."

# รันสคริปต์ทดสอบอัตโนมัติ
chmod +x test_study_group.sh
./test_study_group.sh
```

### 2. Manual Testing
```bash
# ดูตัวอย่าง curl commands
cat curl_examples.md

# ทดสอบทีละขั้นตอน
curl -X POST http://localhost:8080/api/groups \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Group","description":"Test"}'
```

---

## 🔧 Test Setup

### Prerequisites
- Backend application running on `http://localhost:8080`
- Valid JWT token
- curl installed

### Environment Variables
```bash
# ตั้งค่า token
export TOKEN="Bearer your_jwt_token_here"

# ตั้งค่า base URL (optional)
export BASE_URL="http://localhost:8080"
```

---

## 📊 Test Coverage

### ✅ Covered Scenarios
- Group creation and management
- Member management
- File upload flow
- Role-based access control
- Error handling
- Authentication

### 🔄 Test Flow
1. Create group
2. Get group details
3. Generate invite token
4. List members
5. Test file upload
6. Test role updates
7. Test error scenarios
8. Clean up (delete group)

---

## 🐛 Debugging

### Common Issues
1. **401 Unauthorized**: Check JWT token
2. **404 Not Found**: Check group ID
3. **409 Conflict**: Check business rules
4. **500 Internal Error**: Check backend logs

### Logs
```bash
# ดู backend logs
tail -f logs/application.log

# ดู specific errors
grep "ERROR" logs/application.log
```

---

## 📈 Performance Testing

### Load Testing (Future)
```bash
# Example with Apache Bench
ab -n 100 -c 10 -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/groups/my
```

---

## 🔗 Related Documentation

- **[API Reference](../api/STUDY_GROUP_API.md)**
- **[Integration Guide](../integration/FRONTEND_INTEGRATION_CHECKLIST.md)**
- **[Main README](../README.md)**

---

**Happy testing! 🧪**
