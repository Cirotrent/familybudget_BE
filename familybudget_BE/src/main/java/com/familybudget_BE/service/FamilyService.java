package com.familybudget_BE.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.familybudget_BE.entity.Family;
import com.familybudget_BE.entity.FamilyMember;
import com.familybudget_BE.entity.User;
import com.familybudget_BE.exceptions.ForbiddenException;
import com.familybudget_BE.exceptions.NotFoundException;
import com.familybudget_BE.repository.CategoryRepository;
import com.familybudget_BE.repository.FamilyMemberRepository;
import com.familybudget_BE.repository.FamilyRepository;
import com.familybudget_BE.repository.UserRepository;
import com.familybudget_BE.security.SecurityUtils;

@Service
public class FamilyService {
	
	 public record CategoryDTO(Long id, String name) {}
	 public record FamilyDTO(Long id, String name) {}

	private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final UserService userService;
    private final KeycloakUserService keycloakUserService;

    public FamilyService(FamilyRepository familyRepository,
                         FamilyMemberRepository familyMemberRepository,
                         CategoryRepository categoryRepository,
                         UserRepository userRepository,
                         SecurityUtils securityUtils,
                         UserService userService,
                         KeycloakUserService keycloakUserService) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.keycloakUserService = keycloakUserService;
    }

    public Family createFamily(String name) {

        String username = securityUtils.getCurrentUsername();

        User user = userService.getOrCreateCurrentUser();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));

        Family family = familyRepository.save(Family.builder().name(name).build());

        familyMemberRepository.save(FamilyMember.builder()
                .family(family)
                .user(user)
                .role(FamilyMember.Role.OWNER)
                .build());

        return family;
    }

    public void addMember(Long familyId, String username) {

        String currentUser = securityUtils.getCurrentUsername();
        
        if (!keycloakUserService.userExists(username)) {
            throw new NotFoundException("User does not exist in Keycloak");
        }

        FamilyMember owner = familyMemberRepository
                .findByFamilyIdAndUserUsernameAndRole(familyId, currentUser, FamilyMember.Role.OWNER)
                .orElseThrow(() -> new ForbiddenException("Not authorized"));

        User user = userService.getOrCreateByUsername(username);
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new NotFoundException("Family not found"));

        familyMemberRepository.save(FamilyMember.builder()
                .family(family)
                .user(user)
                .role(FamilyMember.Role.MEMBER)
                .build());
    }

    public boolean isMember(Long familyId, String username) {
        return familyMemberRepository.findByFamilyIdAndUserUsername(familyId, username).isPresent();
    }
    
    public List<CategoryDTO> getAllCategory() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName()))
                .toList();
    }
    
    public List<FamilyDTO> getFamiliesByUsername() {
    	String username = securityUtils.getCurrentUsername();
    	
    	User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

    	return familyMemberRepository.findByUserUsername(username)
                .stream()
                .map(fm -> new FamilyDTO(
                        fm.getFamily().getId(),
                        fm.getFamily().getName()
                ))
                .toList();    }
    
   
}

