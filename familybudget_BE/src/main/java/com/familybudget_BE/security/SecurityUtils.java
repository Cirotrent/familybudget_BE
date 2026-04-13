package com.familybudget_BE.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

	public String getCurrentUsername() {

	    var auth = SecurityContextHolder.getContext().getAuthentication();

	    if (auth instanceof JwtAuthenticationToken jwtAuth) {
	        return jwtAuth.getToken().getClaim("preferred_username");
	    }

	    return auth.getName();
	}
	
	public String getCurrentUserEmail() {
	    var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
	    return auth.getToken().getClaim("email");
	}
}
