package se499.kayaanbackend.Theme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDto {
    private Long id;
    private String name;
    private String primaryColor;
    private String secondaryColor;
    private String backgroundColor;
    private String surfaceColor;
    private String textColor;
    private String textSecondaryColor;
    private String borderColor;
    private String successColor;
    private String warningColor;
    private String errorColor;
    private Boolean isDark;
    private Boolean isHighContrast;
    private Boolean isSystemTheme;
}