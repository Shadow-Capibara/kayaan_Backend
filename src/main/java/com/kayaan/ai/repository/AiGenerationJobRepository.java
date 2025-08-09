package com.kayaan.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.ai.entity.AiGenerationJob;

public interface AiGenerationJobRepository extends JpaRepository<AiGenerationJob, Long> {
    List<AiGenerationJob> findTop1ByStatusOrderByCreatedAtAsc(AiGenerationJob.Status status);
}

