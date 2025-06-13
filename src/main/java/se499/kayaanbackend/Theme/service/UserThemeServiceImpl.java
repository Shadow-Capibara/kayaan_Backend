package se499.kayaanbackend.Theme.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.Theme.dto.ResetPersonalizationResponse;
import se499.kayaanbackend.Theme.dto.ThemeDto;
import se499.kayaanbackend.Theme.dto.ThemePresetDto;
import se499.kayaanbackend.Theme.dto.UserThemeDto;
import se499.kayaanbackend.Theme.entity.Theme;
import se499.kayaanbackend.Theme.entity.UserTheme;
import se499.kayaanbackend.Theme.repository.UserThemeRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserThemeServiceImpl implements UserThemeService {

    private final UserThemeRepository userThemeRepository;
    private final UserRepository userRepository;
    private final ThemeService themeService;
    private final ObjectMapper objectMapper;

    @Override
    public UserThemeDto getUserTheme(Integer userId) {
        log.debug("Getting theme for user: {}", userId);

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("No theme found for user {}, creating default", userId);
                    return createDefaultUserTheme(Long.valueOf(userId));
                });

        return UserThemeDto.builder()
                .current(convertThemeToDto(userTheme.getCurrentTheme()))
                .presets(getPresetsFromJson(userTheme.getJsonPresets()))
                .build();
    }

    @Override
    @Transactional
    public ThemeDto updateCurrentTheme(Integer userId, ThemeDto themeDto) {
        log.info("Updating current theme for user: {} to theme: {}", userId, themeDto.getId());

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultUserTheme(Long.valueOf(userId)));

        Theme newTheme = themeService.getThemeEntityById(themeDto.getId());
        userTheme.setCurrentTheme(newTheme);

        UserTheme savedUserTheme = userThemeRepository.save(userTheme);
        log.info("Theme updated successfully for user: {}", userId);

        return convertThemeToDto(savedUserTheme.getCurrentTheme());
    }

    @Override
    @Transactional
    public List<ThemeDto> addPreset(Integer userId, ThemeDto themeDto) {
        log.info("Adding preset for user: {}", userId);

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultUserTheme(Long.valueOf(userId)));

        List<ThemePresetDto> presets = getPresetDtosFromJson(userTheme.getJsonPresets());

        // Check if preset limit is reached (e.g., max 10 presets)
        if (presets.size() >= 10) {
            throw new RuntimeException("Maximum number of presets (10) reached");
        }

        // Create new preset with unique ID
        String presetId = UUID.randomUUID().toString();
        String presetName = themeDto.getName() + " Preset " + (presets.size() + 1);

        ThemePresetDto newPreset = ThemePresetDto.builder()
                .id(presetId)
                .name(presetName)
                .theme(themeDto)
                .build();

        presets.add(newPreset);
        userTheme.setJsonPresets(convertPresetsToJson(presets));

        userThemeRepository.save(userTheme);
        log.info("Preset added successfully for user: {}", userId);

        return getPresetsFromJson(userTheme.getJsonPresets());
    }

    @Override
    @Transactional
    public List<ThemeDto> removePreset(Integer userId, String presetId) {
        log.info("Removing preset {} for user: {}", presetId, userId);

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User theme not found for user: " + userId));

        List<ThemePresetDto> presets = getPresetDtosFromJson(userTheme.getJsonPresets());

        boolean removed = presets.removeIf(preset -> preset.getId().equals(presetId));

        if (!removed) {
            throw new RuntimeException("Preset not found with id: " + presetId);
        }

        userTheme.setJsonPresets(convertPresetsToJson(presets));
        userThemeRepository.save(userTheme);

        log.info("Preset removed successfully for user: {}", userId);
        return getPresetsFromJson(userTheme.getJsonPresets());
    }

    @Override
    @Transactional
    public ResetPersonalizationResponse resetToDefaults(Integer userId) {
        log.info("Resetting theme to defaults for user: {}", userId);

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultUserTheme(Long.valueOf(userId)));

        Theme defaultTheme = themeService.getDefaultTheme();
        userTheme.setCurrentTheme(defaultTheme);
        userTheme.setJsonPresets("[]"); // Clear all presets

        userThemeRepository.save(userTheme);
        log.info("Theme reset to defaults for user: {}", userId);

        return ResetPersonalizationResponse.builder()
                .ok(true)
                .message("Theme settings reset to defaults successfully")
                .build();
    }

    @Override
    public boolean hasUserTheme(Integer userId) {
        return userThemeRepository.findByUserId(userId).isPresent();
    }

    @Override
    public List<ThemeDto> getUserPresets(Integer userId) {
        log.debug("Getting presets for user: {}", userId);

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElse(null);

        if (userTheme == null) {
            return new ArrayList<>();
        }

        return getPresetsFromJson(userTheme.getJsonPresets());
    }

    @Override
    @Transactional
    public void clearAllPresets(Integer userId) {
        log.info("Clearing all presets for user: {}", userId);

        UserTheme userTheme = userThemeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User theme not found for user: " + userId));

        userTheme.setJsonPresets("[]");
        userThemeRepository.save(userTheme);

        log.info("All presets cleared for user: {}", userId);
    }

    @Override
    @Transactional
    public void initializeUserTheme(Integer userId) {
        log.info("Initializing theme for new user: {}", userId);

        if (hasUserTheme(userId)) {
            log.warn("User {} already has theme settings", userId);
            return;
        }

        createDefaultUserTheme(Long.valueOf(userId));
        log.info("Theme initialized for user: {}", userId);
    }

    // Helper methods
    @Transactional
    protected UserTheme createDefaultUserTheme(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Theme defaultTheme = themeService.getDefaultTheme();

        UserTheme userTheme = UserTheme.builder()
                .userId(Math.toIntExact(userId))
                .user(user)
                .currentTheme(defaultTheme)
                .jsonPresets("[]")
                .build();

        return userThemeRepository.save(userTheme);
    }

    private ThemeDto convertThemeToDto(Theme theme) {
        return ThemeDto.builder()
                .id(theme.getId())
                .name(theme.getName())
                .primaryColor(theme.getPrimaryColor())
                .secondaryColor(theme.getSecondaryColor())
                .backgroundColor(theme.getBackgroundColor())
                .surfaceColor(theme.getSurfaceColor())
                .textColor(theme.getTextColor())
                .textSecondaryColor(theme.getTextSecondaryColor())
                .borderColor(theme.getBorderColor())
                .successColor(theme.getSuccessColor())
                .warningColor(theme.getWarningColor())
                .errorColor(theme.getErrorColor())
                .isDark(theme.getIsDark())
                .isHighContrast(theme.getIsHighContrast())
                .isSystemTheme(theme.getIsSystemTheme())
                .build();
    }

    private List<ThemeDto> getPresetsFromJson(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return new ArrayList<>();
        }

        try {
            List<ThemePresetDto> presetDtos = objectMapper.readValue(json,
                    new TypeReference<List<ThemePresetDto>>() {});
            return presetDtos.stream()
                    .map(ThemePresetDto::getTheme)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            log.error("Error parsing presets JSON: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ThemePresetDto> getPresetDtosFromJson(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json,
                    new TypeReference<List<ThemePresetDto>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error parsing preset DTOs JSON: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private String convertPresetsToJson(List<ThemePresetDto> presets) {
        try {
            return objectMapper.writeValueAsString(presets);
        } catch (JsonProcessingException e) {
            log.error("Error converting presets to JSON: {}", e.getMessage());
            return "[]";
        }
    }
}