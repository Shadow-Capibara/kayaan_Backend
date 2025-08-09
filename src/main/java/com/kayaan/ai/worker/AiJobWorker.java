package com.kayaan.ai.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kayaan.ai.service.AiJobService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiJobWorker {
    private static final Logger log = LoggerFactory.getLogger(AiJobWorker.class);
    private final AiJobService aiJobService;

    @Scheduled(fixedDelay = 10000)
    public void poll() {
        boolean processed = aiJobService.processOneQueuedJob();
        if (processed) {
            log.info("Processed one AI job");
        }
    }
}


