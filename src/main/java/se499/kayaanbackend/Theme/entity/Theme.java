package se499.kayaanbackend.Theme.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "theme")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "primary_color", nullable = false, length = 7)
    private String primaryColor;

    @Column(name = "secondary_color", nullable = false, length = 7)
    private String secondaryColor;

    @Column(name = "background_color", length = 7)
    private String backgroundColor;

    @Column(name = "surface_color", length = 7)
    private String surfaceColor;

    @Column(name = "text_color", length = 7)
    private String textColor;

    @Column(name = "text_secondary_color", length = 7)
    private String textSecondaryColor;

    @Column(name = "border_color", length = 7)
    private String borderColor;

    @Column(name = "success_color", length = 7)
    private String successColor;

    @Column(name = "warning_color", length = 7)
    private String warningColor;

    @Column(name = "error_color", length = 7)
    private String errorColor;

    @Column(name = "is_dark", nullable = false)
    private Boolean isDark;

    @Column(name = "is_high_contrast", nullable = false)
    private Boolean isHighContrast;

    @Column(name = "is_system_theme", nullable = false)
    private Boolean isSystemTheme;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}