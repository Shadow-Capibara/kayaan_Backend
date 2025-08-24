# AI Generation Feature

## Overview

The AI Generation feature provides comprehensive AI content generation capabilities for the Kayaan Backend application. It allows users to create AI generation requests, manage prompt templates, and organize generated content with built-in rate limiting and security.

## Features

### Core Capabilities
- **AI Content Generation**: Create and manage AI generation requests
- **Prompt Templates**: Create, manage, and share prompt templates
- **Content Management**: Save, download, and organize generated content
- **Real-time Progress**: Track generation progress and status
- **Rate Limiting**: Built-in protection against abuse
- **Security**: JWT-based authentication and authorization

### Use Cases Covered
- UC-19: Create AI Generation Request
- UC-20: Start Content Generation
- UC-21: Get Generation Status
- UC-22: Get User's Generation Requests
- UC-23: Cancel Generation
- UC-24: Retry Failed Generation
- UC-25: Save Generated Content
- UC-26: Get User's Saved Content
- UC-27: Template Management

## Architecture

### Components
```
AI Generation Feature
├── Controllers (REST API endpoints)
├── Services (Business logic)
├── Repositories (Data access)
├── Entities (Data models)
├── DTOs (Data transfer objects)
├── Exceptions (Error handling)
└── Configuration (Settings & beans)
```

### Database Schema
- `ai_generation_request`: Stores generation requests and their status
- `ai_prompt_template`: Stores reusable prompt templates
- `ai_generated_content`: Stores generated content and metadata

## Setup Instructions

### Prerequisites
- Java 17+
- Spring Boot 3.4.5+
- MySQL 8.0+
- Maven 3.6+

### Environment Variables
```bash
# OpenAI Configuration
OPENAI_API_KEY=your-openai-api-key-here

# Supabase Configuration
SUPABASE_URL=your-supabase-url-here
SUPABASE_KEY=your-supabase-anon-key-here

# JWT Configuration
JWT_SECRET=your-jwt-secret-here

# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=kayaan_backend
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password
```

### Database Migration
The feature includes Flyway migrations that will automatically create the required tables:

```sql
-- V202508XX_XX__ai_generation_request.sql
-- V202508XX_XX__ai_prompt_template.sql
-- V202508XX_XX__ai_generated_content.sql
```

### Configuration
The feature uses the `application-ai-generation.yml` configuration file with the following key settings:

```yaml
ai:
  generation:
    rate-limit:
      max-requests-per-hour: 5
      max-previews-per-minute: 3
    openai:
      model: "gpt-3.5-turbo"
      max-tokens: 2000
    supabase:
      bucket: "ai-generated-content"
      max-file-size-mb: 10
```

## API Endpoints

### Base URL
```
http://localhost:8080/api/ai/generation
```

### Authentication
All endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Generation Requests

#### Create Generation Request
```http
POST /api/ai/generation/request
Content-Type: application/json

{
  "promptText": "Write a blog post about AI",
  "outputFormat": "blog_post",
  "maxRetries": 3
}
```

#### Start Content Generation
```http
POST /api/ai/generation/{requestId}/generate
```

#### Get Generation Status
```http
GET /api/ai/generation/{requestId}/status
```

#### Get User's Generation Requests
```http
GET /api/ai/generation/requests?page=0&size=10&sortBy=createdAt&sortDir=desc
```

#### Cancel Generation
```http
POST /api/ai/generation/{requestId}/cancel
```

#### Retry Failed Generation
```http
POST /api/ai/generation/{requestId}/retry
```

### Content Management

#### Save Generated Content
```http
POST /api/ai/generation/content/save
Content-Type: application/json

{
  "generationRequestId": 123,
  "contentTitle": "AI Blog Post",
  "contentType": "blog_post",
  "contentData": "{\"title\": \"...\", \"content\": \"...\"}",
  "saveToSupabase": true,
  "customFileName": "ai_blog_post.json"
}
```

#### Get User's Saved Content
```http
GET /api/ai/generation/content?page=0&size=10&sortBy=createdAt&sortDir=desc
```

#### Download Content
```http
GET /api/ai/generation/content/{contentId}/download
```

#### Delete Saved Content
```http
DELETE /api/ai/generation/content/{contentId}
```

### Template Management

#### Create Prompt Template
```http
POST /api/ai/generation/template
Content-Type: application/json

{
  "templateName": "Blog Post Template",
  "templateDescription": "Template for writing blog posts",
  "promptText": "Write a blog post about {topic}",
  "outputFormat": "blog_post",
  "isPublic": false,
  "isActive": true
}
```

#### Get User's Templates
```http
GET /api/ai/generation/template?page=0&size=10&sortBy=createdAt&sortDir=desc
```

#### Get Public Templates
```http
GET /api/ai/generation/template/public?page=0&size=10&sortBy=usageCount&sortDir=desc
```

#### Update Template
```http
PUT /api/ai/generation/template/{templateId}
Content-Type: application/json

{
  "templateName": "Updated Blog Post Template",
  "templateDescription": "Updated description",
  "promptText": "Write a blog post about {topic} with {style}",
  "outputFormat": "blog_post",
  "isPublic": true,
  "isActive": true
}
```

#### Delete Template
```http
DELETE /api/ai/generation/template/{templateId}
```

### Additional Features

#### Preview Content
```http
POST /api/ai/generation/preview
Content-Type: application/json

{
  "generationRequestId": 123,
  "contentTitle": "Preview Title",
  "contentType": "blog_post",
  "contentData": "{\"title\": \"...\", \"content\": \"...\"}",
  "previewFormat": "html",
  "includeMetadata": true
}
```

#### Get User Generation Statistics
```http
GET /api/ai/generation/stats
```

#### Cleanup Old Requests (Admin Only)
```http
POST /api/ai/generation/cleanup?daysOld=30
```

#### Health Check
```http
GET /api/ai/generation/health
```

## Response Format

All API responses follow a consistent format:

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": "Additional error details"
}
```

## Rate Limiting

The feature implements comprehensive rate limiting:

- **Generation Requests**: 5 per hour per user
- **Previews**: 3 per minute per user
- **Templates**: 50 per user
- **Content**: 100 per user

Rate limit exceeded responses include:
```json
{
  "success": false,
  "message": "Rate limit exceeded",
  "data": "Maximum 5 requests per hour allowed"
}
```

## Error Handling

The feature includes comprehensive error handling with appropriate HTTP status codes:

- `400 Bad Request`: Validation errors, invalid operations
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Unexpected server errors

## Security

### Authentication
- JWT-based authentication
- Token expiration: 24 hours
- Secure token storage

### Authorization
- Role-based access control
- User resource isolation
- Admin-only operations protected

### Data Protection
- User data isolation
- Secure file storage via Supabase
- Signed URLs for file access

## Monitoring & Health Checks

### Health Endpoints
- `/api/ai/generation/health`: Basic health check
- `/actuator/health`: Detailed health information
- `/actuator/metrics`: Performance metrics

### Logging
Comprehensive logging at DEBUG level for development:
```yaml
logging:
  level:
    se499.kayaanbackend.AI_Generate: DEBUG
```

## Development

### Project Structure
```
src/main/java/se499/kayaanbackend/AI_Generate/
├── controller/
│   └── AIGenerationController.java
├── service/
│   ├── AIGenerationService.java
│   ├── AISupabaseService.java
│   ├── OpenAIService.java
│   └── impl/
│       └── AIGenerationServiceImpl.java
├── repository/
│   ├── AIGenerationRequestRepository.java
│   ├── AIPromptTemplateRepository.java
│   └── AIGeneratedContentRepository.java
├── entity/
│   ├── AIGenerationRequest.java
│   ├── AIPromptTemplate.java
│   └── AIGeneratedContent.java
├── dto/
│   ├── CreateGenerationRequestDTO.java
│   ├── GenerationStatusDTO.java
│   ├── SaveContentDTO.java
│   ├── PreviewRequestDTO.java
│   ├── AIPromptTemplateDTO.java
│   ├── AIGeneratedContentDTO.java
│   └── ApiResponseDTO.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedAccessException.java
│   ├── InvalidOperationException.java
│   ├── RateLimitExceededException.java
│   └── AIServiceException.java
└── config/
    ├── AIGenerationConfig.java
    └── OpenAPIConfig.java
```

### Building
```bash
# Compile the project
./mvnw compile

# Run tests
./mvnw test

# Package the application
./mvnw package

# Run the application
./mvnw spring-boot:run
```

### Testing
The feature includes comprehensive unit and integration tests:

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AIGenerationControllerTest

# Run tests with coverage
./mvnw test jacoco:report
```

## Deployment

### Production Considerations
- Set appropriate environment variables
- Configure database connection pooling
- Enable production logging levels
- Configure monitoring and alerting
- Set up backup and recovery procedures

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/kayaan-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Troubleshooting

### Common Issues

#### Database Connection Issues
- Verify database credentials
- Check network connectivity
- Ensure database is running

#### OpenAI API Issues
- Verify API key is valid
- Check API quota limits
- Ensure network access to OpenAI

#### Supabase Issues
- Verify URL and key configuration
- Check bucket permissions
- Ensure proper CORS configuration

### Debug Mode
Enable debug logging for troubleshooting:
```yaml
logging:
  level:
    se499.kayaanbackend.AI_Generate: DEBUG
```

## Support

For technical support:
- Email: ai-generation@kayaan.com
- Documentation: https://kayaan.com/ai-generation/docs
- Issues: https://github.com/kayaan/ai-generation/issues

## License

This feature is licensed under the MIT License. See the LICENSE file for details.

## Contributing

Contributions are welcome! Please read the contributing guidelines before submitting pull requests.

---

**Note**: This feature is part of the Kayaan Backend application. Ensure you have the necessary permissions and access before making modifications.
