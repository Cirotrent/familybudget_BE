package com.familybudget_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.familybudget_BE.entity.Family;

public interface FamilyRepository extends JpaRepository<Family, Long> {

}
