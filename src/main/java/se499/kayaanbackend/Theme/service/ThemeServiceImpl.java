package se499.kayaanbackend.Theme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.Theme.dto.ThemeDto;
import se499.kayaanbackend.Theme.entity.Theme;
import se499.kayaanbackend.Theme.repository.ThemeRepository;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;

    @Override
    public List<ThemeDto> getAllSystemThemes() {
        log.debug("Fetching all system themes");
        return themeRepository.findByIsSystemThemeTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ThemeDto getThemeById(Long id) {
        log.debug("Fetching theme with id: {}", id);
        Theme theme = getThemeEntityById(id);
        return convertToDto(theme);
    }

    @Override
    public Theme getThemeEntityById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Theme not found with id: {}", id);
                    return new RuntimeException("Theme not found with id: " + id);
                });
    }

    @Override
    public Theme getDefaultTheme() {
        log.debug("Fetching default theme");
        return themeRepository.findByName("Light")
                .orElseThrow(() -> {
                    log.error("Default theme 'Light' not found");
                    return new RuntimeException("Default theme not found");
                });
    }

    @Override
    @Transactional
    public ThemeDto createTheme(ThemeDto themeDto) {
        log.info("Creating new theme: {}", themeDto.getName());

        // Check if theme with same name already exists
        if (themeRepository.findByName(themeDto.getName()).isPresent()) {
            throw new RuntimeException("Theme with name '" + themeDto.getName() + "' already exists");
        }

        Theme theme = convertToEntity(themeDto);
        theme.setIsSystemTheme(false); // User-created themes are not system themes

        Theme savedTheme = themeRepository.save(theme);
        log.info("Theme created successfully with id: {}", savedTheme.getId());

        return convertToDto(savedTheme);
    }

    @Override
    @Transactional
    public ThemeDto updateTheme(Long id, ThemeDto themeDto) {
        log.info("Updating theme with id: {}", id);

        Theme existingTheme = getThemeEntityById(id);

        // Check if it's a system theme
        if (existingTheme.getIsSystemTheme()) {
            throw new RuntimeException("Cannot update system theme");
        }

        // Update theme properties
        updateThemeProperties(existingTheme, themeDto);

        Theme updatedTheme = themeRepository.save(existingTheme);
        log.info("Theme updated successfully with id: {}", updatedTheme.getId());

        return convertToDto(updatedTheme);
    }

    @Override
    @Transactional
    public void deleteTheme(Long id) {
        log.info("Deleting theme with id: {}", id);

        Theme theme = getThemeEntityById(id);

        // Check if it's a system theme
        if (theme.getIsSystemTheme()) {
            throw new RuntimeException("Cannot delete system theme");
        }

        themeRepository.delete(theme);
        log.info("Theme deleted successfully with id: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        return themeRepository.existsById(id);
    }

    @Override
    public ThemeDto getThemeByName(String name) {
        log.debug("Fetching theme by name: {}", name);
        return themeRepository.findByName(name)
                .map(this::convertToDto)
                .orElse(null);
    }

    // Helper methods
    private ThemeDto convertToDto(Theme theme) {
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

    private Theme convertToEntity(ThemeDto dto) {
        return Theme.builder()
                .name(dto.getName())
                .primaryColor(dto.getPrimaryColor())
                .secondaryColor(dto.getSecondaryColor())
                .backgroundColor(dto.getBackgroundColor())
                .surfaceColor(dto.getSurfaceColor())
                .textColor(dto.getTextColor())
                .textSecondaryColor(dto.getTextSecondaryColor())
                .borderColor(dto.getBorderColor())
                .successColor(dto.getSuccessColor())
                .warningColor(dto.getWarningColor())
                .errorColor(dto.getErrorColor())
                .isDark(dto.getIsDark())
                .isHighContrast(dto.getIsHighContrast())
                .build();
    }

    private void updateThemeProperties(Theme theme, ThemeDto dto) {
        theme.setName(dto.getName());
        theme.setPrimaryColor(dto.getPrimaryColor());
        theme.setSecondaryColor(dto.getSecondaryColor());
        theme.setBackgroundColor(dto.getBackgroundColor());
        theme.setSurfaceColor(dto.getSurfaceColor());
        theme.setTextColor(dto.getTextColor());
        theme.setTextSecondaryColor(dto.getTextSecondaryColor());
        theme.setBorderColor(dto.getBorderColor());
        theme.setSuccessColor(dto.getSuccessColor());
        theme.setWarningColor(dto.getWarningColor());
        theme.setErrorColor(dto.getErrorColor());
        theme.setIsDark(dto.getIsDark());
        theme.setIsHighContrast(dto.getIsHighContrast());
    }
}