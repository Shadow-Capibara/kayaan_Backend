package com.kayaan.groups.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.groups.entity.GroupPost;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    List<GroupPost> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}


