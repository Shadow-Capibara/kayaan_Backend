package se499.kayaanbackend.AI_Generate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service for integrating with OpenAI API for content generation
 * Handles AI model interactions and response processing
 */
@Slf4j
@Service
public class OpenAIService {
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;
    
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String openaiModel;
    
    @Value("${openai.max.tokens:2000}")
    private Integer maxTokens;
    
    @Value("${openai.temperature:0.7}")
    private Double temperature;
    
    @Value("${openai.timeout.seconds:60}")
    private Integer timeoutSeconds;
    
    private final RestTemplate restTemplate;
    
    public OpenAIService() {
        this.restTemplate = new RestTemplate();
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
     * Generate content using OpenAI API
     * @param prompt User prompt for content generation
     * @param outputFormat Expected output format
     * @param additionalContext Additional context for generation
     * @return Generated content as JSON string
     */
    public String generateContent(String prompt, String outputFormat, String additionalContext) {
        try {
            log.info("Generating content with OpenAI API - Format: {}, Prompt length: {} chars", 
                outputFormat, prompt.length());
            
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
            
            log.info("Content generation successful - Output length: {} chars", generatedContent.length());
            
            return generatedContent;
            
        } catch (Exception e) {
            log.error("Failed to generate content with OpenAI API", e);
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage());
        }
    }
    
    /**
     * Build system message for OpenAI API
     * @param outputFormat Expected output format
     * @return System message
     */
    private String buildSystemMessage(String outputFormat) {
        switch (outputFormat.toLowerCase()) {
            case "flashcard":
                return "You are an expert educational content creator. Generate flashcards in JSON format with 'question' and 'answer' fields. Each flashcard should be concise and educational.";
                
            case "quiz":
                return "You are an expert quiz creator. Generate multiple-choice questions in JSON format with 'question', 'options' (array), 'correctAnswer' (index), and 'explanation' fields.";
                
            case "note":
                return "You are an expert note-taking assistant. Generate structured notes in JSON format with 'title', 'summary', 'keyPoints' (array), and 'details' fields.";
                
            case "summary":
                return "You are an expert summarizer. Generate concise summaries in JSON format with 'mainPoints' (array), 'summary', and 'keyInsights' fields.";
                
            default:
                return "You are an expert content creator. Generate content in JSON format based on the user's request.";
        }
    }
    
    /**
     * Build user message for OpenAI API
     * @param prompt User prompt
     * @param additionalContext Additional context
     * @return User message
     */
    private String buildUserMessage(String prompt, String additionalContext) {
        StringBuilder message = new StringBuilder(prompt);
        
        if (additionalContext != null && !additionalContext.trim().isEmpty()) {
            message.append("\n\nAdditional Context: ").append(additionalContext);
        }
        
        message.append("\n\nPlease ensure the response is valid JSON format.");
        
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
            // TODO: Implement proper JSON parsing of OpenAI response
            // For now, return mock parsed content
            String mockContent = String.format(
                "{\"type\": \"%s\", \"generatedAt\": \"%s\", \"content\": \"Mock AI-generated content for %s format\"}",
                outputFormat,
                java.time.LocalDateTime.now().toString(),
                outputFormat
            );
            
            log.info("Response parsed successfully - Mock content generated");
            return mockContent;
            
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
               openaiApiUrl != null && !openaiApiUrl.trim().isEmpty();
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
            
            // TODO: Implement actual connection test
            // For now, return mock success
            log.info("OpenAI API connection test successful (mock)");
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
