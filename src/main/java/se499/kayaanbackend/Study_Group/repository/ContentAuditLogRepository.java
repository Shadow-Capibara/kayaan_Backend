package se499.kayaanbackend.Study_Group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Study_Group.security.ContentAuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository สำหรับจัดการ ContentAuditLog
 */
@Repository
public interface ContentAuditLogRepository extends JpaRepository<ContentAuditLog, Long> {
    
    /**
     * ค้นหาประวัติตาม contentId
     */
    List<ContentAuditLog> findByContentIdOrderByTimestampDesc(Long contentId);
    
    /**
     * ค้นหาประวัติตาม userId
     */
    List<ContentAuditLog> findByUserIdOrderByTimestampDesc(Long userId);
    
    /**
     * ค้นหาประวัติตาม groupId
     */
    List<ContentAuditLog> findByGroupIdOrderByTimestampDesc(Long groupId);
    
    /**
     * ค้นหาประวัติตาม action
     */
    List<ContentAuditLog> findByActionOrderByTimestampDesc(String action);
    
    /**
     * ค้นหาประวัติตามช่วงเวลา
     */
    List<ContentAuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
    
    /**
     * ค้นหาประวัติตาม userId และ groupId
     */
    List<ContentAuditLog> findByUserIdAndGroupIdOrderByTimestampDesc(Long userId, Long groupId);
    
    /**
     * ค้นหาประวัติตาม userId และ action
     */
    List<ContentAuditLog> findByUserIdAndActionOrderByTimestampDesc(Long userId, String action);
    
    /**
     * ค้นหาประวัติตาม groupId และ action
     */
    List<ContentAuditLog> findByGroupIdAndActionOrderByTimestampDesc(Long groupId, String action);
    
    /**
     * ค้นหาประวัติตาม userId, groupId และ action
     */
    List<ContentAuditLog> findByUserIdAndGroupIdAndActionOrderByTimestampDesc(Long userId, Long groupId, String action);
    
    /**
     * นับจำนวนการเข้าถึงเนื้อหาตาม contentId
     */
    @Query("SELECT COUNT(c) FROM ContentAuditLog c WHERE c.contentId = :contentId AND c.action = 'VIEW'")
    Long countViewsByContentId(@Param("contentId") Long contentId);
    
    /**
     * นับจำนวนการเข้าถึงเนื้อหาตาม groupId
     */
    @Query("SELECT COUNT(c) FROM ContentAuditLog c WHERE c.groupId = :groupId AND c.action = 'VIEW'")
    Long countViewsByGroupId(@Param("groupId") Long groupId);
    
    /**
     * นับจำนวนการเข้าถึงเนื้อหาตาม userId
     */
    @Query("SELECT COUNT(c) FROM ContentAuditLog c WHERE c.userId = :userId AND c.action = 'VIEW'")
    Long countViewsByUserId(@Param("userId") Long userId);

    /**
     * ค้นหาประวัติตามกลุ่มและการกระทำ
     */
    List<ContentAuditLog> findByGroupIdAndActionIn(Integer groupId, List<String> actions);

    /**
     * ค้นหาประวัติตามกลุ่ม ผู้ใช้ การกระทำ และช่วงเวลา
     */
    List<ContentAuditLog> findByGroupIdAndUserIdAndActionInAndTimestampBetween(
        Integer groupId, Integer userId, List<String> actions, 
        LocalDateTime startDate, LocalDateTime endDate);

    /**
     * ค้นหาประวัติตามกลุ่ม การกระทำ และช่วงเวลา
     */
    List<ContentAuditLog> findByGroupIdAndActionInAndTimestampBetween(
        Integer groupId, List<String> actions, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * ค้นหาประวัติล่าสุดตามกลุ่ม
     */
    @Query("SELECT c FROM ContentAuditLog c WHERE c.groupId = :groupId AND c.timestamp >= :startDate")
    List<ContentAuditLog> findRecentByGroupId(@Param("groupId") Integer groupId, 
                                             @Param("startDate") LocalDateTime startDate);

    /**
     * ค้นหาประวัติตามผู้ใช้และการกระทำ
     */
    @Query("SELECT c FROM ContentAuditLog c WHERE c.userId = :userId AND c.action = :action")
    List<ContentAuditLog> findByUserIdAndAction(@Param("userId") Integer userId, 
                                               @Param("action") String action);
}
