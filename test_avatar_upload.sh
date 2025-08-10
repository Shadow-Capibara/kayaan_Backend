#!/bin/bash

# Test script for avatar upload URL endpoint
# Usage: ./test_avatar_upload.sh <JWT_TOKEN> <USER_ID>

JWT_TOKEN=$1
USER_ID=$2

if [ -z "$JWT_TOKEN" ] || [ -z "$USER_ID" ]; then
    echo "Usage: $0 <JWT_TOKEN> <USER_ID>"
    echo "Example: $0 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...' 103"
    exit 1
fi

echo "Testing avatar upload URL endpoint for user $USER_ID"
echo "=================================================="

# Test 1: Valid request
echo "Test 1: Valid JSON request"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"test.jpg","contentType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 2: Missing fileName
echo "Test 2: Missing fileName (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"contentType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 3: Missing contentType
echo "Test 3: Missing contentType (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"test.jpg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 4: Wrong field names
echo "Test 4: Wrong field names (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.jpg","mimeType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 5: Empty values
echo "Test 5: Empty values (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"","contentType":""}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\nTest completed!"
