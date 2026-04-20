package com.familybudget_BE.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.familybudget_BE.entity.Category;
import com.familybudget_BE.entity.Family;
import com.familybudget_BE.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Category create(String name, Category.Type type, Long familyId) {
        Category c = Category.builder()
                .name(name)
                .type(type)
                .family(Family.builder().id(familyId).build())
                .build();

        return repo.save(c);
    }

    public List<Category> getByFamily(Long familyId) {
        return repo.findByFamilyId(familyId);
    }

    public List<Category> getByFamilyAndType(Long familyId, Category.Type type) {
        return repo.findByFamilyIdAndType(familyId, type);
    }

    public Category update(Long id, String name, Category.Type type) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        c.setName(name);
        c.setType(type);

        return repo.save(c);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}