package com.familybudget_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.familybudget_BE.entity.FamilyMember;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
	List<FamilyMember> findByFamilyId(Long familyId);
    Optional<FamilyMember> findByFamilyIdAndUserUsername(Long familyId, String username);
    Optional<FamilyMember> findByFamilyIdAndUserUsernameAndRole(Long familyId, String username, FamilyMember.Role role);
}
