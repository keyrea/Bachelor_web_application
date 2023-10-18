package com.example.demo.exception;

public class MissingRequiredFieldsException extends RuntimeException{

    public MissingRequiredFieldsException(String message) {
        super(message);
    }

}
