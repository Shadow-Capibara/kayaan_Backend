package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(GroupContentId.class)
public class GroupContent {
    @Id
    private Integer groupID;
    @Id
    private Integer contentInfoID;

    private LocalDateTime shared_at;
}
