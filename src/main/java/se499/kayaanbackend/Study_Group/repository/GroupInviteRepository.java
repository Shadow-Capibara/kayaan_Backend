package se499.kayaanbackend.Study_Group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Study_Group.GroupInvite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    
    @Query("SELECT gi FROM GroupInvite gi WHERE gi.token = :token AND gi.revoked = false AND gi.expiresAt > :now")
    Optional<GroupInvite> findValidByToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    @Query("SELECT gi FROM GroupInvite gi WHERE gi.groupId = :groupId AND gi.revoked = false AND gi.expiresAt > :now")
    List<GroupInvite> findValidByGroupId(@Param("groupId") Integer groupId, @Param("now") LocalDateTime now);
    
    @Query("SELECT gi FROM GroupInvite gi WHERE gi.groupId = :groupId")
    List<GroupInvite> findByGroupId(@Param("groupId") Integer groupId);
    
    @Query("SELECT COUNT(gi) > 0 FROM GroupInvite gi WHERE gi.groupId = :groupId AND gi.createdBy = :createdBy AND gi.revoked = false")
    boolean existsByGroupIdAndCreatedByAndRevokedFalse(@Param("groupId") Integer groupId, @Param("createdBy") Integer createdBy);
    
    /**
     * ค้นหารหัสเชิญตาม inviteCode
     */
    Optional<GroupInvite> findByInviteCode(String inviteCode);
}
