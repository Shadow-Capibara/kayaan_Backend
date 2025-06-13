package se499.kayaanbackend.Theme.service;



import se499.kayaanbackend.Theme.dto.ThemeDto;
import se499.kayaanbackend.Theme.entity.Theme;

import java.util.List;

public interface ThemeService {

    List<ThemeDto> getAllSystemThemes();

    ThemeDto getThemeById(Long id);

    Theme getThemeEntityById(Long id);

    Theme getDefaultTheme();

    ThemeDto createTheme(ThemeDto themeDto);

    ThemeDto updateTheme(Long id, ThemeDto themeDto);

    void deleteTheme(Long id);

    boolean existsById(Long id);

    ThemeDto getThemeByName(String name);
}