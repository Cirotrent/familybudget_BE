package com.familybudget_BE.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.familybudget_BE.service.FamilyService;
import com.familybudget_BE.service.FamilyService.CategoryDTO;
import com.familybudget_BE.service.FamilyService.FamilyDTO;

@RestController
@RequestMapping("/api/families")
public class FamilyController {

    private final FamilyService service;

    public FamilyController(FamilyService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public String create(@RequestParam String name) {
        service.createFamily(name);
        return "Family created";
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('USER')")
    public String addMember(@PathVariable Long id, @RequestParam String username) {
        service.addMember(id, username);
        return "Member added";
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public List<FamilyDTO> myFamilies() {
        return service.getFamiliesByUsername();
    }
    
    @GetMapping("/allCategory")
    @PreAuthorize("hasRole('USER')")
    public List<CategoryDTO> getAllCategory() {
        return service.getAllCategory();
    }
}

