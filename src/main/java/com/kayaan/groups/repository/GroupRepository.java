package com.kayaan.groups.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.groups.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByInviteCode(String inviteCode);
}


