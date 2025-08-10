package se499.kayaanbackend.shared.realtime;

public interface RealtimeBus {
    
    /**
     * Publishes a message to a channel
     * @param channel The channel name
     * @param message The message to publish
     */
    void publish(String channel, Object message);
    
    /**
     * Subscribes to a channel
     * @param channel The channel name
     * @param handler The message handler
     */
    void subscribe(String channel, MessageHandler handler);
    
    /**
     * Unsubscribes from a channel
     * @param channel The channel name
     */
    void unsubscribe(String channel);
    
    /**
     * Message handler interface
     */
    @FunctionalInterface
    interface MessageHandler {
        void handle(String channel, Object message);
    }
}
