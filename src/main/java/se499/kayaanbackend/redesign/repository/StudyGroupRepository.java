package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Study_Group.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {
}
