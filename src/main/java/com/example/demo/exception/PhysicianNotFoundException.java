package com.example.demo.exception;

public class PhysicianNotFoundException extends RuntimeException{

    public PhysicianNotFoundException(String message) {
        super(message);
    }

}
