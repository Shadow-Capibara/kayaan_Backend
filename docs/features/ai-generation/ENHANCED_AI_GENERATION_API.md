# 🚀 Enhanced AI Generation API Documentation

## 📋 Overview

This document describes the enhanced AI Generation API that now supports:
- **File Upload & Processing**: PDF, DOCX, TXT, Images
- **Real-time Progress Tracking**: WebSocket-based progress updates
- **Multi-step Generation Process**: Step-by-step content generation
- **Advanced Content Types**: Summary, Quiz, Flashcards, Notes

## 🔌 API Endpoints

### 1. Create Generation Request with File Upload

**Endpoint:** `POST /api/ai/generation/request`

**Description:** Create a new AI generation request with optional file upload

**Request Format:** `multipart/form-data`

**Parameters:**
- `request` (JSON): Generation request data
- `file` (optional): Uploaded file (PDF, DOCX, TXT, Image)

**Request Body:**
```json
{
  "promptText": "Create flashcards for this content",
  "outputFormat": "flashcard",
  "additionalContext": "Additional instructions",
  "extractTextFromFile": true,
  "useFileContentAsContext": true,
  "maxRetries": 3,
  "useTemplate": false
}
```

**Response:**
```json
{
  "success": true,
  "message": "Generation request created successfully",
  "data": 12345
}
```

**File Support:**
- **PDF**: Text extraction (requires PDFBox library)
- **DOCX**: Text extraction (requires Apache POI library)
- **TXT**: Direct text reading
- **Images**: OCR text extraction (requires Tesseract)

### 2. Real-time Progress Tracking

**WebSocket Endpoint:** `/ws/ai`

**Subscribe to Progress:**
```javascript
// Connect to WebSocket
const socket = new SockJS('/ws/ai');
const stompClient = Stomp.over(socket);

// Subscribe to progress updates
stompClient.subscribe('/topic/user/{userId}/generation/{requestId}/progress', 
  function(message) {
    const progress = JSON.parse(message.body);
    updateProgressUI(progress);
  }
);

// Send subscription message
stompClient.send("/app/ai/generation/{requestId}/subscribe", {}, {});
```

**Progress Data Structure:**
```json
{
  "requestId": 12345,
  "userId": 67890,
  "totalSteps": 4,
  "currentStep": 2,
  "currentStepDescription": "Generating flashcards",
  "stepProgress": 75,
  "status": "processing",
  "overallProgress": 50,
  "elapsedTime": 15000,
  "startTime": 1640995200000,
  "lastUpdateTime": 1640995215000
}
```

**Progress Steps:**
1. **File Processing** (0-25%): Extract text from uploaded file
2. **Content Analysis** (25-50%): Analyze content and prepare AI prompt
3. **AI Generation** (50-75%): Generate content using AI model
4. **Content Formatting** (75-100%): Format and structure final content

### 3. Get Generation Progress

**Endpoint:** `GET /api/ai/generation/{requestId}/progress`

**Description:** Get current progress for a generation request

**Response:**
```json
{
  "success": true,
  "message": "Generation progress retrieved successfully",
  "data": {
    "requestId": 12345,
    "userId": 67890,
    "totalSteps": 4,
    "currentStep": 2,
    "currentStepDescription": "Generating flashcards",
    "stepProgress": 75,
    "status": "processing",
    "overallProgress": 50
  }
}
```

## 📁 File Processing

### Supported File Types

| File Type | Extension | MIME Type | Processing |
|-----------|-----------|-----------|------------|
| PDF | .pdf | application/pdf | Text extraction via PDFBox |
| Word | .docx | application/vnd.openxmlformats-officedocument.wordprocessingml.document | Text extraction via Apache POI |
| Text | .txt | text/plain | Direct text reading |
| Image | .jpg, .png, .gif, .webp | image/* | OCR via Tesseract |

### File Size Limits

- **Maximum File Size**: 10MB
- **Recommended Size**: < 5MB for optimal processing
- **Processing Time**: Varies by file size and type

### File Processing Flow

1. **Upload Validation**: Check file type, size, and format
2. **Text Extraction**: Extract text content based on file type
3. **Content Analysis**: Analyze extracted text for AI processing
4. **Prompt Enhancement**: Enhance user prompt with file content
5. **AI Generation**: Generate content using enhanced prompt

## 🔄 Real-time Updates

### WebSocket Connection

```javascript
// Establish WebSocket connection
const socket = new SockJS('/ws/ai');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
  console.log('Connected to AI Generation WebSocket');
  
  // Subscribe to progress updates
  subscribeToProgress(requestId, userId);
});
```

### Progress Update Events

```javascript
// Handle progress updates
function handleProgressUpdate(progress) {
  switch(progress.status) {
    case 'processing':
      updateProgressBar(progress.overallProgress);
      updateStepDescription(progress.currentStepDescription);
      break;
      
    case 'completed':
      showCompletionMessage(progress.result);
      break;
      
    case 'failed':
      showErrorMessage(progress.errorMessage);
      break;
  }
}
```

### Error Handling

```javascript
// Handle WebSocket errors
stompClient.onWebSocketError = function(error) {
  console.error('WebSocket Error:', error);
  // Fallback to polling
  startPollingProgress(requestId);
};

// Handle STOMP errors
stompClient.onStompError = function(frame) {
  console.error('STOMP Error:', frame);
};
```

## 📊 Content Generation Types

### 1. Summary Generation

**Output Format:** `summary`

**Use Case:** Create concise summaries of uploaded content

**Example Prompt:** "Create a comprehensive summary of this document"

**Output Structure:**
```json
{
  "type": "summary",
  "title": "Document Summary",
  "summary": "Main points and key insights...",
  "keyPoints": ["Point 1", "Point 2", "Point 3"],
  "wordCount": 150
}
```

### 2. Quiz Generation

**Output Format:** `quiz`

**Use Case:** Generate questions based on content

**Example Prompt:** "Create 5 multiple choice questions from this content"

**Output Structure:**
```json
{
  "type": "quiz",
  "title": "Content Quiz",
  "questions": [
    {
      "question": "What is the main topic?",
      "options": ["Option A", "Option B", "Option C", "Option D"],
      "correctAnswer": 0,
      "explanation": "Explanation for the answer"
    }
  ]
}
```

### 3. Flashcard Generation

**Output Format:** `flashcard`

**Use Case:** Create study flashcards

**Example Prompt:** "Create flashcards for key concepts"

**Output Structure:**
```json
{
  "type": "flashcard",
  "title": "Study Flashcards",
  "cards": [
    {
      "front": "Question or concept",
      "back": "Answer or explanation",
      "category": "Concept Category"
    }
  ]
}
```

### 4. Notes Generation

**Output Format:** `note`

**Use Case:** Generate structured study notes

**Example Prompt:** "Create organized study notes from this content"

**Output Structure:**
```json
{
  "type": "note",
  "title": "Study Notes",
  "sections": [
    {
      "heading": "Section 1",
      "content": "Detailed content...",
      "subsections": []
    }
  ]
}
```

## 🚦 Rate Limiting

### Limits per User

- **Generation Requests**: 5 per hour, 3 per minute, 50 per day
- **File Uploads**: 10 per hour, 2 per minute
- **Templates**: 50 per user
- **Saved Content**: 100 per user

### Rate Limit Headers

```
X-RateLimit-Limit: 5
X-RateLimit-Remaining: 3
X-RateLimit-Reset: 1640998800
```

## 🔒 Security

### Authentication

- **JWT Token**: Required for all endpoints
- **User Validation**: Ensures users can only access their own content
- **File Validation**: Strict file type and size validation

### File Security

- **Virus Scanning**: All uploaded files are scanned
- **Content Validation**: File content is validated before processing
- **Access Control**: Files are only accessible to uploader

## 📝 Error Codes

| Code | Message | Description |
|------|---------|-------------|
| 400 | Invalid file type | Unsupported file format |
| 400 | File too large | Exceeds 10MB limit |
| 400 | Invalid content | File content cannot be processed |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | User cannot access this resource |
| 429 | Rate limit exceeded | Too many requests |
| 500 | Processing failed | Internal server error |

## 🧪 Testing

### Test File Upload

```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "request={\"promptText\":\"Create summary\",\"outputFormat\":\"summary\"}" \
  -F "file=@test_document.pdf" \
  http://localhost:8080/api/ai/generation/request
```

### Test WebSocket Connection

```javascript
// Test WebSocket connection
const socket = new SockJS('http://localhost:8080/ws/ai');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
  console.log('Connected:', frame);
  
  // Test subscription
  stompClient.subscribe('/topic/test', function(message) {
    console.log('Received:', message.body);
  });
  
  // Send test message
  stompClient.send("/app/test", {}, "Hello WebSocket!");
});
```

## 🔮 Future Enhancements

### Planned Features

1. **Advanced OCR**: Better image text recognition
2. **Multi-language Support**: Support for non-English content
3. **Content Templates**: Pre-defined content structures
4. **Batch Processing**: Process multiple files simultaneously
5. **Content Analytics**: Track content usage and effectiveness

### Integration Points

1. **Study Group Integration**: Share generated content with groups
2. **Learning Analytics**: Track learning progress
3. **Content Export**: Export to various formats (PDF, Word, etc.)
4. **Mobile App Support**: Optimized for mobile devices

---

**Last Updated:** January 2025  
**Version:** 2.0.0  
**Status:** Enhanced with File Upload & Real-time Progress
