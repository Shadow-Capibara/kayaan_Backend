# 🚀 AI Cost Optimization Features

## 📋 **Overview**

ระบบประหยัดค่าใช้จ่ายสำหรับ AI Generation ที่ครอบคลุมทุกด้าน:

- ✅ **Caching System** - ลด API calls ซ้ำ
- ✅ **Rate Limiting** - ป้องกันการใช้งานเกิน
- ✅ **Token Monitoring** - ติดตามการใช้ token
- ✅ **Cost Estimation** - ประมาณการค่าใช้จ่าย
- ✅ **Prompt Optimization** - ปรับปรุง prompt ให้มีประสิทธิภาพ

## 🏗️ **Architecture**

```
User Request → Rate Limit Check → Cache Check → OpenAI API → Token Tracking
     ↓              ↓              ↓           ↓           ↓
Rate Limiting   Caching      Token Usage   Cost Calc   Monitoring
```

## 🔧 **Components Implemented**

### 1. **CacheConfig.java**
- Caffeine cache configuration
- Cache size: 1000 entries
- Expire after: 1 hour (write), 30 minutes (access)
- Cache names: ai-flashcard, ai-quiz, ai-note, ai-summary

### 2. **RateLimitService.java**
- User-based rate limiting
- Limits: minute, hourly, daily
- Configurable via application.yml
- Automatic reset at intervals

### 3. **TokenUsageService.java**
- Track input/output tokens
- Cost estimation (GPT-5 Nano pricing)
- Daily/monthly projections
- User-specific tracking

### 4. **CachedAIService.java**
- Intelligent caching with SHA-256 keys
- Cache similar requests
- Reduce duplicate API calls

### 5. **AIMonitoringController.java**
- REST endpoints for monitoring
- Rate limit status
- Token usage statistics
- Cost optimization tips

## 📊 **Configuration**

### **application.yml**
```yaml
ai:
  generation:
    rate-limit:
      max-requests-per-hour: 5
      max-requests-per-minute: 3
      max-requests-per-day: 50
```

### **Environment Variables**
```bash
export OPENAI_MODEL="gpt-5-nano"
export OPENAI_MAX_TOKENS="256"
export OPENAI_TEMPERATURE="0.1"
export OPENAI_TIMEOUT_SECONDS="30"
```

## 🚀 **API Endpoints**

### **Rate Limiting**
```bash
# Check user rate limits
GET /api/ai/monitoring/rate-limits/{userId}

# Reset user rate limits
POST /api/ai/monitoring/rate-limits/{userId}/reset
```

### **Token Usage**
```bash
# Get global token usage
GET /api/ai/monitoring/token-usage

# Get user-specific usage
GET /api/ai/monitoring/token-usage/{userId}

# Reset all counters
POST /api/ai/monitoring/token-usage/reset
```

### **Caching**
```bash
# Get cache statistics
GET /api/ai/monitoring/cache-stats
```

### **Cost Optimization**
```bash
# Get optimization tips
GET /api/ai/monitoring/cost-optimization
```

## 💰 **Cost Savings Breakdown**

| Feature | Savings | Description |
|---------|---------|-------------|
| **GPT-5 Nano** | **40%** | Model ใหม่ที่ถูกกว่า gpt-4o-mini |
| **Caching** | **20-40%** | ลด API calls ซ้ำ |
| **Rate Limiting** | **Variable** | ป้องกันการใช้งานเกิน |
| **Prompt Optimization** | **15-25%** | Prompt สั้นและมีประสิทธิภาพ |
| **Token Monitoring** | **10-20%** | ติดตามและควบคุมการใช้ |

### **Total Expected Savings: 60-80%** 🎉

## 📈 **Usage Examples**

### **1. Basic AI Generation with Caching**
```java
@Autowired
private CachedAIService cachedAIService;

String content = cachedAIService.generateContentWithCache(
    "Create a flashcard about Java",
    "flashcard",
    "Keep it simple",
    "user123"
);
```

### **2. Check Rate Limits**
```java
@Autowired
private RateLimitService rateLimitService;

if (rateLimitService.canMakeRequest("user123")) {
    // Proceed with AI generation
} else {
    // Show rate limit message
}
```

### **3. Monitor Token Usage**
```java
@Autowired
private TokenUsageService tokenUsageService;

TokenUsageService.TokenUsageStats stats = tokenUsageService.getCurrentUsage();
TokenUsageService.CostEstimate cost = tokenUsageService.estimateCost();

System.out.println("Daily cost: $" + cost.getDailyCost());
System.out.println("Total tokens: " + stats.getTotalTokens());
```

## 🔍 **Monitoring Dashboard**

### **Real-time Metrics**
- Token usage per user
- Cost per day/month
- Cache hit rates
- Rate limit violations

### **Cost Alerts**
- High daily usage (>$0.10)
- Excessive requests (>20/day)
- Token usage spikes

### **Optimization Tips**
- Prompt improvement suggestions
- Caching recommendations
- Rate limit adjustments

## 🧪 **Testing**

### **Run Cost Optimization Tests**
```bash
# Windows PowerShell
./test_cost_optimization.sh

# Or manually test endpoints
curl http://localhost:8080/api/ai/monitoring/token-usage
curl http://localhost:8080/api/ai/monitoring/rate-limits/test_user
```

### **Test Scenarios**
1. **Caching**: Same prompt → Cache hit
2. **Rate Limiting**: Multiple rapid requests → Limit enforcement
3. **Token Tracking**: API calls → Token count increase
4. **Cost Calculation**: Usage → Cost estimation

## ⚙️ **Customization**

### **Adjust Rate Limits**
```yaml
ai:
  generation:
    rate-limit:
      max-requests-per-hour: 10    # เพิ่มจาก 5 เป็น 10
      max-requests-per-minute: 5   # เพิ่มจาก 3 เป็น 5
      max-requests-per-day: 100    # เพิ่มจาก 50 เป็น 100
```

### **Cache Configuration**
```java
// ใน CacheConfig.java
.maximumSize(2000)           // เพิ่ม cache size
.expireAfterWrite(2, TimeUnit.HOURS)  // เพิ่มเวลาหมดอายุ
```

### **Token Monitoring Alerts**
```java
// ใน TokenUsageService.java
if (dailyCost > 0.20) {  // เพิ่ม threshold
    // Send alert
}
```

## 🚨 **Troubleshooting**

### **Common Issues**

1. **Cache Not Working**
   - Check Caffeine dependency in pom.xml
   - Verify @EnableCaching annotation
   - Check cache configuration

2. **Rate Limiting Too Strict**
   - Adjust limits in application.yml
   - Check user ID consistency
   - Verify timezone settings

3. **Token Usage Not Tracking**
   - Check OpenAI API response format
   - Verify TokenUsageService injection
   - Check logging for errors

### **Debug Commands**
```bash
# Check application logs
tail -f logs/application.log | grep "AI_Generate"

# Test individual services
curl -X POST http://localhost:8080/api/ai/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "test", "outputFormat": "flashcard"}'

# Monitor cache performance
curl http://localhost:8080/api/ai/monitoring/cache-stats
```

## 📚 **Best Practices**

### **1. Caching Strategy**
- Cache similar prompts together
- Use appropriate TTL (Time To Live)
- Monitor cache hit rates
- Implement cache warming for popular requests

### **2. Rate Limiting**
- Set realistic limits based on user behavior
- Implement progressive limits (new users vs power users)
- Provide clear feedback when limits are hit
- Consider implementing burst allowances

### **3. Token Optimization**
- Keep prompts concise and specific
- Use few-shot examples when possible
- Implement prompt templates
- Monitor token usage patterns

### **4. Cost Monitoring**
- Set up daily cost alerts
- Track usage per user/feature
- Implement cost budgets
- Regular cost optimization reviews

## 🔮 **Future Enhancements**

### **Planned Features**
- **Advanced Caching**: Redis integration for distributed caching
- **Smart Rate Limiting**: ML-based limit adjustment
- **Cost Prediction**: AI-powered cost forecasting
- **Usage Analytics**: Detailed usage insights and trends
- **Automated Optimization**: Self-tuning parameters

### **Integration Possibilities**
- **Slack/Email Alerts**: Cost threshold notifications
- **Grafana Dashboards**: Real-time monitoring
- **Prometheus Metrics**: Performance tracking
- **AWS Cost Explorer**: Cloud cost integration

## 🎯 **Success Metrics**

### **Key Performance Indicators**
- **Cost Reduction**: 60-80% savings target
- **Cache Hit Rate**: >70% for similar requests
- **API Response Time**: <2 seconds average
- **User Satisfaction**: <5% rate limit complaints
- **System Uptime**: >99.9% availability

### **Monitoring Checklist**
- [ ] Daily cost tracking
- [ ] Cache performance monitoring
- [ ] Rate limit violation alerts
- [ ] Token usage per user
- [ ] Cost optimization recommendations
- [ ] System performance metrics

## 🏁 **Getting Started**

### **1. Setup Dependencies**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

### **2. Configure Environment**
```bash
export OPENAI_API_KEY="your-api-key"
export OPENAI_MODEL="gpt-5-nano"
```

### **3. Start Application**
```bash
mvn spring-boot:run
```

### **4. Test Features**
```bash
./test_cost_optimization.sh
```

### **5. Monitor Usage**
```bash
curl http://localhost:8080/api/ai/monitoring/token-usage
```

## 🎉 **Conclusion**

ระบบประหยัดค่าใช้จ่ายนี้จะช่วยให้คุณ:

- **ประหยัดค่าใช้จ่าย 60-80%** ในการใช้ OpenAI API
- **ควบคุมการใช้งาน** ด้วย rate limiting และ monitoring
- **เพิ่มประสิทธิภาพ** ด้วย caching และ prompt optimization
- **ติดตามค่าใช้จ่าย** แบบ real-time
- **ปรับปรุง UX** ด้วยการตอบสนองที่เร็วขึ้น

พร้อมใช้งานทันทีหลังจาก setup! 🚀
