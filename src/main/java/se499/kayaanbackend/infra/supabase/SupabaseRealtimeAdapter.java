package se499.kayaanbackend.infra.supabase;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.shared.realtime.RealtimeBus;

@Slf4j
@Component
public class SupabaseRealtimeAdapter implements RealtimeBus {
    
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<MessageHandler>> subscriptions = new ConcurrentHashMap<>();
    
    @Override
    public void publish(String channel, Object message) {
        log.info("Publishing message to channel {}: {}", channel, message);
        // TODO: Implement actual Supabase Realtime publish
        // For now, just log the message
    }
    
    @Override
    public void subscribe(String channel, MessageHandler handler) {
        log.info("Subscribing to channel: {}", channel);
        subscriptions.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>()).add(handler);
        // TODO: Implement actual Supabase Realtime subscribe
    }
    
    @Override
    public void unsubscribe(String channel) {
        log.info("Unsubscribing from channel: {}", channel);
        subscriptions.remove(channel);
        // TODO: Implement actual Supabase Realtime unsubscribe
    }
    
    /**
     * Notifies all local subscribers of a message
     * This is a helper method for testing and local development
     */
    public void notifyLocalSubscribers(String channel, Object message) {
        CopyOnWriteArrayList<MessageHandler> handlers = subscriptions.get(channel);
        if (handlers != null) {
            handlers.forEach(handler -> {
                try {
                    handler.handle(channel, message);
                } catch (Exception e) {
                    log.error("Error handling message on channel {}: {}", channel, e.getMessage());
                }
            });
        }
    }
}
