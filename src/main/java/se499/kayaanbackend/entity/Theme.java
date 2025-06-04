package se499.kayaanbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import se499.kayaanbackend.security.user.User;

@Entity
public class Theme {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String primaryColor;
    private String secondaryColor;
    private boolean isDark;
    private boolean isHighContrast;

    @ManyToOne(optional = true)
    private User createdBy;

}
