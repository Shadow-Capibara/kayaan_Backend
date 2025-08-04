package se499.kayaanbackend.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.common.entity.ContentInformation;

@Repository
public interface ContentInformationRepository extends JpaRepository<ContentInformation, Long> {
    
    @Query("SELECT ci FROM ContentInformation ci WHERE ci.isActive = true")
    List<ContentInformation> findAllActive();
    
    @Query("SELECT ci FROM ContentInformation ci WHERE ci.contentType = :contentType AND ci.isActive = true")
    List<ContentInformation> findByContentType(@Param("contentType") String contentType);
    
    @Query("SELECT ci FROM ContentInformation ci WHERE ci.createdByUsername = :username AND ci.isActive = true")
    List<ContentInformation> findByCreatedByUsername(@Param("username") String username);
    
    @Query("SELECT ci FROM ContentInformation ci WHERE ci.subject = :subject AND ci.isActive = true")
    List<ContentInformation> findBySubject(@Param("subject") String subject);
    
    @Query("SELECT ci FROM ContentInformation ci WHERE ci.category = :category AND ci.isActive = true")
    List<ContentInformation> findByCategory(@Param("category") String category);
    
    @Query("SELECT ci FROM ContentInformation ci WHERE ci.difficulty = :difficulty AND ci.isActive = true")
    List<ContentInformation> findByDifficulty(@Param("difficulty") String difficulty);
    
    Optional<ContentInformation> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT ci FROM ContentInformation ci WHERE (ci.title LIKE %:keyword% OR ci.description LIKE %:keyword%) AND ci.isActive = true")
    List<ContentInformation> searchByKeyword(@Param("keyword") String keyword);
} 