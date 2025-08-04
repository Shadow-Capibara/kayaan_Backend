package se499.kayaanbackend.Study_Group;

import jakarta.persistence.*;
import lombok.*;
import se499.kayaanbackend.redesign.entity.UserNew;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerUserID", nullable = false)
    private UserNew owner;

    @Column(nullable = false)
    private String name;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}
