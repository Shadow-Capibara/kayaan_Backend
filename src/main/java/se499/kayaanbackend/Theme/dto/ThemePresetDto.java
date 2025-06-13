package se499.kayaanbackend.Theme.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemePresetDto {
    private String id;
    private String name;
    private ThemeDto theme;
}