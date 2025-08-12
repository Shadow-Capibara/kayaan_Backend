package se499.kayaanbackend.Study_Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.GroupMemberId;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.groupId = :groupId")
    List<GroupMember> findByGroupId(@Param("groupId") Integer groupId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.userId = :userId")
    List<GroupMember> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.userId = :userId")
    Optional<GroupMember> findByGroupIdAndUserId(@Param("groupId") Integer groupId, @Param("userId") Integer userId);
    
    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.userId = :userId")
    boolean existsByGroupIdAndUserId(@Param("groupId") Integer groupId, @Param("userId") Integer userId);
}
