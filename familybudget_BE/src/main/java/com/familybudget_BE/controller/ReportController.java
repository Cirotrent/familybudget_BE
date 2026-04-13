package com.familybudget_BE.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.familybudget_BE.dto.MonthlyReportDTO;
import com.familybudget_BE.service.TransactionService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final TransactionService service;

    public ReportController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/monthly")
    public MonthlyReportDTO report(@RequestParam Long familyId,
                                   @RequestParam int year,
                                   @RequestParam int month) {
        return service.report(familyId, year, month);
    }
}
