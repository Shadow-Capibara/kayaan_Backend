package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(GroupMemberId.class)
public class GroupMember {
    @Id
    private Integer groupID;
    @Id
    private Integer userID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.member;

    private LocalDateTime joined_at;

    public enum Role { member, admin }
}
