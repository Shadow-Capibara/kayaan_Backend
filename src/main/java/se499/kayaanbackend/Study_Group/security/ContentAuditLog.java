package se499.kayaanbackend.Study_Group.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity สำหรับเก็บประวัติการเข้าถึงและเปลี่ยนแปลงเนื้อหาในกลุ่มเรียน
 */
@Entity
@Table(name = "content_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "content_id", nullable = false)
    private Long contentId;
    
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, VIEW, DOWNLOAD
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // ข้อมูลเพิ่มเติม เช่น ข้อมูลที่เปลี่ยนแปลง
    
    @Column(name = "success", nullable = false)
    private Boolean success; // การดำเนินการสำเร็จหรือไม่
    
    @PrePersist
    void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (success == null) {
            success = true;
        }
    }
}
