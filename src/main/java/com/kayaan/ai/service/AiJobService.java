package com.kayaan.ai.service;

import com.kayaan.ai.entity.AiGenerationJob;
import com.kayaan.ai.repository.AiGenerationJobRepository;
import com.kayaan.content.entity.Content;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiJobService {
    private final AiGenerationJobRepository jobRepository;
    private final com.kayaan.content.repository.ContentRepository contentRepository;

    public AiGenerationJob enqueue(Long userId, AiGenerationJob.Type type, String prompt) {
        AiGenerationJob job = AiGenerationJob.builder()
                .userId(userId)
                .type(type)
                .prompt(prompt)
                .status(AiGenerationJob.Status.queued)
                .provider("mock")
                .model("mock-v1")
                .temperature(0.7)
                .build();
        return jobRepository.save(job);
    }

    @Transactional
    public boolean processOneQueuedJob() {
        List<AiGenerationJob> list = jobRepository.findTop1ByStatusOrderByCreatedAtAsc(AiGenerationJob.Status.queued);
        if (list.isEmpty()) return false;
        AiGenerationJob job = list.get(0);
        job.setStatus(AiGenerationJob.Status.running);
        jobRepository.save(job);
        try {
            // mock generation -> create content draft
            Content content = contentRepository.save(Content.builder()
                    .ownerId(job.getUserId())
                    .kind(switch (job.getType()) {
                        case quiz -> Content.Kind.quiz;
                        case flashcard -> Content.Kind.flashcard;
                        case note -> Content.Kind.note;
                    })
                    .title("AI Draft: " + job.getType())
                    .body("{\"prompt\":\"" + job.getPrompt().replace("\"", "'") + "\",\"draft\":true}")
                    .visibility(Content.Visibility.private_)
                    .build());
            job.setOutputRef("content:" + content.getId());
            job.setStatus(AiGenerationJob.Status.done);
            jobRepository.save(job);
            return true;
        } catch (Exception e) {
            job.setStatus(AiGenerationJob.Status.failed);
            jobRepository.save(job);
            return true;
        }
    }
}


