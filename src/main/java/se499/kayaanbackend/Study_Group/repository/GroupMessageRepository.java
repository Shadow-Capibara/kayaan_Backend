package se499.kayaanbackend.Study_Group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Group.entity.GroupMessage;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
}
