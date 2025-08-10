# Kayaan Backend - Supabase Integration

## Environment Variables

Set the following environment variables for Supabase integration:

```bash
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_KEY=your-service-role-key
SUPABASE_BUCKET_AVATARS=avatars
SUPABASE_BUCKET_AI=ai-outputs
```

## Testing Avatar Flow

### 1. Get Signed Upload URL

```bash
curl -X POST "http://localhost:8080/api/users/1/avatar-upload-url" \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "me.jpg",
    "contentType": "image/jpeg"
  }'
```

Response:
```json
{
  "signedUrl": "https://your-project.supabase.co/storage/v1/object/sign/avatars/users/1/1699999999999_me.jpg?token=...",
  "path": "users/1/1699999999999_me.jpg",
  "expiresIn": 600
}
```

### 2. Upload File to Signed URL

```bash
curl -X PUT "https://your-project.supabase.co/storage/v1/object/sign/avatars/users/1/1699999999999_me.jpg?token=..." \
  -H "Content-Type: image/jpeg" \
  --data-binary @/path/to/your/image.jpg
```

### 3. Save Avatar Path

```bash
curl -X PUT "http://localhost:8080/api/users/1/avatar-url" \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "path": "users/1/1699999999999_me.jpg"
  }'
```

## Testing AI Job Upload

```bash
curl -X POST "http://localhost:8080/api/ai/jobs/job123/request-upload" \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "output.pdf",
    "contentType": "application/pdf"
  }'
```

## WebSocket Testing

Connect to WebSocket endpoint:
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    // Subscribe to group messages
    stompClient.subscribe('/topic/groups/1', function (message) {
        console.log('Received message:', JSON.parse(message.body));
    });
    
    // Send a message
    stompClient.send("/app/groups/1/send", {}, JSON.stringify({
        groupId: 1,
        userId: 1,
        content: "Hello, group!"
    }));
});
```

## Bucket Setup

Ensure the following buckets exist in your Supabase project:
- `avatars` (private)
- `ai-outputs` (private)

## Security Notes

- All endpoints require JWT authentication
- Avatar endpoints require ownership or ADMIN role
- AI job endpoints require authentication
- No Supabase keys are exposed to the frontend
