package se499.kayaanbackend.Study_Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.GroupPost;

@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, String> {
    
    // Find posts by group ID with pagination, ordered by created date (newest first)
    Page<GroupPost> findByGroupIdOrderByCreatedAtDesc(Integer groupId, Pageable pageable);
    
    @Query("SELECT p FROM GroupPost p " +
           "LEFT JOIN FETCH p.author " +
           "LEFT JOIN FETCH p.group " +
           "WHERE p.group.id = :groupId " +
           "ORDER BY p.createdAt DESC")
    Page<GroupPost> findByGroupIdWithDetailsOrderByCreatedAtDesc(@Param("groupId") Integer groupId, Pageable pageable);
    
    // Find pinned posts by group ID
    List<GroupPost> findByGroupIdAndIsPinnedTrueOrderByCreatedAtDesc(Integer groupId);
    
    // Find posts by author ID
    Page<GroupPost> findByAuthorIdOrderByCreatedAtDesc(Integer authorId, Pageable pageable);
    
    // Find posts by group ID and author ID
    Page<GroupPost> findByGroupIdAndAuthorIdOrderByCreatedAtDesc(Integer groupId, Integer authorId, Pageable pageable);
    
    // Find posts by content type
    Page<GroupPost> findByGroupIdAndContentTypeOrderByCreatedAtDesc(Integer groupId, GroupPost.ContentType contentType, Pageable pageable);
    
    // Search posts by title or content
    @Query("SELECT p FROM GroupPost p WHERE p.group.id = :groupId AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<GroupPost> searchPostsByGroupId(@Param("groupId") Integer groupId, 
                                        @Param("searchTerm") String searchTerm, 
                                        Pageable pageable);
    
    // Count posts by group ID
    long countByGroupId(Integer groupId);
    
    // Count posts by author ID
    long countByAuthorId(Integer authorId);
    
    // Find posts with attachments
    @Query("SELECT p FROM GroupPost p WHERE p.id = :postId")
    Optional<GroupPost> findByIdWithAttachments(@Param("postId") String postId);
    
    // Find posts with comments
    @Query("SELECT p FROM GroupPost p WHERE p.id = :postId")
    Optional<GroupPost> findByIdWithComments(@Param("postId") String postId);
}
