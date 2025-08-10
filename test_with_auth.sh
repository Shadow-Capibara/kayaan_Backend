#!/bin/bash

# Test script for avatar upload URL endpoint with authentication
# Usage: ./test_with_auth.sh <USER_ID>

USER_ID=$1

if [ -z "$USER_ID" ]; then
    echo "Usage: $0 <USER_ID>"
    echo "Example: $0 103"
    exit 1
fi

echo "Testing avatar upload URL endpoint for user $USER_ID"
echo "=================================================="

# First, let's try to get a valid JWT token by logging in
echo "Step 1: Attempting to get JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/v1/auth/authenticate" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}' \
  -w "\nHTTP Status: %{http_code}")

echo "Login response: $LOGIN_RESPONSE"

# Extract JWT token from response (assuming it's in the format {"access_token": "..."})
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "Failed to get JWT token. Please provide a valid token manually:"
    echo "export JWT_TOKEN='your_jwt_token_here'"
    echo "Then run: ./test_avatar_upload.sh \"\$JWT_TOKEN\" $USER_ID"
    exit 1
fi

echo "JWT Token obtained: ${JWT_TOKEN:0:20}..."

# Now test the avatar upload endpoint
echo -e "\nStep 2: Testing avatar upload URL endpoint..."

# Test 1: Valid request with standard field names
echo "Test 1: Valid JSON request with standard field names"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"IMG_6045.jpeg","contentType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 2: Valid request with filename alias
echo "Test 2: Valid JSON request with filename alias"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"filename":"IMG_6045.jpeg","contentType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 3: Valid request with mimeType alias
echo "Test 3: Valid JSON request with mimeType alias"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"IMG_6045.jpeg","mimeType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 4: Missing fileName (should return 400)
echo "Test 4: Missing fileName (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"contentType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\nTest completed!"
