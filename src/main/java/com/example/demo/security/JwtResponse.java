package com.example.demo.security;
import java.io.Serializable;

//  this class is required for creating a response containing the JWT to be returned to the User

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

}
