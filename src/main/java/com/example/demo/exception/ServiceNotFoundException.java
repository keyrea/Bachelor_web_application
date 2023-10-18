package com.example.demo.exception;

public class ServiceNotFoundException extends RuntimeException{

    public ServiceNotFoundException(String message) {
        super(message);
    }

}
