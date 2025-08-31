package se499.kayaanbackend.AI_Generate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for tracking AI generation progress and sending real-time updates
 * Uses WebSocket to send progress updates to connected clients
 */
@Slf4j
@Service
public class ProgressTrackingService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    // In-memory storage for progress tracking (in production, use Redis or database)
    private final Map<Long, GenerationProgress> progressMap = new ConcurrentHashMap<>();
    
    public ProgressTrackingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Start tracking progress for a generation request
     * @param requestId Generation request ID
     * @param userId User ID
     * @param totalSteps Total number of steps in generation process
     */
    public void startProgressTracking(Long requestId, Long userId, int totalSteps) {
        GenerationProgress progress = new GenerationProgress(requestId, userId, totalSteps);
        progressMap.put(requestId, progress);
        
        log.info("Started progress tracking for request: {} (user: {}, total steps: {})", 
                requestId, userId, totalSteps);
        
        // Send initial progress update
        sendProgressUpdate(requestId, userId, progress);
    }
    
    /**
     * Update progress for a generation request
     * @param requestId Generation request ID
     * @param currentStep Current step number
     * @param stepDescription Description of current step
     * @param stepProgress Progress within current step (0-100)
     */
    public void updateProgress(Long requestId, int currentStep, String stepDescription, int stepProgress) {
        GenerationProgress progress = progressMap.get(requestId);
        if (progress == null) {
            log.warn("Progress not found for request: {}", requestId);
            return;
        }
        
        progress.updateStep(currentStep, stepDescription, stepProgress);
        
        log.debug("Updated progress for request: {} - Step {}/{}: {} ({}%)", 
                requestId, currentStep, progress.getTotalSteps(), stepDescription, stepProgress);
        
        // Send progress update to client
        sendProgressUpdate(requestId, progress.getUserId(), progress);
    }
    
    /**
     * Complete a step in the generation process
     * @param requestId Generation request ID
     * @param stepNumber Step number that was completed
     */
    public void completeStep(Long requestId, int stepNumber) {
        GenerationProgress progress = progressMap.get(requestId);
        if (progress == null) {
            log.warn("Progress not found for request: {}", requestId);
            return;
        }
        
        progress.completeStep(stepNumber);
        
        log.info("Completed step {}/{} for request: {}", 
                stepNumber, progress.getTotalSteps(), requestId);
        
        // Send progress update to client
        sendProgressUpdate(requestId, progress.getUserId(), progress);
    }
    
    /**
     * Mark generation as completed
     * @param requestId Generation request ID
     * @param result Result data or message
     */
    public void completeGeneration(Long requestId, String result) {
        GenerationProgress progress = progressMap.get(requestId);
        if (progress == null) {
            log.warn("Progress not found for request: {}", requestId);
            return;
        }
        
        progress.complete(result);
        
        log.info("Completed generation for request: {} with result: {}", requestId, result);
        
        // Send final progress update to client
        sendProgressUpdate(requestId, progress.getUserId(), progress);
        
        // Clean up progress tracking
        progressMap.remove(requestId);
    }
    
    /**
     * Mark generation as failed
     * @param requestId Generation request ID
     * @param errorMessage Error message
     */
    public void failGeneration(Long requestId, String errorMessage) {
        GenerationProgress progress = progressMap.get(requestId);
        if (progress == null) {
            log.warn("Progress not found for request: {}", requestId);
            return;
        }
        
        progress.fail(errorMessage);
        
        log.error("Generation failed for request: {} - Error: {}", requestId, errorMessage);
        
        // Send error update to client
        sendProgressUpdate(requestId, progress.getUserId(), progress);
        
        // Clean up progress tracking
        progressMap.remove(requestId);
    }
    
    /**
     * Get current progress for a generation request
     * @param requestId Generation request ID
     * @return Current progress or null if not found
     */
    public GenerationProgress getProgress(Long requestId) {
        return progressMap.get(requestId);
    }
    
    /**
     * Send progress update via WebSocket
     * @param requestId Generation request ID
     * @param userId User ID
     * @param progress Progress data
     */
    private void sendProgressUpdate(Long requestId, Long userId, GenerationProgress progress) {
        try {
            String destination = "/topic/user/" + userId + "/generation/" + requestId + "/progress";
            messagingTemplate.convertAndSend(destination, progress);
            
            log.debug("Sent progress update to: {} - Progress: {}", destination, progress);
        } catch (Exception e) {
            log.error("Failed to send progress update for request: {}", requestId, e);
        }
    }
    
    /**
     * Inner class to represent generation progress
     */
    public static class GenerationProgress {
        private final Long requestId;
        private final Long userId;
        private final int totalSteps;
        private final AtomicInteger currentStep;
        private String currentStepDescription;
        private int stepProgress; // 0-100
        private String status; // "processing", "completed", "failed"
        private String result;
        private String errorMessage;
        private final long startTime;
        private long lastUpdateTime;
        
        public GenerationProgress(Long requestId, Long userId, int totalSteps) {
            this.requestId = requestId;
            this.userId = userId;
            this.totalSteps = totalSteps;
            this.currentStep = new AtomicInteger(0);
            this.status = "processing";
            this.startTime = System.currentTimeMillis();
            this.lastUpdateTime = this.startTime;
        }
        
        public void updateStep(int stepNumber, String description, int progress) {
            this.currentStep.set(stepNumber);
            this.currentStepDescription = description;
            this.stepProgress = Math.max(0, Math.min(100, progress));
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public void completeStep(int stepNumber) {
            this.currentStep.set(stepNumber);
            this.stepProgress = 100;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public void complete(String result) {
            this.status = "completed";
            this.result = result;
            this.stepProgress = 100;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public void fail(String errorMessage) {
            this.status = "failed";
            this.errorMessage = errorMessage;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        // Getters
        public Long getRequestId() { return requestId; }
        public Long getUserId() { return userId; }
        public int getTotalSteps() { return totalSteps; }
        public int getCurrentStep() { return currentStep.get(); }
        public String getCurrentStepDescription() { return currentStepDescription; }
        public int getStepProgress() { return stepProgress; }
        public String getStatus() { return status; }
        public String getResult() { return result; }
        public String getErrorMessage() { return errorMessage; }
        public long getStartTime() { return startTime; }
        public long getLastUpdateTime() { return lastUpdateTime; }
        public long getElapsedTime() { return System.currentTimeMillis() - startTime; }
        
        /**
         * Get overall progress percentage (0-100)
         * @return Overall progress percentage
         */
        public int getOverallProgress() {
            if (totalSteps == 0) return 0;
            return Math.min(100, (currentStep.get() * 100) / totalSteps);
        }
        
        /**
         * Check if generation is completed
         * @return True if completed
         */
        public boolean isCompleted() {
            return "completed".equals(status);
        }
        
        /**
         * Check if generation failed
         * @return True if failed
         */
        public boolean isFailed() {
            return "failed".equals(status);
        }
        
        /**
         * Check if generation is still processing
         * @return True if processing
         */
        public boolean isProcessing() {
            return "processing".equals(status);
        }
    }
}
