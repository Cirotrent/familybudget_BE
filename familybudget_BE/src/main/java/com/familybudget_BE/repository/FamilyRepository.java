package com.familybudget_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.familybudget_BE.entity.Family;
import com.familybudget_BE.entity.FamilyMember;

public interface FamilyRepository extends JpaRepository<Family, Long> {
	
	

}
