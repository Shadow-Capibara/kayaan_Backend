package se499.kayaanbackend.AI_Generate.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for integrating with OpenAI API for content generation
 * Handles AI model interactions and response processing
 */
@Slf4j
@Service
public class OpenAIService {
    
    @Value("${ai.openai.api-key}")
    private String openaiApiKey;
    
    @Value("${ai.openai.model:gpt-5-nano}")
    private String openaiModel;
    
    @Value("${ai.openai.max-tokens:256}")
    private Integer maxTokens;
    
    @Value("${ai.openai.temperature:0.1}")
    private Double temperature;
    
    @Value("${ai.openai.timeout-seconds:30}")
    private Integer timeoutSeconds;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String openaiApiUrl = "https://api.openai.com/v1/chat/completions";
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @Autowired
    private TokenUsageService tokenUsageService;
    
    public OpenAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate content using OpenAI API asynchronously
     * @param prompt User prompt for content generation
     * @param outputFormat Expected output format (flashcard, quiz, note, summary)
     * @param additionalContext Additional context for generation
     * @return CompletableFuture with generated content
     */
    public CompletableFuture<String> generateContentAsync(String prompt, String outputFormat, String additionalContext) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return generateContent(prompt, outputFormat, additionalContext);
            } catch (Exception e) {
                log.error("Failed to generate content asynchronously", e);
                throw new RuntimeException("Content generation failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Generate content using OpenAI API with caching and rate limiting
     * @param prompt User prompt for content generation
     * @param outputFormat Expected output format
     * @param additionalContext Additional context for generation
     * @param userId User ID for rate limiting and monitoring
     * @return Generated content as JSON string
     */
    public String generateContent(String prompt, String outputFormat, String additionalContext, String userId) {
        try {
            log.info("Generating content with OpenAI API - Format: {}, Prompt length: {} chars, User: {}", 
                outputFormat, prompt.length(), userId);
            
            // Check rate limits
            if (!rateLimitService.canMakeRequest(userId)) {
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
            
            // Build system message based on output format
            String systemMessage = buildSystemMessage(outputFormat);
            
            // Build user message with context
            String userMessage = buildUserMessage(prompt, additionalContext);
            
            // Prepare request payload
            Map<String, Object> requestPayload = buildRequestPayload(systemMessage, userMessage);
            
            // Make API call
            String response = makeOpenAIRequest(requestPayload);
            
            // Parse and validate response
            String generatedContent = parseOpenAIResponse(response, outputFormat);
            
            // Record successful request and token usage
            rateLimitService.recordRequest(userId);
            recordTokenUsage(response, userId);
            
            log.info("Content generation successful - Output length: {} chars, User: {}", 
                    generatedContent.length(), userId);
            
            return generatedContent;
            
        } catch (Exception e) {
            log.error("Failed to generate content with OpenAI API for user: {}", userId, e);
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage());
        }
    }
    
    /**
     * Generate content using OpenAI API (backward compatibility)
     */
    public String generateContent(String prompt, String outputFormat, String additionalContext) {
        return generateContent(prompt, outputFormat, additionalContext, "anonymous");
    }
    
    /**
     * Build system message for OpenAI API
     * @param outputFormat Expected output format
     * @return System message
     */
    private String buildSystemMessage(String outputFormat) {
        // ใช้ prompt สั้นสุดตามที่ต้องการ
        return "JSON only. Be concise.";
    }
    
    /**
     * Build user message for OpenAI API
     * @param prompt User prompt
     * @param additionalContext Additional context
     * @return User message
     */
    private String buildUserMessage(String prompt, String additionalContext) {
        // ตัดความยาว input ก่อนส่ง (จำกัดที่ 1000 characters)
        String truncatedPrompt = prompt.length() > 1000 ? 
            prompt.substring(0, 1000) + "..." : prompt;
        
        StringBuilder message = new StringBuilder(truncatedPrompt);
        
        if (additionalContext != null && !additionalContext.trim().isEmpty()) {
            String truncatedContext = additionalContext.length() > 500 ? 
                additionalContext.substring(0, 500) + "..." : additionalContext;
            message.append("\n\nContext: ").append(truncatedContext);
        }
        
        // เพิ่ม JSON mode enforcement
        message.append("\n\nResponse format: JSON only. Max 256 tokens.");
        
        return message.toString();
    }
    
    /**
     * Build request payload for OpenAI API
     * @param systemMessage System message
     * @param userMessage User message
     * @return Request payload
     */
    private Map<String, Object> buildRequestPayload(String systemMessage, String userMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", openaiModel);
        payload.put("max_tokens", maxTokens);
        payload.put("temperature", temperature);
        
        // เพิ่ม JSON mode เพื่อบังคับ JSON response
        payload.put("response_format", Map.of("type", "json_object"));
        
        payload.put("messages", new Object[]{
            Map.of("role", "system", "content", systemMessage),
            Map.of("role", "user", "content", userMessage)
        });
        
        return payload;
    }
    
    /**
     * Make HTTP request to OpenAI API
     * @param payload Request payload
     * @return API response
     */
    private String makeOpenAIRequest(Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            log.info("Making OpenAI API request to: {}", openaiApiUrl);
            
            ResponseEntity<String> response = restTemplate.postForEntity(openaiApiUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("OpenAI API response received - Status: {}", response.getStatusCode());
                return response.getBody();
            } else {
                log.error("OpenAI API error - Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("OpenAI API returned error status: " + response.getStatusCode());
            }
            
        } catch (ResourceAccessException e) {
            log.error("OpenAI API connection failed", e);
            throw new RuntimeException("Failed to connect to OpenAI API: " + e.getMessage());
        } catch (Exception e) {
            log.error("OpenAI API request failed", e);
            throw new RuntimeException("OpenAI API request failed: " + e.getMessage());
        }
    }
    
    /**
     * Parse OpenAI API response
     * @param response API response
     * @param outputFormat Expected output format
     * @return Parsed content
     */
    private String parseOpenAIResponse(String response, String outputFormat) {
        try {
            // Parse actual OpenAI response
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Extract content from OpenAI response
            String content = jsonNode.get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();
            
            // Validate JSON format
            try {
                objectMapper.readTree(content);
                log.info("Response parsed successfully - Valid JSON content");
                return content;
            } catch (Exception e) {
                log.warn("OpenAI response is not valid JSON, returning as is");
                return content;
            }
            
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response", e);
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage());
        }
    }
    
    /**
     * Validate OpenAI API configuration
     * @return True if configuration is valid
     */
    public boolean isConfigurationValid() {
        return openaiApiKey != null && !openaiApiKey.trim().isEmpty() &&
               !openaiApiKey.equals("your-openai-api-key-here");
    }
    
    /**
     * Test OpenAI API connection
     * @return True if connection successful
     */
    public boolean testConnection() {
        try {
            if (!isConfigurationValid()) {
                log.warn("OpenAI API configuration is invalid");
                return false;
            }
            
            // Test with a simple prompt
            String testPrompt = "Generate a simple JSON response: {\"test\": \"Hello World\"}";
            String systemMessage = "You are a test assistant. Respond with the exact JSON requested.";
            
            Map<String, Object> payload = buildRequestPayload(systemMessage, testPrompt);
            String response = makeOpenAIRequest(payload);
            
            log.info("OpenAI API connection test successful");
            return true;
            
        } catch (Exception e) {
            log.error("OpenAI API connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get estimated generation time for content
     * @param promptLength Prompt length in characters
     * @param outputFormat Expected output format
     * @return Estimated time in seconds
     */
    public int getEstimatedGenerationTime(int promptLength, String outputFormat) {
        // Simple estimation based on prompt length and format complexity
        int baseTime = 5; // Base time in seconds
        int promptFactor = promptLength / 100; // 1 second per 100 characters
        int formatFactor = getFormatComplexityFactor(outputFormat);
        
        return Math.max(baseTime, baseTime + promptFactor + formatFactor);
    }
    
    /**
     * Record token usage from OpenAI response
     * @param response OpenAI API response
     * @param userId User ID for tracking
     */
    private void recordTokenUsage(String response, String userId) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Extract token usage from OpenAI response
            JsonNode usageNode = jsonNode.get("usage");
            if (usageNode != null) {
                int inputTokens = usageNode.get("prompt_tokens").asInt(0);
                int outputTokens = usageNode.get("completion_tokens").asInt(0);
                
                // Record token usage
                tokenUsageService.recordTokenUsage(userId, inputTokens, outputTokens);
                
                log.debug("Recorded token usage - User: {}, Input: {}, Output: {}", 
                         userId, inputTokens, outputTokens);
            }
        } catch (Exception e) {
            log.warn("Failed to record token usage for user: {}", userId, e);
        }
    }
    
    /**
     * Get complexity factor for output format
     * @param outputFormat Output format
     * @return Complexity factor
     */
    private int getFormatComplexityFactor(String outputFormat) {
        switch (outputFormat.toLowerCase()) {
            case "flashcard": return 2;
            case "quiz": return 5;
            case "note": return 3;
            case "summary": return 4;
            default: return 3;
        }
    }
}
