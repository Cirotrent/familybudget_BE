package com.familybudget_BE.service;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public boolean userExists(String username) {

        List<UserRepresentation> users = keycloak
                .realm("family-budget")
                .users()
                .search(username, true);

        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }
}
