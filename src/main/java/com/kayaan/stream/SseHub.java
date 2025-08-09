package com.kayaan.stream;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseHub {
    private final Map<String, SseEmitter> clientIdToEmitter = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String clientId) {
        SseEmitter emitter = new SseEmitter(0L);
        clientIdToEmitter.put(clientId, emitter);
        emitter.onCompletion(() -> clientIdToEmitter.remove(clientId));
        emitter.onTimeout(() -> clientIdToEmitter.remove(clientId));
        return emitter;
    }

    public void broadcast(String eventName, String jsonPayload) {
        clientIdToEmitter.forEach((id, emitter) -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .name(eventName)
                        .data(jsonPayload, MediaType.APPLICATION_JSON);
                emitter.send(event);
            } catch (IOException e) {
                emitter.completeWithError(e);
                clientIdToEmitter.remove(id);
            }
        });
    }
}


