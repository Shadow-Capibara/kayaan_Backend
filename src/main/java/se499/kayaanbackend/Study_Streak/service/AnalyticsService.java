package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;

import se499.kayaanbackend.Study_Streak.dto.AnalyticsResponseDTO;

public interface AnalyticsService {

    // Get daily analytics
    AnalyticsResponseDTO getDailyAnalytics(Integer userId, LocalDate date);

    // Get weekly analytics
    AnalyticsResponseDTO getWeeklyAnalytics(Integer userId, LocalDate startDate);

    // Get monthly analytics
    AnalyticsResponseDTO getMonthlyAnalytics(Integer userId, String yearMonth);

    // Get study trends
    AnalyticsResponseDTO getStudyTrends(Integer userId, Integer days);

    // Get overall study summary
    AnalyticsResponseDTO getStudySummary(Integer userId);
} 