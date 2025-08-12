# Study Group API - Curl Examples

## Setup
```bash
# ตั้งค่า JWT Token
export TOKEN="Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# ตั้งค่า Base URL
BASE_URL="http://localhost:8080"
```

## 1. Create Group
```bash
curl -X POST $BASE_URL/api/groups \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SE331 Review Group",
    "description": "Study group for SE331 final exam preparation"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "name": "SE331 Review Group",
  "description": "Study group for SE331 final exam preparation",
  "ownerId": 123,
  "createdAt": "2024-01-15T10:30:00"
}
```

## 2. Get My Groups
```bash
curl -H "Authorization: $TOKEN" $BASE_URL/api/groups/my
```

## 3. Get Group Details
```bash
curl -H "Authorization: $TOKEN" $BASE_URL/api/groups/1
```

## 4. Generate Invite Token
```bash
curl -X POST $BASE_URL/api/groups/1/invites \
  -H "Authorization: $TOKEN"
```

**Expected Response:**
```json
{
  "token": "abc123def456",
  "expiresAt": "2024-01-22T10:30:00"
}
```

## 5. Join Group by Token
```bash
curl -X POST $BASE_URL/api/groups/join \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "token": "abc123def456"
  }'
```

## 6. Get Group Members
```bash
curl -H "Authorization: $TOKEN" $BASE_URL/api/groups/1/members
```

**Expected Response:**
```json
[
  {
    "userId": 123,
    "role": "owner",
    "joinedAt": "2024-01-15T10:30:00"
  }
]
```

## 7. Update Member Role
```bash
curl -X PATCH $BASE_URL/api/groups/1/members/456 \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 456,
    "role": "moderator"
  }'
```

## 8. File Upload Flow

### 8.1 Initialize Upload
```bash
curl -X POST $BASE_URL/api/groups/1/resources/upload-url \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "lecture-notes.pdf",
    "mimeType": "application/pdf",
    "size": 1048576
  }'
```

**Expected Response:**
```json
{
  "uploadUrl": "https://storage.supabase.co/...",
  "fileUrl": "https://storage.supabase.co/..."
}
```

### 8.2 Upload File (PUT to signed URL)
```bash
curl -X PUT "UPLOAD_URL_FROM_STEP_8.1" \
  -H "Content-Type: application/pdf" \
  --data-binary @lecture-notes.pdf
```

### 8.3 Complete Upload
```bash
curl -X POST $BASE_URL/api/groups/1/resources \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "lecture-notes.pdf",
    "fileUrl": "FILE_URL_FROM_STEP_8.1",
    "title": "Week 1 Lecture Notes",
    "description": "Introduction to Software Engineering",
    "tags": ["lecture", "week1", "introduction"]
  }'
```

## 9. Get Group Resources
```bash
curl -H "Authorization: $TOKEN" $BASE_URL/api/groups/1/resources
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "title": "Week 1 Lecture Notes",
    "description": "Introduction to Software Engineering",
    "fileUrl": "https://storage.supabase.co/...",
    "mimeType": "application/pdf",
    "fileSize": 1048576,
    "tags": ["lecture", "week1", "introduction"],
    "uploaderId": 123,
    "createdAt": "2024-01-15T10:35:00"
  }
]
```

## 10. Leave Group
```bash
curl -X POST $BASE_URL/api/groups/1/leave \
  -H "Authorization: $TOKEN"
```

## 11. Delete Group (Owner only)
```bash
curl -X DELETE $BASE_URL/api/groups/1 \
  -H "Authorization: $TOKEN"
```

## Error Response Examples

### 401 Unauthorized
```bash
curl -H "Authorization: Bearer invalid_token" $BASE_URL/api/groups/1
```

**Response:**
```json
{
  "error": "Authentication required"
}
```

### 403 Forbidden
```bash
# Non-owner trying to delete group
curl -X DELETE $BASE_URL/api/groups/1 \
  -H "Authorization: $TOKEN_NON_OWNER"
```

**Response:**
```json
{
  "error": "Only the owner can delete the group"
}
```

### 404 Not Found
```bash
curl -H "Authorization: $TOKEN" $BASE_URL/api/groups/999
```

**Response:**
```json
{
  "error": "Group not found"
}
```

### 409 Conflict
```bash
# Owner trying to leave when they're the only member
curl -X POST $BASE_URL/api/groups/1/leave \
  -H "Authorization: $TOKEN_OWNER"
```

**Response:**
```json
{
  "error": "Owner cannot leave the group if they are the only member"
}
```

## Test Script Usage

```bash
# ให้สิทธิ์การรัน
chmod +x test_study_group.sh

# รันสคริปต์ทดสอบ
./test_study_group.sh
```

## Environment Variables

```bash
# .env file
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_KEY=your_service_key_here
SUPABASE_BUCKET_LIBRARY=library
```

## CORS Configuration

Backend ตั้งค่า CORS สำหรับ:
- Origin: `http://localhost:5173` (Vite dev server)
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: *
- Credentials: true
