package se499.kayaanbackend.Manual_Generate.contentInfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;


import java.util.List;
import java.util.Optional;

public interface ContentInfoRepository extends JpaRepository<ContentInfo, Long> {
    List<ContentInfo> findByUserCreatedAtUsernameAndContentType(String username, String contentType);
    List<ContentInfo> findByUserCreatedAtUsername(String username);

    @Query("SELECT ci FROM ContentInfo ci WHERE ci.userCreatedAt.username = :username " +
            "AND ci.contentType = :contentType AND ci.contentSubject = :subject")
    List<ContentInfo> findByUsernameTypeAndSubject(@Param("username") String username,
                                                   @Param("contentType") String contentType,
                                                   @Param("subject") String subject);

    Optional<ContentInfo> findByContentIdAndUserCreatedAtUsername(Long contentId, String username);
}
