package se499.kayaanbackend.AI_Generate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.AI_Generate.entity.AIDraft;

@Repository
public interface AIDraftRepository extends JpaRepository<AIDraft, Long> {
}
