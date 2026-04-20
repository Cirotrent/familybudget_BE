package com.familybudget_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.familybudget_BE.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	 List<Category> findByFamilyId(Long familyId);

	 List<Category> findByFamilyIdAndType(Long familyId, Category.Type type);
}
