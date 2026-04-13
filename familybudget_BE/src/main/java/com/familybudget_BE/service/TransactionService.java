package com.familybudget_BE.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.familybudget_BE.dto.MonthlyReportDTO;
import com.familybudget_BE.dto.TransactionRequestDTO;
import com.familybudget_BE.dto.TransactionResponseDTO;
import com.familybudget_BE.entity.Category;
import com.familybudget_BE.entity.Family;
import com.familybudget_BE.entity.Transaction;
import com.familybudget_BE.entity.User;
import com.familybudget_BE.repository.CategoryRepository;
import com.familybudget_BE.repository.FamilyMemberRepository;
import com.familybudget_BE.repository.FamilyRepository;
import com.familybudget_BE.repository.TransactionRepository;
import com.familybudget_BE.repository.UserRepository;
import com.familybudget_BE.security.SecurityUtils;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final SecurityUtils securityUtils;

    public TransactionService(TransactionRepository repository,
	            UserRepository userRepository,
	            CategoryRepository categoryRepository,
	            FamilyRepository familyRepository,
	            FamilyMemberRepository familyMemberRepository,
	            SecurityUtils securityUtils) {
	this.repository = repository;
	this.userRepository = userRepository;
	this.categoryRepository = categoryRepository;
	this.familyRepository = familyRepository;
	this.familyMemberRepository = familyMemberRepository;
	this.securityUtils = securityUtils;
	}

    public TransactionResponseDTO save(TransactionRequestDTO dto) {

        String username = securityUtils.getCurrentUsername();

        if (!familyMemberRepository.findByFamilyIdAndUserUsername(dto.getFamilyId(), username).isPresent()) {
            throw new RuntimeException("Not member of family");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Family family = familyRepository.findById(dto.getFamilyId())
                .orElseThrow(() -> new RuntimeException("Family not found"));

        Transaction t = Transaction.builder()
                .amount(dto.getAmount())
                .type(Transaction.Type.valueOf(dto.getType()))
                .date(dto.getDate())
                .description(dto.getDescription())
                .category(category)
                .family(family)
                .user(user)
                .build();

        return mapToDTO(repository.save(t));
    }
    
    public MonthlyReportDTO report(Long familyId, int year, int month) {

        String username = securityUtils.getCurrentUsername();

        if (!familyMemberRepository.findByFamilyIdAndUserUsername(familyId, username).isPresent()) {
            throw new RuntimeException("Not authorized");
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> tx = repository.findAll().stream()
                .filter(t -> t.getFamily().getId().equals(familyId))
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .toList();

        BigDecimal income = tx.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = tx.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MonthlyReportDTO.builder()
                .month(year + "-" + month)
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income.subtract(expense))
                .build();
    }



//    public List<TransactionResponseDTO> findAll(String type, LocalDate startDate, LocalDate endDate) {
//
//        String username = securityUtils.getCurrentUsername();
//
//        List<Transaction> transactions;
//
//        if (startDate != null && endDate != null) {
//            transactions = repository.findByUserUsernameAndDateBetween(username, startDate, endDate);
//        } else if (type != null) {
//            transactions = repository.findByUserUsernameAndType(username, Transaction.Type.valueOf(type));
//        } else {
//            transactions = repository.findByUserUsername(username);
//        }
//
//        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
//    }



	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    
	  	    
	    private TransactionResponseDTO mapToDTO(Transaction t) {
	        return TransactionResponseDTO.builder()
	                .id(t.getId())
	                .amount(t.getAmount())
	                .type(t.getType().name())
	                .date(t.getDate())
	                .description(t.getDescription())
	                .username(t.getUser().getUsername())
	                .categoryName(t.getCategory().getName())
	                .build();
	    }
}
