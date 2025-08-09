package com.kayaan.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kayaan.ai.entity.AiGenerationJob;
import com.kayaan.ai.repository.AiGenerationJobRepository;
import com.kayaan.ai.service.AiJobService;
import com.kayaan.core.user.UserContext;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Validated
public class AiController {
    private final AiJobService aiJobService;
    private final AiGenerationJobRepository jobRepository;
    private final UserContext userContext;

    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(@RequestBody CreateJobRequest req) {
        Long userId = userContext.getCurrentUserId();
        AiGenerationJob job = aiJobService.enqueue(userId, req.getType(), req.getPrompt());
        return ResponseEntity.ok(new CreateJobResponse(job.getId(), job.getStatus().name()));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        AiGenerationJob job = jobRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(new JobStatusResponse(job.getStatus().name(), job.getOutputRef()));
    }

    @Data
    public static class CreateJobRequest {
        @NotNull private AiGenerationJob.Type type;
        @NotBlank private String prompt;
    }

    @Data
    public static class CreateJobResponse {
        private final Long jobId;
        private final String status;
    }

    @Data
    public static class JobStatusResponse {
        private final String status;
        private final String outputRef;
    }
}


