package com.kayaan.groups.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kayaan.groups.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMember.GroupMemberId> {
    long countByIdGroupId(Long groupId);
    boolean existsByIdGroupIdAndIdUserId(Long groupId, Long userId);
    List<GroupMember> findByIdGroupId(Long groupId);
}


