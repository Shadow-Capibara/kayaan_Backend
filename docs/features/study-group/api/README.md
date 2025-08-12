# Study Group - API Documentation

## 📋 Overview

This folder contains complete API documentation for the Study Group feature.

## 📁 Files

- **[STUDY_GROUP_API.md](./STUDY_GROUP_API.md)** - Complete API reference with examples

---

## 🚀 Quick API Reference

### Base URL
```
http://localhost:8080/api
```

### Authentication
```
Authorization: Bearer <jwt_token>
```

### Core Endpoints
```
POST   /groups                    - Create group
GET    /groups/my                 - Get my groups
GET    /groups/{id}               - Get group details
POST   /groups/join               - Join by token
POST   /groups/{id}/leave         - Leave group
DELETE /groups/{id}               - Delete group

GET    /groups/{id}/members       - Get members
PATCH  /groups/{id}/members/{userId} - Update role
DELETE /groups/{id}/members/{userId} - Remove member

GET    /groups/{id}/resources     - Get resources
POST   /groups/{id}/resources/upload-url - Init upload
POST   /groups/{id}/resources     - Complete upload
DELETE /groups/{id}/resources/{id} - Delete resource
```

---

## 🔧 API Features

### Role System
- **Owner**: Full access, can delete group
- **Moderator**: Manage members and content
- **Member**: View and upload content

### File Upload Flow
1. **Init**: Get signed URL
2. **Upload**: PUT file to signed URL
3. **Complete**: Save metadata

### Error Handling
- 401: Authentication required
- 403: Access denied
- 404: Not found
- 409: Conflict (business rules)

---

## 📊 Response Formats

### Success Response
```json
{
  "id": 1,
  "name": "Study Group",
  "description": "Description",
  "ownerId": 123,
  "createdAt": "2024-01-15T10:30:00"
}
```

### Error Response
```json
{
  "error": "Error message"
}
```

---

## 🔗 Related Documentation

- **[Complete API Reference](./STUDY_GROUP_API.md)**
- **[Testing Guide](../testing/README.md)**
- **[Integration Guide](../integration/README.md)**
- **[Main README](../README.md)**

---

**Happy API development! 🔌**
