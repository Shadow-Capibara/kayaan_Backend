# Supabase Setup Guide

## 🔐 Security Update

**IMPORTANT**: Service Key has been moved to environment variable for security.

## Setup Instructions

### 1. Get Supabase Service Key

1. Go to [Supabase Dashboard](https://supabase.com/dashboard)
2. Select your project
3. Go to **Settings** > **API**
4. Copy the **service_role** key (NOT the anon key)

### 2. Set Environment Variable

#### Option A: Export in terminal
```bash
export SUPABASE_SERVICE_KEY="your_service_key_here"
```

#### Option B: Create .env file
```bash
# Copy example file
cp env.example .env

# Edit .env file and add your service key
SUPABASE_SERVICE_KEY=your_service_key_here
```

#### Option C: Set in IDE
- IntelliJ IDEA: Run Configuration > Environment Variables
- VS Code: .vscode/settings.json

### 3. Create Storage Bucket

1. Go to **Storage** in Supabase Dashboard
2. Create a new bucket named `avatars`
3. Set bucket as **public** (for avatar access)
4. Set RLS (Row Level Security) policies as needed

### 4. Test Configuration

```bash
# Test with curl
curl -X POST "http://localhost:8080/api/users/53/avatar-upload-url" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"test.jpg","contentType":"image/jpeg"}'
```

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `SUPABASE_SERVICE_KEY` | Supabase service role key | ✅ Yes |
| `SUPABASE_URL` | Supabase project URL | ❌ No (in application.yml) |

## Security Notes

- ✅ Service key is now in environment variable
- ✅ Old service key should be rotated in Supabase
- ✅ Never commit .env files to git
- ✅ Use service_role key for backend operations
- ✅ Use anon key for frontend operations

## Troubleshooting

### "Error creating signed upload URL"
1. Check if `SUPABASE_SERVICE_KEY` is set
2. Verify bucket `avatars` exists
3. Check Supabase project URL
4. Verify service key has storage permissions

### "Port 8080 already in use"
```bash
# Kill existing process
lsof -ti:8080 | xargs kill -9

# Or use different port
export SERVER_PORT=8081
```
