package com.familybudget_BE.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.familybudget_BE.dto.CategoryRequestDTO;
import com.familybudget_BE.entity.Category;
import com.familybudget_BE.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('USER')")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public Category create(@RequestBody CategoryRequestDTO dto) {
        return service.create(dto.getName(), dto.getType(), dto.getFamilyId());
    }

    @GetMapping
    public List<Category> getAll(@RequestParam Long familyId,
                                @RequestParam(required = false) Category.Type type) {

        if (type != null) {
            return service.getByFamilyAndType(familyId, type);
        }

        return service.getByFamily(familyId);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id,
                           @RequestBody CategoryRequestDTO dto) {
        return service.update(id, dto.getName(), dto.getType());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
