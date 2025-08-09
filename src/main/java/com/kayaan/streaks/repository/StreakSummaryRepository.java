package com.kayaan.streaks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.streaks.entity.StreakSummary;

public interface StreakSummaryRepository extends JpaRepository<StreakSummary, Long> {
}


