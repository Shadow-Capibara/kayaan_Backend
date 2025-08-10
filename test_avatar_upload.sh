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

echo -e "\n\n"

# Test 5: Missing contentType (should return 400)
echo "Test 5: Missing contentType (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"IMG_6045.jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 6: Empty fileName (should return 400)
echo "Test 6: Empty fileName (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"","contentType":"image/jpeg"}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 7: Empty contentType (should return 400)
echo "Test 7: Empty contentType (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fileName":"IMG_6045.jpeg","contentType":""}' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 8: Wrong Content-Type header (should return 400)
echo "Test 8: Wrong Content-Type header (should return 400)"
curl -X POST "http://localhost:8080/api/users/$USER_ID/avatar-upload-url" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'fileName=IMG_6045.jpeg&contentType=image/jpeg' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\nTest completed!"
