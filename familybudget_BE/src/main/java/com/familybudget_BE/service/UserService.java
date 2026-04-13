package com.familybudget_BE.service;

import org.springframework.stereotype.Service;

import com.familybudget_BE.entity.User;
import com.familybudget_BE.repository.UserRepository;
import com.familybudget_BE.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public UserService(UserRepository userRepository,
                       SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Recupera l'utente corrente dal DB oppure lo crea automaticamente
     */
    @Transactional
    public User getOrCreateCurrentUser() {

        String username = securityUtils.getCurrentUsername();
        String email = securityUtils.getCurrentUserEmail(); // assicurati di averlo

        return userRepository.findByUsername(username)
                .orElseGet(() -> createUser(username, email));
    }

    /**
     * Usato quando aggiungi membri alla famiglia
     */
    @Transactional
    public User getOrCreateByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseGet(() -> createUser(username, null));
    }

    private User createUser(String username, String email) {

        User user = User.builder()
                .username(username)
                .email(email)
                .build();

        return userRepository.save(user);
    }
}
