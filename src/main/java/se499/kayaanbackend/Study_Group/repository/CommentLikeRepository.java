package se499.kayaanbackend.Study_Group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.CommentLike;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, String> {
    
    // Check if user has liked a comment
    boolean existsByCommentIdAndUserId(String commentId, Integer userId);
    
    // Find like by comment ID and user ID
    Optional<CommentLike> findByCommentIdAndUserId(String commentId, Integer userId);
    
    // Count likes by comment ID
    long countByCommentId(String commentId);
    
    // Delete like by comment ID and user ID
    void deleteByCommentIdAndUserId(String commentId, Integer userId);
    
    // Delete all likes for a comment
    void deleteByCommentId(String commentId);
    
    // Find likes by user ID
    @Query("SELECT cl FROM CommentLike cl LEFT JOIN FETCH cl.comment WHERE cl.user.id = :userId")
    java.util.List<CommentLike> findByUserIdWithComment(@Param("userId") Integer userId);
}
