package se499.kayaanbackend.Study_Group.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.GroupContent;

@Repository
public interface GroupContentRepository extends JpaRepository<GroupContent, Long> {
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.groupId = :groupId ORDER BY gc.createdAt DESC")
    List<GroupContent> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Integer groupId);
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.groupId = :groupId ORDER BY gc.createdAt DESC")
    List<GroupContent> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Integer groupId, Pageable pageable);
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.uploaderId = :uploaderId")
    List<GroupContent> findByUploaderId(@Param("uploaderId") Integer uploaderId);
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.groupId = :groupId AND " +
           "(LOWER(gc.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(gc.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(gc.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY gc.createdAt DESC")
    List<GroupContent> findByGroupIdAndSearchTerm(@Param("groupId") Integer groupId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT gc FROM GroupContent gc WHERE gc.groupId = :groupId AND gc.mimeType LIKE %:mimeType% ORDER BY gc.createdAt DESC")
    List<GroupContent> findByGroupIdAndMimeTypeContaining(@Param("groupId") Integer groupId, @Param("mimeType") String mimeType);
}
