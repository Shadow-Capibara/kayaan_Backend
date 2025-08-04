package se499.kayaanbackend.Study_Group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    @Query("SELECT g FROM Group g WHERE g.isActive = true AND g.isPublic = true")
    List<Group> findAllActivePublicGroups();
    
    @Query("SELECT g FROM Group g WHERE g.createdByUsername = :username AND g.isActive = true")
    List<Group> findByCreatedByUsername(@Param("username") String username);
    
    @Query("SELECT g FROM Group g WHERE g.subject = :subject AND g.isActive = true AND g.isPublic = true")
    List<Group> findBySubject(@Param("subject") String subject);
    
    @Query("SELECT g FROM Group g WHERE g.category = :category AND g.isActive = true AND g.isPublic = true")
    List<Group> findByCategory(@Param("category") String category);
    
    @Query("SELECT g FROM Group g WHERE g.difficulty = :difficulty AND g.isActive = true AND g.isPublic = true")
    List<Group> findByDifficulty(@Param("difficulty") String difficulty);
    
    Optional<Group> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:keyword% OR g.description LIKE %:keyword% AND g.isActive = true AND g.isPublic = true")
    List<Group> searchByKeyword(@Param("keyword") String keyword);
} 