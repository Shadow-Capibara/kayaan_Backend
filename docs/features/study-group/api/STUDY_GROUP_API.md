# Study Group API Documentation

## Overview
Study Group feature allows users to create, join, and manage study groups with file sharing and real-time chat capabilities.

## Authentication
All endpoints require authentication via JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## API Endpoints

### Study Group Management

#### Create Group
```
POST /api/groups
Content-Type: application/json

{
  "name": "Study Group Name",
  "description": "Group description"
}
```

#### Get My Groups
```
GET /api/groups/my
```

#### Get Group Details
```
GET /api/groups/{groupId}
```

#### Join Group by Token
```
POST /api/groups/join
Content-Type: application/json

{
  "token": "invite_token_here"
}
```

#### Leave Group
```
POST /api/groups/{groupId}/leave
```

#### Delete Group (Owner only)
```
DELETE /api/groups/{groupId}
```

#### Generate Invite Token
```
POST /api/groups/{groupId}/invites
```

### Member Management

#### Get Group Members
```
GET /api/groups/{groupId}/members
```

#### Update Member Role
```
PATCH /api/groups/{groupId}/members/{userId}
Content-Type: application/json

{
  "userId": 123,
  "role": "moderator"
}
```

#### Remove Member
```
DELETE /api/groups/{groupId}/members/{userId}
```

#### Invite by Email
```
POST /api/groups/{groupId}/invite-by-email
Content-Type: application/json

{
  "email": "user@example.com"
}
```

### Resource Management

#### List Resources
```
GET /api/groups/{groupId}/resources
```

#### Initialize Upload
```
POST /api/groups/{groupId}/resources/upload-url
Content-Type: application/json

{
  "fileName": "document.pdf",
  "mimeType": "application/pdf",
  "size": 1024000
}
```

#### Complete Upload
```
POST /api/groups/{groupId}/resources
Content-Type: application/json

{
  "fileName": "document.pdf",
  "fileUrl": "https://storage.example.com/file.pdf",
  "title": "Study Document",
  "description": "Important study material",
  "tags": ["math", "calculus"]
}
```

#### Delete Resource
```
DELETE /api/groups/{groupId}/resources/{resourceId}
```

## Roles and Permissions

### Owner
- Can delete the group
- Can manage all members (promote, demote, remove)
- Can manage all resources
- Cannot leave if they are the only member

### Moderator
- Can manage members (promote to moderator, remove non-owners)
- Can manage resources
- Cannot promote to owner or demote owners

### Member
- Can view group content
- Can upload resources
- Can leave the group
- Cannot manage other members or delete resources they didn't upload

## File Upload Flow

1. **Initialize Upload**: Call `/api/groups/{groupId}/resources/upload-url` to get signed URL
2. **Upload File**: Use the signed URL to upload file directly to Supabase Storage
3. **Complete Upload**: Call `/api/groups/{groupId}/resources` to save metadata

## WebSocket Chat

### Connect to Chat
```
WebSocket: /ws
Destination: /topic/groups/{groupId}
```

### Send Message
```
Destination: /app/groups/{groupId}/chat
Content-Type: application/json

{
  "content": "Hello everyone!"
}
```

## Error Responses

### 400 Bad Request
```json
{
  "error": "Invalid or expired invite token"
}
```

### 401 Unauthorized
```json
{
  "error": "Authentication required"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied: Only owners can delete the group"
}
```

### 404 Not Found
```json
{
  "error": "Group not found"
}
```

### 409 Conflict
```json
{
  "error": "Owner cannot leave the group if they are the only member"
}
```

## Environment Variables

Add these to your environment:
```
SUPABASE_URL=your_supabase_url
SUPABASE_SERVICE_KEY=your_service_key
SUPABASE_BUCKET_LIBRARY=library
```

## Database Schema

The feature uses the following tables:
- `study_group`: Main group information
- `group_member`: Group membership and roles
- `group_content`: Shared resources
- `group_invite`: Invitation tokens
- `group_message`: Chat messages
