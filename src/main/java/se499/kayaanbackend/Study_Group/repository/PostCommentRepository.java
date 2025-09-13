package se499.kayaanbackend.Study_Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {
    
    // Find comments by post ID, ordered by created date (oldest first for chronological order)
    List<PostComment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(String postId);
    
    // Find replies to a specific comment
    List<PostComment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);
    
    // Find comments by author ID
    Page<PostComment> findByAuthorIdOrderByCreatedAtDesc(Integer authorId, Pageable pageable);
    
    // Find comments by post ID with pagination
    Page<PostComment> findByPostIdOrderByCreatedAtAsc(String postId, Pageable pageable);
    
    // Count comments by post ID
    long countByPostId(String postId);
    
    // Count replies by parent comment ID
    long countByParentCommentId(String parentCommentId);
    
    // Find comment with author data
    @Query("SELECT c FROM PostComment c LEFT JOIN FETCH c.author WHERE c.id = :commentId")
    Optional<PostComment> findByIdWithAuthor(@Param("commentId") String commentId);
    
    // Find comments with replies
    @Query("SELECT c FROM PostComment c LEFT JOIN FETCH c.replies r LEFT JOIN FETCH r.author WHERE c.id = :commentId")
    Optional<PostComment> findByIdWithReplies(@Param("commentId") String commentId);
    
    // Find all comments for a post with author data
    @Query("SELECT c FROM PostComment c LEFT JOIN FETCH c.author WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<PostComment> findByPostIdWithAuthor(@Param("postId") String postId);
    
    // Delete comments by post ID
    void deleteByPostId(String postId);
    
    // Delete replies by parent comment ID
    void deleteByParentCommentId(String parentCommentId);
}
