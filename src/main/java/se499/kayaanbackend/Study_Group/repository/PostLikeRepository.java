package se499.kayaanbackend.Study_Group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {
    
    // Check if user has liked a post
    boolean existsByPostIdAndUserId(String postId, Integer userId);
    
    // Find like by post ID and user ID
    Optional<PostLike> findByPostIdAndUserId(String postId, Integer userId);
    
    // Count likes by post ID
    long countByPostId(String postId);
    
    // Delete like by post ID and user ID
    void deleteByPostIdAndUserId(String postId, Integer userId);
    
    // Delete all likes for a post
    void deleteByPostId(String postId);
    
    // Find likes by user ID
    @Query("SELECT pl FROM PostLike pl LEFT JOIN FETCH pl.post WHERE pl.user.id = :userId")
    java.util.List<PostLike> findByUserIdWithPost(@Param("userId") Integer userId);
}
