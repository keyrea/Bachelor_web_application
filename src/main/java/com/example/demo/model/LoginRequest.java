package com.example.demo.model;

import lombok.Data;

import java.io.Serializable;

/*
* 'LoginRequest' class is used to encapsulate the credentials (username and password) that a client
* (such as front-end or Postman) sends to server during the authentication process. It serves as a
* container to organize and structure the data that is being sent in the HTTP request body.
*
* When user tries to log in, they provide their username and password. The 'LoginRequest' class is used
* to map these credentials from HTTP request to Java objects that backend code can work with it.
 */
@Data
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;
    private Long organizationId;

}
