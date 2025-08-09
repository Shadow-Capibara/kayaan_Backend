package com.kayaan.streaks.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.streaks.entity.StreakEvent;

public interface StreakEventRepository extends JpaRepository<StreakEvent, Long> {
    Optional<StreakEvent> findByUserIdAndDateAndType(Long userId, LocalDate date, StreakEvent.Type type);
}


