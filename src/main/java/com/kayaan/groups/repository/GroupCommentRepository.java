package com.kayaan.groups.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.groups.entity.GroupComment;

public interface GroupCommentRepository extends JpaRepository<GroupComment, Long> {
    List<GroupComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}


