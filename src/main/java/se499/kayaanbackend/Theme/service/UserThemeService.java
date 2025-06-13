package se499.kayaanbackend.Theme.service;



import se499.kayaanbackend.Theme.dto.ResetPersonalizationResponse;
import se499.kayaanbackend.Theme.dto.ThemeDto;
import se499.kayaanbackend.Theme.dto.UserThemeDto;

import java.util.List;

public interface UserThemeService {

    /**
     * Get user's current theme and presets
     * @param userId User ID
     * @return UserThemeDto containing current theme and presets
     */
    UserThemeDto getUserTheme(Integer userId);

    /**
     * Update user's current theme
     * @param userId User ID
     * @param themeDto New theme to set as current
     * @return Updated current theme DTO
     */
    ThemeDto updateCurrentTheme(Integer userId, ThemeDto themeDto);

    /**
     * Add a new preset for the user
     * @param userId User ID
     * @param themeDto Theme to save as preset
     * @return Updated list of all user presets
     */
    List<ThemeDto> addPreset(Integer userId, ThemeDto themeDto);

    /**
     * Remove a preset from user's collection
     * @param userId User ID
     * @param presetId Preset ID to remove
     * @return Updated list of all user presets
     * @throws RuntimeException if preset not found
     */
    List<ThemeDto> removePreset(Integer userId, String presetId);

    /**
     * Reset user's theme settings to defaults
     * @param userId User ID
     * @return Reset confirmation response
     */
    ResetPersonalizationResponse resetToDefaults(Integer userId);

    /**
     * Check if user has theme settings
     * @param userId User ID
     * @return true if user has theme settings, false otherwise
     */
    boolean hasUserTheme(Integer userId);

    /**
     * Get all presets for a user
     * @param userId User ID
     * @return List of preset themes
     */
    List<ThemeDto> getUserPresets(Integer userId);

    /**
     * Clear all presets for a user
     * @param userId User ID
     */
    void clearAllPresets(Integer userId);

    /**
     * Initialize theme for new user
     * @param userId User ID
     */
    void initializeUserTheme(Integer userId);
}