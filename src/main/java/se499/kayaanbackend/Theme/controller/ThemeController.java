package se499.kayaanbackend.Theme.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Theme.dto.ResetPersonalizationResponse;
import se499.kayaanbackend.Theme.dto.ThemeDto;
import se499.kayaanbackend.Theme.dto.UserThemeDto;
import se499.kayaanbackend.Theme.service.ThemeService;
import se499.kayaanbackend.Theme.service.UserThemeService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;
    private final UserThemeService userThemeService;

    // Public endpoint - รายชื่อธีมสาธารณะ
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeDto>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllSystemThemes());
    }

    // Get user's current theme and presets
    @GetMapping("/users/{id}/theme")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserThemeDto> getUserTheme(@PathVariable Integer id) {
        return ResponseEntity.ok(userThemeService.getUserTheme(id));
    }

    // Update user's current theme
    @PutMapping("/users/{id}/theme")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ThemeDto> updateUserTheme(
            @PathVariable Integer id,
            @RequestBody ThemeDto themeDto) {
        return ResponseEntity.ok(userThemeService.updateCurrentTheme(id, themeDto));
    }

    // Save theme as preset
    @PostMapping("/users/{id}/presets")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<List<ThemeDto>> savePreset(
            @PathVariable Integer id,
            @RequestBody ThemeDto themeDto) {
        return ResponseEntity.ok(userThemeService.addPreset(id, themeDto));
    }

    // Delete preset
    @DeleteMapping("/users/{id}/presets/{presetId}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<List<ThemeDto>> deletePreset(
            @PathVariable Integer id,
            @PathVariable String presetId) {
        return ResponseEntity.ok(userThemeService.removePreset(id, presetId));
    }

    // Reset to defaults
    @PostMapping("/users/{id}/reset-personalization")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ResetPersonalizationResponse> resetPersonalization(
            @PathVariable Integer id) {
        return ResponseEntity.ok(userThemeService.resetToDefaults(id));
    }
}