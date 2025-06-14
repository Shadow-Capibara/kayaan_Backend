package se499.kayaanbackend.Theme.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserThemeDto {
    private ThemeDto current;
    private List<ThemeDto> presets;
}