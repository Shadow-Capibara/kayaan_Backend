package se499.kayaanbackend.Study_Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.PostAttachment;

@Repository
public interface PostAttachmentRepository extends JpaRepository<PostAttachment, String> {
    
    // Find attachments by post ID
    List<PostAttachment> findByPostIdOrderByCreatedAtAsc(String postId);
    
    // Find attachment by ID with post data
    @Query("SELECT a FROM PostAttachment a LEFT JOIN FETCH a.post WHERE a.id = :attachmentId")
    Optional<PostAttachment> findByIdWithPost(@Param("attachmentId") String attachmentId);
    
    // Count attachments by post ID
    long countByPostId(String postId);
    
    // Find attachments by file type
    List<PostAttachment> findByPostIdAndFileTypeOrderByCreatedAtAsc(String postId, String fileType);
    
    // Delete attachments by post ID
    void deleteByPostId(String postId);
}
