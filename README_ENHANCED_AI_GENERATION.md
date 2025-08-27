# 🚀 Enhanced AI Generation System

## 📋 Overview

The Enhanced AI Generation System is a comprehensive backend solution that supports:
- **File Upload & Processing**: PDF, DOCX, TXT, Images
- **Real-time Progress Tracking**: WebSocket-based progress updates
- **Multi-step Generation Process**: Step-by-step content generation
- **Advanced Content Types**: Summary, Quiz, Flashcards, Notes

## ✨ New Features

### 1. File Upload Support
- **PDF Documents**: Text extraction using PDFBox
- **Word Documents**: Text extraction using Apache POI
- **Text Files**: Direct text reading
- **Images**: OCR text extraction using Tesseract

### 2. Real-time Progress Tracking
- **WebSocket Integration**: Live progress updates
- **Multi-step Process**: Track each generation step
- **Progress Visualization**: Real-time progress bars and status

### 3. Enhanced Content Generation
- **Smart Prompt Enhancement**: Automatically enhance prompts with file content
- **Content Analysis**: Analyze uploaded content for better AI generation
- **Format Optimization**: Optimize output for each content type

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend API    │    │   AI Services   │
│                 │    │                  │    │                 │
│ • File Upload   │◄──►│ • File Processing│◄──►│ • OpenAI API    │
│ • Progress UI   │    │ • Progress Track │    │ • Content Gen   │
│ • Real-time     │    │ • WebSocket      │    │ • Rate Limiting │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌──────────────────┐
                       │   Database       │
                       │                  │
                       │ • Generation     │
                       │ • Content        │
                       │ • Templates      │
                       └──────────────────┘
```

## 🚀 Quick Start

### 1. Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- OpenAI API Key

### 2. Configuration
```yaml
# application.yml
ai:
  openai:
    api-key: ${OPENAI_API_KEY}
    model: gpt-5-nano
    max-tokens: 256
    temperature: 0.1

supabase:
  url: ${SUPABASE_URL}
  service-key: ${SUPABASE_SERVICE_KEY}
  bucket:
    ai: ai-outputs
```

### 3. Run the Application
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## 📁 File Processing

### Supported File Types

| File Type | Extension | Processing | Library |
|-----------|-----------|------------|---------|
| PDF | .pdf | Text extraction | PDFBox |
| Word | .docx | Text extraction | Apache POI |
| Text | .txt | Direct reading | Java IO |
| Image | .jpg, .png, .gif, .webp | OCR | Tesseract |

### File Processing Flow

1. **Upload Validation**
   - Check file type and size
   - Validate content format
   - Security scanning

2. **Text Extraction**
   - Extract text based on file type
   - Handle encoding issues
   - Clean and normalize text

3. **Content Analysis**
   - Analyze text structure
   - Extract key concepts
   - Identify content type

4. **AI Processing**
   - Enhance prompts with content
   - Generate AI content
   - Format output

## 🔌 API Endpoints

### File Upload with Generation
```http
POST /api/ai/generation/request
Content-Type: multipart/form-data

Parameters:
- request: JSON generation request
- file: Uploaded file (optional)
```

### Real-time Progress
```http
GET /api/ai/generation/{requestId}/progress
```

### WebSocket Endpoints
```
/ws/ai - AI Generation WebSocket
/topic/user/{userId}/generation/{requestId}/progress - Progress updates
```

## 💻 Frontend Integration

### File Upload
```javascript
const formData = new FormData();
formData.append('request', JSON.stringify({
  promptText: 'Create flashcards for this content',
  outputFormat: 'flashcard',
  extractTextFromFile: true
}));
formData.append('file', file);

const response = await fetch('/api/ai/generation/request', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});
```

### Real-time Progress
```javascript
// Connect to WebSocket
const socket = new SockJS('/ws/ai');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
  // Subscribe to progress updates
  stompClient.subscribe(
    `/topic/user/${userId}/generation/${requestId}/progress`,
    function(message) {
      const progress = JSON.parse(message.body);
      updateProgressUI(progress);
    }
  );
});
```

### Progress UI Updates
```javascript
function updateProgressUI(progress) {
  // Update progress bar
  document.getElementById('progress-bar').style.width = 
    `${progress.overallProgress}%`;
  
  // Update step description
  document.getElementById('step-description').textContent = 
    progress.currentStepDescription;
  
  // Handle completion
  if (progress.status === 'completed') {
    showCompletionMessage(progress.result);
  }
}
```

## 📊 Content Types

### 1. Summary Generation
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
```json
{
  "type": "quiz",
  "title": "Content Quiz",
  "questions": [
    {
      "question": "What is the main topic?",
      "options": ["A", "B", "C", "D"],
      "correctAnswer": 0,
      "explanation": "Explanation"
    }
  ]
}
```

### 3. Flashcard Generation
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

## 🔒 Security Features

### Authentication
- JWT-based authentication
- User validation for all endpoints
- Role-based access control

### File Security
- File type validation
- Size limit enforcement (10MB max)
- Content scanning
- Access control

### Rate Limiting
- Per-user rate limits
- Configurable limits per time window
- Automatic throttling

## 📈 Monitoring & Analytics

### Progress Tracking
- Real-time generation progress
- Step-by-step tracking
- Performance metrics
- Error tracking

### Usage Analytics
- Generation requests per user
- Content type distribution
- Success/failure rates
- Processing times

## 🧪 Testing

### Run Test Suite
```bash
# Make script executable
chmod +x test_enhanced_ai_generation.sh

# Run tests
./test_enhanced_ai_generation.sh
```

### Test Coverage
- File upload functionality
- Progress tracking
- WebSocket connections
- Content generation
- Rate limiting
- Error handling

## 🚦 Rate Limiting

### Default Limits
- **Generation Requests**: 5/hour, 3/minute, 50/day
- **File Uploads**: 10/hour, 2/minute
- **Templates**: 50 per user
- **Saved Content**: 100 per user

### Customization
```yaml
ai:
  generation:
    rate-limit:
      max-requests-per-hour: 10
      max-requests-per-minute: 5
      max-requests-per-day: 100
```

## 🔧 Configuration

### Environment Variables
```bash
# OpenAI Configuration
export OPENAI_API_KEY="your-api-key"
export OPENAI_MODEL="gpt-5-nano"
export OPENAI_MAX_TOKENS="256"

# Supabase Configuration
export SUPABASE_URL="your-supabase-url"
export SUPABASE_SERVICE_KEY="your-service-key"

# Database Configuration
export DB_URL="jdbc:mysql://localhost:3306/kayaan_db"
export DB_USERNAME="root"
export DB_PASSWORD="password"
```

### Application Properties
```yaml
# File Upload Configuration
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# WebSocket Configuration
websocket:
  allowed-origins: "*"
  heartbeat-interval: 30000
```

## 🐛 Troubleshooting

### Common Issues

1. **File Upload Fails**
   - Check file size (max 10MB)
   - Verify file type is supported
   - Ensure proper authentication

2. **WebSocket Connection Issues**
   - Check WebSocket endpoint configuration
   - Verify CORS settings
   - Check network connectivity

3. **AI Generation Fails**
   - Verify OpenAI API key
   - Check rate limits
   - Review error logs

### Debug Mode
```yaml
logging:
  level:
    se499.kayaanbackend.AI_Generate: DEBUG
    se499.kayaanbackend.AI_Generate.service: DEBUG
    se499.kayaanbackend.AI_Generate.controller: DEBUG
```

## 🔮 Future Enhancements

### Planned Features
1. **Advanced OCR**: Better image text recognition
2. **Multi-language Support**: Non-English content processing
3. **Content Templates**: Pre-defined content structures
4. **Batch Processing**: Multiple file processing
5. **Content Analytics**: Usage tracking and insights

### Integration Points
1. **Study Group Integration**: Share generated content
2. **Learning Analytics**: Track learning progress
3. **Content Export**: Multiple export formats
4. **Mobile App Support**: Mobile-optimized APIs

## 📚 Additional Resources

### Documentation
- [API Reference](docs/features/ai-generation/ENHANCED_AI_GENERATION_API.md)
- [Integration Guide](docs/features/ai-generation/FRONTEND_INTEGRATION.md)
- [Testing Guide](docs/features/ai-generation/testing/README.md)

### Examples
- [Frontend Integration Examples](examples/frontend/)
- [API Usage Examples](examples/api/)
- [WebSocket Examples](examples/websocket/)

### Support
- [Issue Tracker](https://github.com/your-repo/issues)
- [Discussion Forum](https://github.com/your-repo/discussions)
- [Documentation Wiki](https://github.com/your-repo/wiki)

---

**Version:** 2.0.0  
**Last Updated:** January 2025  
**Status:** Production Ready with Enhanced Features
