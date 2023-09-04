package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * purpose is to provide user information to the Spring Security framework, specifically during
 * authentication process
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /*
    * when user tries to log in, Spring Security needs to verify their credentials and load their
    * details for further processing. 'UserDetailsService' interface provides method
    * 'loadUserByUsername', that retrieves user information based on their username.
    * Retrieves user's details such as password, roles and other attributes. This information is
    * essential for Spring Security to perform an authentication and authorization tasks.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // retrieve the User entity from database by username
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username" + username);
        }

        // create a list to hold the user's authorities
        List<GrantedAuthority> authorities = new ArrayList<>();

        // add the user's role as an authority (prefixed with "ROLE_")
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // create and return a UserDetails object with the retrieved user's information
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
