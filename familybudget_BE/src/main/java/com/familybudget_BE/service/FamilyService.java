package com.familybudget_BE.service;

import org.springframework.stereotype.Service;

import com.familybudget_BE.entity.Family;
import com.familybudget_BE.entity.FamilyMember;
import com.familybudget_BE.entity.User;
import com.familybudget_BE.repository.FamilyMemberRepository;
import com.familybudget_BE.repository.FamilyRepository;
import com.familybudget_BE.repository.UserRepository;
import com.familybudget_BE.security.SecurityUtils;

@Service
public class FamilyService {

	private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final SecurityUtils securityUtils;
    private final UserService userService;
    private final KeycloakUserService keycloakUserService;

    public FamilyService(FamilyRepository familyRepository,
                         FamilyMemberRepository familyMemberRepository,
                         SecurityUtils securityUtils,
                         UserService userService,
                         KeycloakUserService keycloakUserService) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
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
            throw new RuntimeException("User does not exist in Keycloak");
        }

        FamilyMember owner = familyMemberRepository
                .findByFamilyIdAndUserUsernameAndRole(familyId, currentUser, FamilyMember.Role.OWNER)
                .orElseThrow(() -> new RuntimeException("Not authorized"));

        User user = userService.getOrCreateByUsername(username);
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));

        familyMemberRepository.save(FamilyMember.builder()
                .family(family)
                .user(user)
                .role(FamilyMember.Role.MEMBER)
                .build());
    }

    public boolean isMember(Long familyId, String username) {
        return familyMemberRepository.findByFamilyIdAndUserUsername(familyId, username).isPresent();
    }
}
