package com.familybudget_BE.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.familybudget_BE.dto.TransactionRequestDTO;
import com.familybudget_BE.dto.TransactionResponseDTO;
import com.familybudget_BE.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	  private final TransactionService service;

	    public TransactionController(TransactionService service) {
	        this.service = service;
	    }

	    @PostMapping
	    @PreAuthorize("hasRole('USER')")
	    public TransactionResponseDTO create(@Valid @RequestBody TransactionRequestDTO dto) {
	        return service.save(dto);
	    }

	    @GetMapping
	    @PreAuthorize("hasRole('USER')")
	    public List<TransactionResponseDTO> getAll(
	            @RequestParam(required = false) String type,
	            @RequestParam(required = false) LocalDate startDate,
	            @RequestParam(required = false) LocalDate endDate) {

	        return service.findAll(type, startDate, endDate);
	    }

	    @DeleteMapping("/{id}")
	    @PreAuthorize("hasRole('USER')")
	    public void delete(@PathVariable Long id) {
	        service.delete(id);
	    }
}
