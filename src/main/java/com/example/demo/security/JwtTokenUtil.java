package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
* 'JwtTokenUtil' class is utility class that provides methods for working with JSON Web Tokens (JWT) in
* context of authentication and authorization. JWTs are standard for representing claims between two
* parties in a compact and self-contained-way.
*
* JwtTokenUtil class typically performs following tasks:
* - Generating JWTs:
*   -class provides methods to generate JWTs based on a set of claims (such as username, roles, expiration
*   time and etc.)
*   -these methods take user-specific information and encode it into a JWT using a specified signing key
* - Validating JWTs:
*   -offers methods to validate the authenticity and integrity of received JWTs
*   -class verifies the signature of JWT to ensure it has not been tampered with
* - Extracting Claims from JWTs:
*   -class provides methods to extract the claims (such as username, roles) from JWT
* - Handling JWT Expiry:
*   -class can determine whether a JWT has expired or still valid
*
* In context of authentication 'JwtTokenUtil' class is often used in combination with Spring Security
* filters and user detail services. After a user logs, a JWT is generated and provided to the user
* as part of the response.
 */

@Component
public class JwtTokenUtil implements Serializable {

    private static final Long serialVersionUID = -2550185165626007488L;

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private Long expiration;

    // retrieve username from JWT token
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // retrieve expiration date from JWT token
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // extract claims from JWT token using provided function
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // for retrieving any information from token, we will need a secret key
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // check if the token has expired
    private Boolean isTokenExpired(String token){
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    // generate token for user
    public String generateToken(UserDetails userDetails, Long organizationId){
        Map<String, Object> claims = new HashMap<>();
        if(organizationId != null){
            claims.put("organization_id",organizationId);
        }
        String generatedToken = createToken(claims, userDetails.getUsername());
        System.out.println("Generated token "+generatedToken);

        return generatedToken;
    }

    // create token using provided claims and subject (username)
    private String createToken(Map<String, Object> claims, String subject){

        System.out.println("Token Expiration Time: " + new Date(System.currentTimeMillis() + expiration));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }

    // validate token for user
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Long extractOrganizationId(String token){
        final Claims claims = extractAllClaims(token);
        Object organizationId = claims.get("organization_id");
        return organizationId != null ? Long.valueOf(organizationId.toString()) : null;
    }

}
