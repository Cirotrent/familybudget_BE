package com.familybudget_BE.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.familybudget_BE.dto.MonthlyReportDTO;
import com.familybudget_BE.dto.TransactionRequestDTO;
import com.familybudget_BE.dto.TransactionResponseDTO;
import com.familybudget_BE.entity.Category;
import com.familybudget_BE.entity.Family;
import com.familybudget_BE.entity.Transaction;
import com.familybudget_BE.entity.User;
import com.familybudget_BE.exceptions.ForbiddenException;
import com.familybudget_BE.exceptions.NotFoundException;
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
            throw new NotFoundException("Not member of family");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Family family = familyRepository.findById(dto.getFamilyId())
                .orElseThrow(() -> new NotFoundException("Family not found"));

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
            throw new ForbiddenException("Not authorized");
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



//    public List<TransactionResponseDTO> findAll(String type, LocalDate startDate, LocalDate endDate, Long categoryId) {
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
    
    public Page<TransactionResponseDTO> findAll(
            String type,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long familyId,
            Pageable pageable
            
    ) {

        String username = securityUtils.getCurrentUsername();
        
        Specification<Transaction> spec = (root, query, cb) -> cb.equal(
                root.get("user").get("username"), username
        );

        if (type != null) {
            spec = spec.and(byType(type));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(byDateBetween(startDate, endDate));
        }

        if (categoryId != null) {
            spec = spec.and(byCategory(categoryId));
        }

        if (familyId != null) {
            spec = spec.and(byFamily(familyId));
        }

        Page<Transaction> transactions = repository.findAll(spec,pageable);

        return transactions.map(this::mapToDTO);
    }



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
	                .familyName(t.getFamily().getName())
	                .build();
	    }
	    
	    private Specification<Transaction> byUsername(String username) {
	        return (root, query, cb) ->
	                cb.equal(root.get("user").get("username"), username);
	    }

	    private Specification<Transaction> byType(String type) {
	        return (root, query, cb) ->
	                cb.equal(root.get("type"), Transaction.Type.valueOf(type));
	    }

	    private Specification<Transaction> byDateBetween(LocalDate start, LocalDate end) {
	        return (root, query, cb) ->
	                cb.between(root.get("date"), start, end);
	    }

	    private Specification<Transaction> byCategory(Long categoryId) {
	        return (root, query, cb) ->
	                cb.equal(root.get("category").get("id"), categoryId);
	    }

	    private Specification<Transaction> byFamily(Long familyId) {
	        return (root, query, cb) ->
	                cb.equal(root.get("family").get("id"), familyId);
	    }
}
