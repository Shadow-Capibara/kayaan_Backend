# AI Generation Feature - Frontend Backend Sync Prompt

## 🎯 วัตถุประสงค์
สร้างการ sync ระหว่าง Frontend และ Backend สำหรับ AI Generation feature เพื่อให้ทั้งสองฝั่งทำงานร่วมกันได้อย่างสมบูรณ์

**หมายเหตุ**: โปรเจคนี้รองรับ content formats 3 แบบเท่านั้น: **Quiz**, **Note**, และ **Flashcard**

## 📋 ข้อมูลพื้นฐาน Backend

### Base URL
```
http://localhost:8080/api/ai/generation
```

### Authentication
```typescript
// ทุก request ต้องมี Authorization header
headers: {
  'Authorization': `Bearer ${jwtToken}`,
  'Content-Type': 'application/json'
}
```

### Response Format
```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}
```

## 🔄 API Endpoints ที่ต้อง Sync

### 1. Generation Requests

#### Create Generation Request
```typescript
// Frontend Request
POST /api/ai/generation/request
Content-Type: multipart/form-data

interface CreateGenerationRequest {
  request: {
    promptText: string;
    outputFormat: 'flashcard' | 'quiz' | 'note';
    maxRetries?: number;
  };
  file?: File; // Optional uploaded file
}

// Backend Response
interface CreateGenerationResponse {
  success: true;
  message: "Generation request created successfully";
  data: number; // requestId
}
```

#### Start Content Generation
```typescript
// Frontend Request
POST /api/ai/generation/{requestId}/generate

// Backend Response
interface GenerationStartResponse {
  success: true;
  message: "Content generation started successfully. Check status for progress updates.";
  data: "Generation in progress";
}
```

#### Get Generation Status
```typescript
// Frontend Request
GET /api/ai/generation/{requestId}/status

// Backend Response
interface GenerationStatusResponse {
  success: true;
  message: "Generation status retrieved successfully";
  data: {
    requestId: number;
    status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
    progress: number; // 0-100
    errorMessage?: string;
    createdAt: string;
    startedAt?: string;
    completedAt?: string;
    retryCount: number;
    maxRetries: number;
  };
}
```

#### Get User's Generation Requests
```typescript
// Frontend Request
GET /api/ai/generation/requests?page=0&size=10&sortBy=createdAt&sortDir=desc

// Backend Response
interface GenerationRequestsResponse {
  success: true;
  message: "Generation requests retrieved successfully";
  data: {
    content: Array<{
      id: number;
      promptText: string;
      outputFormat: string;
      status: string;
      progress: number;
      createdAt: string;
      updatedAt: string;
    }>;
    totalElements: number;
    totalPages: number;
    currentPage: number;
    size: number;
  };
}
```

### 2. Content Management

#### Save Generated Content
```typescript
// Frontend Request
POST /api/ai/generation/content/save
Content-Type: application/json

interface SaveContentRequest {
  generationRequestId: number;
  contentTitle: string;
  contentType: 'flashcard' | 'quiz' | 'note';
  contentData: string; // JSON string
  saveToSupabase?: boolean;
  customFileName?: string;
}

// Backend Response
interface SaveContentResponse {
  success: true;
  message: "Content saved successfully";
  data: {
    contentId: number;
    supabaseFilePath?: string;
    fileSize?: number;
  };
}
```

#### Get User's Saved Content
```typescript
// Frontend Request
GET /api/ai/generation/content?page=0&size=10&sortBy=createdAt&sortDir=desc

// Backend Response
interface SavedContentResponse {
  success: true;
  message: "Saved content retrieved successfully";
  data: {
    content: Array<{
      id: number;
      contentTitle: string;
      contentType: string;
      contentVersion: number;
      supabaseFilePath?: string;
      fileSize?: number;
      isSaved: boolean;
      createdAt: string;
    }>;
    totalElements: number;
    totalPages: number;
    currentPage: number;
    size: number;
  };
}
```

### 3. Template Management

#### Create Prompt Template
```typescript
// Frontend Request
POST /api/ai/generation/template
Content-Type: application/json

interface CreateTemplateRequest {
  templateName: string;
  templateDescription?: string;
  promptText: string;
  outputFormat: 'flashcard' | 'quiz' | 'note';
  isPublic?: boolean;
  isActive?: boolean;
}

// Backend Response
interface CreateTemplateResponse {
  success: true;
  message: "Template created successfully";
  data: {
    templateId: number;
    templateName: string;
    usageCount: number;
  };
}
```

#### Get User's Templates
```typescript
// Frontend Request
GET /api/ai/generation/template?page=0&size=10&sortBy=createdAt&sortDir=desc

// Backend Response
interface TemplatesResponse {
  success: true;
  message: "Templates retrieved successfully";
  data: {
    content: Array<{
      id: number;
      templateName: string;
      templateDescription?: string;
      promptText: string;
      outputFormat: string;
      isPublic: boolean;
      isActive: boolean;
      usageCount: number;
      createdAt: string;
    }>;
    totalElements: number;
    totalPages: number;
    currentPage: number;
    size: number;
  };
}
```

## 🚨 Error Handling

### Rate Limit Exceeded
```typescript
// Backend Response (429 Too Many Requests)
{
  success: false;
  message: "Rate limit exceeded";
  data: "Maximum 5 requests per hour allowed";
}
```

### Validation Errors
```typescript
// Backend Response (400 Bad Request)
{
  success: false;
  message: "Validation failed";
  data: {
    field: "promptText";
    error: "Prompt text is required";
  };
}
```

### Authentication Errors
```typescript
// Backend Response (401 Unauthorized)
{
  success: false;
  message: "Authentication required";
  data: "JWT token is missing or invalid";
}
```

## 🔧 Frontend Implementation Guidelines

### 1. API Service Layer
```typescript
// services/aiGenerationService.ts
class AIGenerationService {
  private baseUrl = 'http://localhost:8080/api/ai/generation';
  private token: string;

  constructor(token: string) {
    this.token = token;
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      ...options,
      headers: {
        'Authorization': `Bearer ${this.token}`,
        'Content-Type': 'application/json',
        ...options.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  async createGenerationRequest(data: CreateGenerationRequest): Promise<number> {
    const formData = new FormData();
    formData.append('request', JSON.stringify(data.request));
    
    if (data.file) {
      formData.append('file', data.file);
    }

    const response = await fetch(`${this.baseUrl}/request`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${this.token}`,
      },
      body: formData,
    });

    const result = await response.json();
    if (!result.success) {
      throw new Error(result.message);
    }

    return result.data;
  }

  async startGeneration(requestId: number): Promise<string> {
    const response = await this.request<string>(`/${requestId}/generate`, {
      method: 'POST',
    });

    if (!response.success) {
      throw new Error(response.message);
    }

    return response.data!;
  }

  async getGenerationStatus(requestId: number): Promise<GenerationStatus> {
    const response = await this.request<GenerationStatus>(`/${requestId}/status`);
    
    if (!response.success) {
      throw new Error(response.message);
    }

    return response.data!;
  }

  // ... other methods
}
```

### 2. State Management
```typescript
// stores/aiGenerationStore.ts
interface AIGenerationState {
  requests: GenerationRequest[];
  savedContent: SavedContent[];
  templates: Template[];
  currentRequest: GenerationRequest | null;
  loading: boolean;
  error: string | null;
}

class AIGenerationStore {
  private state: AIGenerationState = {
    requests: [],
    savedContent: [],
    templates: [],
    currentRequest: null,
    loading: false,
    error: null,
  };

  async createRequest(requestData: CreateGenerationRequest) {
    this.setLoading(true);
    try {
      const requestId = await aiGenerationService.createGenerationRequest(requestData);
      this.addRequest({ id: requestId, ...requestData.request, status: 'PENDING' });
      return requestId;
    } catch (error) {
      this.setError(error.message);
      throw error;
    } finally {
      this.setLoading(false);
    }
  }

  async pollGenerationStatus(requestId: number) {
    const interval = setInterval(async () => {
      try {
        const status = await aiGenerationService.getGenerationStatus(requestId);
        this.updateRequestStatus(requestId, status);
        
        if (status.status === 'COMPLETED' || status.status === 'FAILED') {
          clearInterval(interval);
        }
      } catch (error) {
        clearInterval(interval);
        this.setError(error.message);
      }
    }, 2000); // Poll every 2 seconds
  }

  // ... other methods
}
```

### 3. React Components
```typescript
// components/AIGenerationForm.tsx
const AIGenerationForm: React.FC = () => {
  const [formData, setFormData] = useState<CreateGenerationRequest>({
    request: {
      promptText: '',
      outputFormat: 'flashcard',
      maxRetries: 3,
    },
  });
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const requestId = await aiGenerationStore.createRequest({
        ...formData,
        file: file || undefined,
      });

      // Start polling for status
      aiGenerationStore.pollGenerationStatus(requestId);
      
      // Navigate to status page
      navigate(`/ai-generation/status/${requestId}`);
    } catch (error) {
      toast.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <textarea
        value={formData.request.promptText}
        onChange={(e) => setFormData({
          ...formData,
          request: { ...formData.request, promptText: e.target.value }
        })}
        placeholder="Enter your prompt here..."
        required
      />
      
      <select
        value={formData.request.outputFormat}
        onChange={(e) => setFormData({
          ...formData,
          request: { ...formData.request, outputFormat: e.target.value as any }
        })}
      >
        <option value="flashcard">Flashcard</option>
        <option value="quiz">Quiz</option>
        <option value="note">Note</option>
      </select>

      <input
        type="file"
        onChange={(e) => setFile(e.target.files?.[0] || null)}
        accept=".txt,.pdf,.doc,.docx"
      />

      <button type="submit" disabled={loading}>
        {loading ? 'Creating...' : 'Create Generation Request'}
      </button>
    </form>
  );
};
```

## 📊 Rate Limiting Implementation

### Frontend Rate Limiting
```typescript
// utils/rateLimiter.ts
class RateLimiter {
  private requests: Map<string, number[]> = new Map();
  private limits = {
    generationRequests: { max: 5, window: 3600000 }, // 5 per hour
    previews: { max: 3, window: 60000 }, // 3 per minute
  };

  canMakeRequest(type: keyof typeof this.limits, userId: string): boolean {
    const limit = this.limits[type];
    const now = Date.now();
    const userRequests = this.requests.get(`${userId}-${type}`) || [];
    
    // Remove old requests outside the window
    const validRequests = userRequests.filter(time => now - time < limit.window);
    
    if (validRequests.length >= limit.max) {
      return false;
    }

    // Add current request
    validRequests.push(now);
    this.requests.set(`${userId}-${type}`, validRequests);
    
    return true;
  }

  getRemainingRequests(type: keyof typeof this.limits, userId: string): number {
    const limit = this.limits[type];
    const now = Date.now();
    const userRequests = this.requests.get(`${userId}-${type}`) || [];
    const validRequests = userRequests.filter(time => now - time < limit.window);
    
    return Math.max(0, limit.max - validRequests.length);
  }
}
```

## 🔄 Real-time Updates

### WebSocket Integration
```typescript
// services/aiGenerationWebSocket.ts
class AIGenerationWebSocket {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  connect(token: string) {
    this.ws = new WebSocket(`ws://localhost:8080/ws/ai-generation?token=${token}`);
    
    this.ws.onopen = () => {
      console.log('WebSocket connected');
      this.reconnectAttempts = 0;
    };

    this.ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      this.handleMessage(data);
    };

    this.ws.onclose = () => {
      console.log('WebSocket disconnected');
      this.attemptReconnect(token);
    };
  }

  private handleMessage(data: any) {
    switch (data.type) {
      case 'GENERATION_PROGRESS':
        aiGenerationStore.updateRequestProgress(data.requestId, data.progress);
        break;
      case 'GENERATION_COMPLETED':
        aiGenerationStore.updateRequestStatus(data.requestId, { status: 'COMPLETED', progress: 100 });
        break;
      case 'GENERATION_FAILED':
        aiGenerationStore.updateRequestStatus(data.requestId, { 
          status: 'FAILED', 
          errorMessage: data.errorMessage 
        });
        break;
    }
  }

  private attemptReconnect(token: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => this.connect(token), 1000 * this.reconnectAttempts);
    }
  }
}
```

## 🧪 Testing Guidelines

### API Testing
```typescript
// tests/aiGeneration.test.ts
describe('AI Generation API', () => {
  let token: string;

  beforeAll(async () => {
    token = await loginUser('test@example.com', 'password');
  });

  test('should create generation request', async () => {
    const requestData = {
      request: {
        promptText: 'Create a flashcard about AI',
        outputFormat: 'flashcard',
        maxRetries: 3,
      },
    };

    const response = await aiGenerationService.createGenerationRequest(requestData);
    expect(response).toBeGreaterThan(0);
  });

  test('should handle rate limiting', async () => {
    // Make 6 requests to trigger rate limit
    for (let i = 0; i < 6; i++) {
      try {
        await aiGenerationService.createGenerationRequest({
          request: {
            promptText: `Test request ${i}`,
            outputFormat: 'quiz',
          },
        });
      } catch (error) {
        if (i === 5) {
          expect(error.message).toContain('Rate limit exceeded');
        }
      }
    }
  });
});
```

## 📝 Checklist สำหรับ Frontend Developer

### ✅ Authentication & Authorization
- [ ] JWT token management
- [ ] Token refresh mechanism
- [ ] Protected route implementation
- [ ] Role-based access control

### ✅ API Integration
- [ ] All endpoints implemented
- [ ] Error handling for all responses
- [ ] Loading states management
- [ ] Retry mechanism for failed requests

### ✅ Rate Limiting
- [ ] Client-side rate limiting
- [ ] User feedback for rate limit exceeded
- [ ] Request countdown display
- [ ] Graceful degradation

### ✅ Real-time Updates
- [ ] WebSocket connection
- [ ] Progress tracking
- [ ] Status updates
- [ ] Reconnection logic

### ✅ File Upload
- [ ] File validation (size, type)
- [ ] Upload progress
- [ ] Error handling
- [ ] Preview functionality

### ✅ Content Management
- [ ] Save content functionality
- [ ] Download content
- [ ] Content preview
- [ ] Content organization

### ✅ Template Management
- [ ] Create/edit templates
- [ ] Template sharing
- [ ] Template usage tracking
- [ ] Template search/filter

### ✅ User Experience
- [ ] Responsive design
- [ ] Loading indicators
- [ ] Error messages
- [ ] Success notifications
- [ ] Accessibility features

## 🚀 Deployment Considerations

### Environment Variables
```bash
# Frontend .env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_WS_URL=ws://localhost:8080
REACT_APP_OPENAI_MODEL=gpt-5-nano
REACT_APP_MAX_FILE_SIZE=10485760 # 10MB
```

### CORS Configuration
```typescript
// Backend CORS settings (already configured)
cors:
  allowed-origins: "*"
  allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
  allowed-headers: "*"
  allow-credentials: true
```

### Production Checklist
- [ ] Environment-specific configurations
- [ ] Error monitoring (Sentry, etc.)
- [ ] Performance monitoring
- [ ] Security headers
- [ ] HTTPS enforcement
- [ ] CDN configuration

---

**หมายเหตุ**: Prompt นี้ครอบคลุมการ sync ระหว่าง Frontend และ Backend สำหรับ AI Generation feature อย่างครบถ้วน พร้อมตัวอย่างโค้ดและแนวทางปฏิบัติที่ดี
