package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.security.JwtResponse;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* POST API gets username and password in the body. Using Spring Authentication Manager
* the username and password will be authenticated. If credentials are valid, a JWT token is created
* using the JwtTokenUtil and provided to the client
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest)
            throws Exception {

        // authenticate the user
        authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        // load user details from UserDetailService
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // generate JWT token
        String token = jwtTokenUtil.generateToken(userDetails);

        // return the token in the response
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate((new UsernamePasswordAuthenticationToken(username,password)));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

}
