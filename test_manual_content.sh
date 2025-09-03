#!/bin/bash

# Test script for manual content endpoint
echo "=== Testing Manual Content Endpoint ==="

# Test 1: Valid note content
echo "Test 1: Valid note content"
curl -X POST http://localhost:8080/api/content/manual \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsInN1YiI6IjMwMiIsImlhdCI6MTc1NjgzMDg0NCwiZXhwIjoxNzU2OTE3MjQ0fQ.hr8oDJTItZTf6Ouwg5PN7bSQNBa0pdsdhBL0QEnC7Bg" \
  -d '{
    "contentTitle": "Test Note",
    "contentType": "note",
    "contentData": "{\"type\":\"note\",\"content\":[{\"feature\":\"Test Feature\",\"description\":\"Test description\"}]}"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 2: Empty content array (should fail)
echo "Test 2: Empty content array (should fail)"
curl -X POST http://localhost:8080/api/content/manual \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsInN1YiI6IjMwMiIsImlhdCI6MTc1NjgzMDg0NCwiZXhwIjoxNzU2OTE3MjQ0fQ.hr8oDJTItZTf6Ouwg5PN7bSQNBa0pdsdhBL0QEnC7Bg" \
  -d '{
    "contentTitle": "Test Note",
    "contentType": "note",
    "contentData": "{\"type\":\"note\",\"content\":[]}"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 3: Missing content field (should fail)
echo "Test 3: Missing content field (should fail)"
curl -X POST http://localhost:8080/api/content/manual \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiZW1haWwiOiJ0ZXN0dXNlciIsInN1YiI6IjMwMiIsImlhdCI6MTc1NjgzMDg0NCwiZXhwIjoxNzU2OTE3MjQ0fQ.hr8oDJTItZTf6Ouwg5PN7bSQNBa0pdsdhBL0QEnC7Bg" \
  -d '{
    "contentTitle": "Test Note",
    "contentType": "note",
    "contentData": "{\"type\":\"note\"}"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n"

# Test 4: Invalid JSON (should fail)
echo "Test 4: Invalid JSON (should fail)"
curl -X POST http://localhost:8080/api/content/manual \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiZW1haWwiOiJ0ZXN0dXNlciIsInN1YiI6IjMwMiIsImlhdCI6MTc1NjgzMDg0NCwiZXhwIjoxNzU2OTE3MjQ0fQ.hr8oDJTItZTf6Ouwg5PN7bSQNBa0pdsdhBL0QEnC7Bg" \
  -d '{
    "contentTitle": "Test Note",
    "contentType": "note",
    "contentData": "{\"type\":\"note\",\"content\":[{\"feature\":\"Test Feature\"}"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n\n=== Test completed ==="
