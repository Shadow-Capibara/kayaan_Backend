package com.kayaan.streaks.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kayaan.streaks.entity.StreakEvent;
import com.kayaan.streaks.service.StreakService;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/streaks")
@RequiredArgsConstructor
@Validated
public class StreakController {
    private final StreakService streakService;

    @PostMapping("/events")
    public ResponseEntity<?> addEvent(@RequestBody AddEventRequest req) {
        return ResponseEntity.ok(streakService.addEvent(req.getType()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(streakService.getMySummary());
    }

    @Data
    public static class AddEventRequest {
        @NotNull private StreakEvent.Type type;
    }
}


