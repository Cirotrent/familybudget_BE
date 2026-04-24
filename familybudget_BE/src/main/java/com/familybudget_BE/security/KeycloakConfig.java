package com.familybudget_BE.security;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("https://keycloak-server-4bwx.onrender.com")
//                .serverUrl("http://localhost:8080")
                .realm("family-budget")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("spring-client")
//                .clientSecret("c4oS373qLZHammP458TyNlm89bNE3cRP") //locale
                .clientSecret("5D851pNKrnLEWAI4bferZyFoyzJkahLt") //render
                .build();
    }
}
