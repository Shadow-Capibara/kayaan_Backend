package se499.kayaanbackend.Study_Group.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.GroupMessage;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    
    @Query("SELECT gm FROM GroupMessage gm WHERE gm.studyGroup.id = :groupId ORDER BY gm.createdAt DESC")
    List<GroupMessage> findByStudyGroupIdOrderByCreatedAtDesc(@Param("groupId") Integer groupId, Pageable pageable);
    
    List<GroupMessage> findByStudyGroupIdOrderByCreatedAtDesc(Integer groupId);
}
