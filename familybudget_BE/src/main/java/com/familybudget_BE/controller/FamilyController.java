package com.familybudget_BE.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.familybudget_BE.service.FamilyService;

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
}

