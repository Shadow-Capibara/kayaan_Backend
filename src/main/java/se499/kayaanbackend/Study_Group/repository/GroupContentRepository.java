package se499.kayaanbackend.Study_Group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.GroupContent;

@Repository
public interface GroupContentRepository extends JpaRepository<GroupContent, Long> {
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.groupId = :groupId ORDER BY gc.createdAt DESC")
    List<GroupContent> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Integer groupId);
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.uploaderId = :uploaderId")
    List<GroupContent> findByUploaderId(@Param("uploaderId") Integer uploaderId);
}
