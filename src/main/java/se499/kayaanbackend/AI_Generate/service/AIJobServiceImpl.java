package se499.kayaanbackend.AI_Generate.service;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se499.kayaanbackend.AI_Generate.entity.AIJob;
import se499.kayaanbackend.AI_Generate.entity.AIDraft;
import se499.kayaanbackend.AI_Generate.repository.AIJobRepository;
import se499.kayaanbackend.AI_Generate.repository.AIDraftRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.shared.storage.StorageService;

@Service
@Transactional
public class AIJobServiceImpl implements AIJobService {
    
    @Autowired
    private StorageService storageService;
    
    @Autowired
    private AIJobRepository aiJobRepository;
    
    @Autowired
    private AIDraftRepository aiDraftRepository;
    
    @Value("${kayaan.supabase.buckets.ai}")
    private String aiBucket;
    
    @Override
    public StorageService.SignedUrl requestOutputUploadUrl(String jobId, String fileName, String contentType) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String path = String.format("jobs/%s/%s_%s", jobId, timestamp, fileName);
        
        return storageService.createSignedUploadUrl(
            aiBucket,
            path,
            1800, // 30 minutes for AI jobs
            contentType
        );
    }
    
    @Override
    public AIJob createJob(User user, String jobType, String inputData) {
        AIJob job = AIJob.builder()
            .user(user)
            .jobType(jobType)
            .status("pending")
            .inputData(inputData)
            .createdAt(LocalDateTime.now())
            .build();
        
        return aiJobRepository.save(job);
    }
    
    @Override
    public AIJob processJob(Long jobId) {
        AIJob job = aiJobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        job.setStatus("processing");
        job.setUpdatedAt(LocalDateTime.now());
        
        // TODO: Implement actual AI processing logic here
        job.setStatus("completed");
        job.setCompletedAt(LocalDateTime.now());
        
        return aiJobRepository.save(job);
    }
    
    @Override
    public AIDraft createDraft(User user, String draftType, String title, String content) {
        AIDraft draft = AIDraft.builder()
            .user(user)
            .draftType(draftType)
            .title(title)
            .content(content)
            .isSaved(false)
            .createdAt(LocalDateTime.now())
            .build();
        
        return aiDraftRepository.save(draft);
    }
    
    @Override
    public AIDraft saveDraft(Long draftId) {
        AIDraft draft = aiDraftRepository.findById(draftId)
            .orElseThrow(() -> new RuntimeException("Draft not found"));
        
        draft.setIsSaved(true);
        draft.setUpdatedAt(LocalDateTime.now());
        
        return aiDraftRepository.save(draft);
    }
}
