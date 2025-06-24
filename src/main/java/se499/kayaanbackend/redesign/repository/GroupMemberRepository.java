package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
}
