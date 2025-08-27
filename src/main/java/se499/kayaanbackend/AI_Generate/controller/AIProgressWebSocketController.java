package se499.kayaanbackend.AI_Generate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import se499.kayaanbackend.AI_Generate.service.ProgressTrackingService;

/**
 * WebSocket Controller for AI Generation Progress Updates
 * Handles real-time communication with Frontend for progress tracking
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AIProgressWebSocketController {
    
    private final ProgressTrackingService progressTrackingService;
    
    /**
     * Subscribe to generation progress updates
     * @param requestId Generation request ID
     * @param headerAccessor WebSocket session information
     * @return Confirmation message
     */
    @MessageMapping("/ai/generation/{requestId}/subscribe")
    @SendTo("/topic/user/generation/{requestId}/progress")
    public String subscribeToProgress(Long requestId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("User {} subscribed to progress updates for generation request: {}", sessionId, requestId);
        
        return "Subscribed to progress updates for generation request: " + requestId;
    }
    
    /**
     * Unsubscribe from generation progress updates
     * @param requestId Generation request ID
     * @param headerAccessor WebSocket session information
     * @return Confirmation message
     */
    @MessageMapping("/ai/generation/{requestId}/unsubscribe")
    @SendTo("/topic/user/generation/{requestId}/progress")
    public String unsubscribeFromProgress(Long requestId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("User {} unsubscribed from progress updates for generation request: {}", sessionId, requestId);
        
        return "Unsubscribed from progress updates for generation request: " + requestId;
    }
    
    /**
     * Get current progress for a generation request
     * @param requestId Generation request ID
     * @param headerAccessor WebSocket session information
     * @return Current progress data
     */
    @MessageMapping("/ai/generation/{requestId}/progress")
    @SendTo("/topic/user/generation/{requestId}/progress")
    public Object getCurrentProgress(Long requestId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.debug("User {} requested current progress for generation request: {}", sessionId, requestId);
        
        return progressTrackingService.getProgress(requestId);
    }
    
    /**
     * Heartbeat/ping to keep connection alive
     * @param requestId Generation request ID
     * @param headerAccessor WebSocket session information
     * @return Pong response
     */
    @MessageMapping("/ai/generation/{requestId}/ping")
    @SendTo("/topic/user/generation/{requestId}/progress")
    public String ping(Long requestId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.debug("Ping from user {} for generation request: {}", sessionId, requestId);
        
        return "pong";
    }
}
