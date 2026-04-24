package com.familybudget_BE.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        	.csrf(csrf -> csrf.disable())
	        	.authorizeHttpRequests(auth -> auth
	        			.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
	        		    .requestMatchers("/public/**").permitAll()
	        		    .requestMatchers("/api/admin/**").hasRole("ADMIN")
	        		    .requestMatchers("/api/**").hasRole("USER")
	        		    .anyRequest().authenticated()
	        		)
	        	.sessionManagement(session -> 
	            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        			)
	        	.oauth2ResourceServer(oauth2 -> oauth2
	        		    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
	        		    .authenticationEntryPoint((request, response, authException) -> {
	        		        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	        		    })
	        		);

	        return http.build();
	    }
	 
	 @Bean
	 public JwtAuthenticationConverter jwtAuthenticationConverter() {
	     JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	     grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

	     JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
	     jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
	         Collection<GrantedAuthority> authorities = new ArrayList<>();

	         Map<String, Object> realmAccess = jwt.getClaim("realm_access");
	         if (realmAccess != null && realmAccess.containsKey("roles")) {
	             List<String> roles = (List<String>) realmAccess.get("roles");
	             roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
	         }

	         return authorities;
	     });

	     return jwtConverter;
	 }
	 
//	 @Bean
//	    public CorsFilter corsFilter() {
//	        CorsConfiguration config = new CorsConfiguration();
//	        
//	        config.setAllowedOrigins(List.of("http://localhost:4200"));
//	        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//	        config.setAllowedHeaders(List.of("*"));
//	        config.setAllowCredentials(true);
//
//	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	        source.registerCorsConfiguration("/**", config);
//
//	        return new CorsFilter(source);
//	    }
	 
	 @Bean
	 public UrlBasedCorsConfigurationSource corsConfigurationSource() {
	     CorsConfiguration config = new CorsConfiguration();

	     config.setAllowedOrigins(List.of("http://localhost:4200","https://familybudget-fe.vercel.app"));
	     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	     config.setAllowedHeaders(List.of("*"));
	     config.setAllowCredentials(true);

	     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	     source.registerCorsConfiguration("/**", config);

	     return source;
	 }
}
