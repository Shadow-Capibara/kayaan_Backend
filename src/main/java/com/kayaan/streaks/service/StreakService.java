package com.kayaan.streaks.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kayaan.core.user.UserContext;
import com.kayaan.streaks.entity.StreakEvent;
import com.kayaan.streaks.entity.StreakSummary;
import com.kayaan.streaks.repository.StreakEventRepository;
import com.kayaan.streaks.repository.StreakSummaryRepository;
import com.kayaan.stream.SseHub;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreakService {
    private final StreakEventRepository streakEventRepository;
    private final StreakSummaryRepository streakSummaryRepository;
    private final UserContext userContext;
    private final SseHub sseHub;

    private static final ZoneId ZONE = ZoneId.of("Asia/Bangkok");

    public record SummaryResponse(int current, int longest, LocalDate lastActiveDate) {}

    @Transactional
    public SummaryResponse addEvent(StreakEvent.Type type) {
        Long userId = userContext.getCurrentUserId();
        LocalDate today = LocalDate.now(ZONE);
        StreakEvent event = streakEventRepository.findByUserIdAndDateAndType(userId, today, type)
                .orElseGet(() -> StreakEvent.builder()
                        .userId(userId)
                        .date(today)
                        .type(type)
                        .count(0)
                        .build());
        event.setCount(event.getCount() + 1);
        streakEventRepository.save(event);

        StreakSummary summary = streakSummaryRepository.findById(userId)
                .orElse(StreakSummary.builder()
                        .userId(userId)
                        .currentStreak(0)
                        .longestStreak(0)
                        .build());

        // simple streak calc: if lastActiveDate is yesterday, increment; else reset to 1
        if (summary.getLastActiveDate() != null && summary.getLastActiveDate().equals(today.minusDays(1))) {
            summary.setCurrentStreak(summary.getCurrentStreak() + 1);
        } else if (summary.getLastActiveDate() == null || summary.getLastActiveDate().isBefore(today)) {
            summary.setCurrentStreak(1);
        }
        summary.setLastActiveDate(today);
        if (summary.getCurrentStreak() > summary.getLongestStreak()) {
            summary.setLongestStreak(summary.getCurrentStreak());
        }
        streakSummaryRepository.save(summary);

        SummaryResponse resp = new SummaryResponse(summary.getCurrentStreak(), summary.getLongestStreak(), summary.getLastActiveDate());
        sseHub.broadcast("streak_update", toJson(Map.of(
                "userId", userId,
                "current", resp.current(),
                "longest", resp.longest()
        )));
        return resp;
    }

    public SummaryResponse getMySummary() {
        Long userId = userContext.getCurrentUserId();
        StreakSummary summary = streakSummaryRepository.findById(userId)
                .orElse(StreakSummary.builder().userId(userId).currentStreak(0).longestStreak(0).build());
        return new SummaryResponse(summary.getCurrentStreak(), summary.getLongestStreak(), summary.getLastActiveDate());
    }

    private String toJson(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\":" + (e.getValue() instanceof Number ? e.getValue() : ("\"" + e.getValue() + "\"")))
                .reduce((a, b) -> a + "," + b)
                .map(s -> "{" + s + "}")
                .orElse("{}");
    }
}


