package com.familybudget_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.familybudget_BE.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
