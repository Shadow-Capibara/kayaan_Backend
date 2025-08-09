package se499.kayaanbackend.Manual_Generate.contentInfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentInfoRepository extends JpaRepository<ContentInfo, Long> {
    
    @Query("SELECT ci FROM ContentInfo ci WHERE ci.createdBy.username = :username AND ci.contentType = :contentType AND ci.isActive = true")
    List<ContentInfo> findByCreatedByUsernameAndContentType(@Param("username") String username, @Param("contentType") ContentInfo.ContentType contentType);
    
    @Query("SELECT ci FROM ContentInfo ci WHERE ci.createdBy.username = :username AND ci.isActive = true")
    List<ContentInfo> findByCreatedByUsername(@Param("username") String username);

    @Query("SELECT ci FROM ContentInfo ci WHERE ci.createdBy.username = :username " +
            "AND ci.contentType = :contentType AND ci.subject = :subject AND ci.isActive = true")
    List<ContentInfo> findByUsernameTypeAndSubject(@Param("username") String username,
                                                   @Param("contentType") ContentInfo.ContentType contentType,
                                                   @Param("subject") String subject);

    @Query("SELECT ci FROM ContentInfo ci WHERE ci.contentId = :contentId AND ci.createdBy.username = :username AND ci.isActive = true")
    Optional<ContentInfo> findByContentIdAndCreatedByUsername(@Param("contentId") Long contentId, @Param("username") String username);
}
