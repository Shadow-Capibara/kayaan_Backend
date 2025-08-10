package se499.kayaanbackend.Study_Group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.StudyGroup;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {
}
