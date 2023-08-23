package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*

    Bean SecurityFilterChain to specify how security filters should be configured and applied
    to different parts of application.
    This bean is used to encapsulate a set of security filters that are responsible for various
    security-related tasks as:
    - Authentication: verifying the identity of user or client making a request
    - Authorization: deciding whether a user or client has permission to access a particular resource
    - CSRF protection: preventing Cross-Site Request Forgery attacks
    - Session Management: managing user sessions and handling session-related issues
    - Remember me Authentication: supporting remember me functionality
    - Logout handling: managing user logout and related actions

    Each SecurityFilterChain bean matchs to specific set of URL patterns and security requirements.
    Within SecurityFilterChain bean an object HttpSecurity can be used to define the security
    configuration for those URL patterns.

     */

    // bean defaultSecurityFilterChain defines how different URL patterns are authorized and handled
    // by Spring Security filters


    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        http.
                authorizeHttpRequests(authorizeHttpRequest -> //method to specify which request should be authorized
                        authorizeHttpRequest
                                .anyRequest().permitAll()
                )
                .csrf().disable();
        return http.build();
    }

}
