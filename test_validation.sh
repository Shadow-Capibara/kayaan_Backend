#!/bin/bash

echo "🧪 Testing Manual Content Validation Endpoint..."

# Test 1: Valid Flashcard
echo "📝 Test 1: Valid Flashcard JSON..."
curl -X POST http://localhost:8080/api/content/manual/test \
  -H "Content-Type: application/json" \
  -d '{
    "contentTitle": "Test Flashcard",
    "contentType": "flashcard",
    "contentData": "{\"type\":\"flashcard\",\"topic\":\"Test Topic\",\"flashcards\":[{\"question\":\"What is 2+2?\",\"answer\":\"4\"}]}",
    "subject": "Test",
    "difficulty": "Easy",
    "tags": ["test"]
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 2: Missing contentTitle
echo "📝 Test 2: Missing contentTitle..."
curl -X POST http://localhost:8080/api/content/manual/test \
  -H "Content-Type: application/json" \
  -d '{
    "contentType": "flashcard",
    "contentData": "{\"type\":\"flashcard\",\"topic\":\"Test Topic\",\"flashcards\":[{\"question\":\"What is 2+2?\",\"answer\":\"4\"}]}",
    "subject": "Test",
    "difficulty": "Easy",
    "tags": ["test"]
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 3: Invalid contentType
echo "📝 Test 3: Invalid contentType..."
curl -X POST http://localhost:8080/api/content/manual/test \
  -H "Content-Type: application/json" \
  -d '{
    "contentTitle": "Test Content",
    "contentType": "invalid_type",
    "contentData": "{\"type\":\"invalid\",\"topic\":\"Test Topic\"}",
    "subject": "Test",
    "difficulty": "Easy",
    "tags": ["test"]
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 4: Invalid JSON in contentData
echo "📝 Test 4: Invalid JSON in contentData..."
curl -X POST http://localhost:8080/api/content/manual/test \
  -H "Content-Type: application/json" \
  -d '{
    "contentTitle": "Test Content",
    "contentType": "flashcard",
    "contentData": "invalid json string",
    "subject": "Test",
    "difficulty": "Easy",
    "tags": ["test"]
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 5: Missing type field in JSON
echo "📝 Test 5: Missing type field in JSON..."
curl -X POST http://localhost:8080/api/content/manual/test \
  -H "Content-Type: application/json" \
  -d '{
    "contentTitle": "Test Content",
    "contentType": "flashcard",
    "contentData": "{\"topic\":\"Test Topic\",\"flashcards\":[{\"question\":\"What is 2+2?\",\"answer\":\"4\"}]}",
    "subject": "Test",
    "difficulty": "Easy",
    "tags": ["test"]
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 6: Empty flashcards array
echo "📝 Test 6: Empty flashcards array..."
curl -X POST http://localhost:8080/api/content/manual/test \
  -H "Content-Type: application/json" \
  -d '{
    "contentTitle": "Test Content",
    "contentType": "flashcard",
    "contentData": "{\"type\":\"flashcard\",\"topic\":\"Test Topic\",\"flashcards\":[]}",
    "subject": "Test",
    "difficulty": "Easy",
    "tags": ["test"]
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n✅ Validation testing completed!"
